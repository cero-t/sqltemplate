package ninja.cero.sqltemplate;

import ninja.cero.sqltemplate.test.TestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Enum &lt;-&gt; string-column mapping across every parameter and mapper path.
 * {@code emp.job} is a VARCHAR column; enums are mapped by {@link Enum#name()} in both directions.
 * Covers the three entity shapes (record / public field / private field + accessor) plus
 * Map, positional and batch parameters, and the single-column mapper.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class EnumMappingTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    SqlTemplate sqlTemplate() {
        return new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate);
    }

    public enum Job {
        ANALYST, CLERK, MANAGER, PRESIDENT, SALESMAN
    }

    /** record entity -> RecordMapper */
    public record RecordEmp(Integer empno, Job job) {
    }

    /** public-field entity -> BeanMapper (public field path) */
    public static class PublicEmp {
        public Integer empno;
        public Job job;
        public Integer deptno;
    }

    /** private-field + accessor entity -> BeanMapper (bean/setter path) */
    public static class AccessorEmp {
        private Integer empno;
        private Job job;

        public Integer getEmpno() {
            return empno;
        }

        public void setEmpno(Integer empno) {
            this.empno = empno;
        }

        public Job getJob() {
            return job;
        }

        public void setJob(Job job) {
            this.job = job;
        }
    }

    // ---- READ: VARCHAR column -> enum (mapped by name()) ----

    @Test
    public void read_singleColumn() {
        Job job = sqlTemplate().query("select job from emp where empno = 7499").forObject(Job.class);
        assertSame(Job.SALESMAN, job);
    }

    @Test
    public void read_record() {
        RecordEmp emp = sqlTemplate().query("select empno, job from emp where empno = 7499").forObject(RecordEmp.class);
        assertSame(Job.SALESMAN, emp.job());
    }

    @Test
    public void read_publicField() {
        PublicEmp emp = sqlTemplate().query("select empno, job from emp where empno = 7499").forObject(PublicEmp.class);
        assertSame(Job.SALESMAN, emp.job);
    }

    @Test
    public void read_accessor() {
        AccessorEmp emp = sqlTemplate().query("select empno, job from emp where empno = 7499").forObject(AccessorEmp.class);
        assertSame(Job.SALESMAN, emp.getJob());
    }

    @Test
    public void read_list() {
        List<RecordEmp> list = sqlTemplate()
                .query("select empno, job from emp where deptno = 30 and job = 'SALESMAN'")
                .forList(RecordEmp.class);
        assertEquals(4, list.size());
        list.forEach(e -> assertSame(Job.SALESMAN, e.job()));
    }

    @Test
    public void read_null() {
        // Set job to NULL (rolled back by @Transactional), then read it back as an enum.
        Map<String, Object> param = new HashMap<>();
        param.put("job", null);
        param.put("mgr", 7698);
        param.put("empno", 7369);
        sqlTemplate().file("sql/updateByParam.sql").param(param).update();

        RecordEmp emp = sqlTemplate().query("select empno, job from emp where empno = 7369").forObject(RecordEmp.class);
        assertNull(emp.job());
    }

    @Test
    public void read_unknownName_throws() {
        // Write a string that is not a valid enum constant, then read it back as an enum.
        Map<String, Object> param = new HashMap<>();
        param.put("job", "NOPE");
        param.put("mgr", 7698);
        param.put("empno", 7369);
        sqlTemplate().file("sql/updateByParam.sql").param(param).update();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                sqlTemplate().query("select empno, job from emp where empno = 7369").forObject(RecordEmp.class));
        assertEquals("No enum constant " + Job.class.getCanonicalName() + ".NOPE", ex.getMessage());
    }

    // ---- WRITE: enum -> parameter (bound as name(), compared against VARCHAR column) ----

    @Test
    public void write_mapParam() {
        List<PublicEmp> list = sqlTemplate().file("sql/selectByParam.sql")
                .addParam("deptno", 30)
                .addParam("job", Job.SALESMAN)
                .forList(PublicEmp.class);
        assertEquals(4, list.size());
    }

    @Test
    public void write_beanParam() {
        PublicEmp param = new PublicEmp();
        param.deptno = 30;
        param.job = Job.SALESMAN;
        List<PublicEmp> list = sqlTemplate().file("sql/selectByParam.sql").param(param).forList(PublicEmp.class);
        assertEquals(4, list.size());
    }

    @Test
    public void write_positional_insertRoundTrip() {
        int count = sqlTemplate()
                .query("insert into emp (empno, ename, job, deptno) values (?, ?, ?, ?)")
                .params(1000, "TESTER", Job.MANAGER, 30)
                .update();
        assertEquals(1, count);

        RecordEmp emp = sqlTemplate().query("select empno, job from emp where empno = 1000").forObject(RecordEmp.class);
        assertSame(Job.MANAGER, emp.job());
    }

    @Test
    public void write_batch() {
        Map<String, Object> arg1 = new HashMap<>();
        arg1.put("empno", 7369);
        arg1.put("job", Job.CLERK);
        arg1.put("mgr", 7698);

        Map<String, Object> arg2 = new HashMap<>();
        arg2.put("empno", 7499);
        arg2.put("job", Job.ANALYST);
        arg2.put("mgr", 7902);

        int[] counts = sqlTemplate().batchUpdate().file("sql/updateByParam.sql")
                .addBatch(arg1)
                .addBatch(arg2)
                .execute();
        assertArrayEquals(new int[]{1, 1}, counts);

        RecordEmp e1 = sqlTemplate().query("select empno, job from emp where empno = 7369").forObject(RecordEmp.class);
        assertSame(Job.CLERK, e1.job());
        RecordEmp e2 = sqlTemplate().query("select empno, job from emp where empno = 7499").forObject(RecordEmp.class);
        assertSame(Job.ANALYST, e2.job());
    }
}
