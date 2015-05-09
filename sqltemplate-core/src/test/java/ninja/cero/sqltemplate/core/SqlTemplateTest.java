package ninja.cero.sqltemplate.core;

import ninja.cero.sqltemplate.test.TestConfig;
import ninja.cero.sqltemplate.test.entity.Emp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfig.class)
@Transactional
public class SqlTemplateTest {
    @Autowired
    SqlTemplate template;

    @Test
    public void testForObject_NoArgs() {
        Emp emp = template.forObject("sql/selectSingle.sql", Emp.class);
        assertThat(emp.empno, is(7369));
    }

    @Test
    public void testForObject_MapArg() {
        Map<String, Object> param = new HashMap<>();
        param.put("deptno", 30);
        param.put("job", "SALESMAN");

        Emp emp = template.forObject("sql/selectSingleByParam.sql", Emp.class, param);
        assertThat(emp.empno, is(7499));
    }

    @Test
    public void testForObject_EntityArg() {
        Emp param = new Emp();
        param.deptno = 30;
        param.job = "SALESMAN";

        Emp emp = template.forObject("sql/selectSingleByParam.sql", Emp.class, param);
        assertThat(emp.empno, is(7499));
    }

    @Test
    public void testForObject_SingleArg() {
        Emp emp = template.forObject("sql/selectByEmpno.sql", Emp.class, 7839);
        assertThat(emp.empno, is(7839));
    }

    @Test
    public void testForObject_MultiArg() {
        Emp emp = template.forObject("sql/selectSingleByArgs.sql", Emp.class, 30, "SALESMAN");
        assertThat(emp.empno, is(7499));
    }

    @Test
    public void testForObject_ReturnSimple() {
        Integer result = template.forObject("sql/selectSingleEmpno.sql", Integer.class);
        assertThat(result, is(7369));
    }

    @Test
    public void testForList_NoArg() {
        List<Emp> result = template.forList("sql/selectAll.sql", Emp.class);
        assertThat(result.size(), is(14));
        assertThat(result.get(0).empno, is(7369));
        assertThat(result.get(13).empno, is(7934));
    }

    @Test
    public void testForList_MapArg() {
        Map<String, Object> param = new HashMap<>();
        param.put("deptno", 30);
        param.put("job", "SALESMAN");

        List<Emp> result = template.forList("sql/selectByParam.sql", Emp.class, param);
        assertThat(result.size(), is(4));
        assertThat(result.get(0).empno, is(7499));
        assertThat(result.get(3).empno, is(7844));
    }

    @Test
    public void testForList_EntityArg() {
        Emp param = new Emp();
        param.deptno = 30;
        param.job = "SALESMAN";

        List<Emp> result = template.forList("sql/selectByParam.sql", Emp.class, param);
        assertThat(result.size(), is(4));
        assertThat(result.get(0).empno, is(7499));
        assertThat(result.get(3).empno, is(7844));
    }

    @Test
    public void testForList_SingleArg() {
        List<Emp> result = template.forList("sql/selectByDeptno.sql", Emp.class, 10);
        assertThat(result.size(), is(3));
        assertThat(result.get(0).empno, is(7782));
        assertThat(result.get(2).empno, is(7934));
    }

    @Test
    public void testForList_MultiArg() {
        List<Emp> result = template.forList("sql/selectByArgs.sql", Emp.class, 30, "SALESMAN");
        assertThat(result.size(), is(4));
        assertThat(result.get(0).empno, is(7499));
        assertThat(result.get(3).empno, is(7844));
    }

    @Test
    public void testForList_ReturnSimple() {
        List<Integer> result = template.forList("sql/selectEmpno.sql", Integer.class);
        assertThat(result.size(), is(14));
        assertThat(result.get(0), is(7369));
        assertThat(result.get(13), is(7934));
    }

    @Test
    public void testUpdate_insertByEntity() {
        Emp emp = new Emp();
        emp.empno = 1000;
        emp.ename = "TEST";
        emp.job = "MANAGER";
        emp.mgr = 7839;
        emp.hiredate = LocalDate.of(2015, 4, 1);
        emp.sal = new BigDecimal(4000);
        emp.comm = new BigDecimal(400);
        emp.deptno = 10;

        int count = template.update("sql/insertByParam.sql", emp);
        assertThat(count, is(1));

        Emp result = template.forObject("sql/selectByEmpno.sql", Emp.class, 1000);
        assertThat(result.ename, is(emp.ename));
        assertThat(result.hiredate, is(emp.hiredate));
        assertThat(result.deptno, is(emp.deptno));
    }

    @Test
    public void testUpdate_updateByMap() {
        Map<String, Object> param = new HashMap<>();
        param.put("job", "ANALYST");
        param.put("mgr", 7566);
        param.put("empno", 7876);

        int count = template.update("sql/updateByParam.sql", param);
        assertThat(count, is(1));

        Emp result = template.forObject("sql/selectByEmpno.sql", Emp.class, 7876);
        assertThat(result.job, is("ANALYST"));
        assertThat(result.mgr, is(7566));
    }

    @Test
    public void testUpdate_deleteByArg() {
        int count = template.update("sql/deleteByArg.sql", 7566);
        assertThat(count, is(1));

        Emp result = template.forObject("sql/selectByEmpno.sql", Emp.class, 7566);
        assertNull(result);
    }

    @Test
    public void testUpdate_deleteByArgs() {
        int count = template.update("sql/deleteByArgs.sql", 30, "SALESMAN");
        assertThat(count, is(4));

        List<Emp> result = template.forList("sql/selectByDeptno.sql", Emp.class, 30);
        assertThat(result.size(), is(2));
        assertThat(result.get(0).empno, is(7698));
        assertThat(result.get(1).empno, is(7900));
    }

    @Test
    public void testQuery_forList() {
        List<Emp> result = template.query("sql/selectByParam.sql", Emp.class).add("job", "SALESMAN").add("deptno", 30)
                .forList();
        assertThat(result.size(), is(4));
        assertThat(result.get(0).empno, is(7499));
        assertThat(result.get(3).empno, is(7844));
    }

    @Test
    public void testQuery_forObject() {
        Emp emp = template.query("sql/selectSingleByParam.sql", Emp.class).add("job", "SALESMAN").add("deptno", 30)
                .forObject();
        assertThat(emp.empno, is(7499));
    }

    @Test
    public void testForObject_noFile() {
        try {
            Emp emp = template.forObject("x", Emp.class);
            fail();
        } catch (UncheckedIOException ex) {
            assertThat(ex.getCause().getMessage(), is("Template 'x' not found"));
        }
    }
}
