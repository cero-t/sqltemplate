package ninja.cero.sqltemplate;

import ninja.cero.sqltemplate.test.TestConfig;
import ninja.cero.sqltemplate.test.entity.AccessorEmp;
import ninja.cero.sqltemplate.test.entity.DateTimeEntity;
import ninja.cero.sqltemplate.test.entity.Emp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class SqlTemplateTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    public void testForObject_NoArgs() {
        Emp emp = sqlTemplate()
                .file("sql/selectSingle.sql")
                .forObject(Emp.class);
        assertEquals(7369, emp.empno);
    }

    @Test
    public void testForObject_MapArg() {
        Map<String, Object> param = new HashMap<>();
        param.put("deptno", 30);
        param.put("job", "SALESMAN");

        Emp emp = sqlTemplate()
                .file("sql/selectSingleByParam.sql")
                .param(param)
                .forObject(Emp.class);
        assertEquals(7499, emp.empno);
    }

    @Test
    public void testForObject_EntityArg() {
        Emp param = new Emp();
        param.deptno = 30;
        param.job = "SALESMAN";

        Emp emp = sqlTemplate()
                .file("sql/selectSingleByParam.sql")
                .param(param)
                .forObject(Emp.class);
        assertEquals(7499, emp.empno);
    }

    @Test
    public void testForObject_SingleArg() {
        Emp emp = sqlTemplate()
                .file("sql/selectByEmpno.sql")
                .params(7839)
                .forObject(Emp.class);
        assertEquals(7839, emp.empno);
    }

    @Test
    public void testForObject_MultiArg() {
        Emp emp = sqlTemplate()
                .file("sql/selectSingleByArgs.sql")
                .params(30, "SALESMAN")
                .forObject(Emp.class);
        assertEquals(7499, emp.empno);
    }

    @Test
    public void testForObject_ReturnSimple() {
        Integer result = sqlTemplate()
                .file("sql/selectSingleEmpno.sql")
                .forObject(Integer.class);
        assertEquals(7369, result);
    }

    @Test
    public void testForList_NoArg() {
        List<Emp> result = sqlTemplate().file("sql/selectAll.sql").forList(Emp.class);
        assertEquals(14, result.size());
        assertEquals(7369, result.get(0).empno);
        assertEquals(7934, result.get(13).empno);
    }

    @Test
    public void testForList_MapArg() {
        Map<String, Object> param = new HashMap<>();
        param.put("deptno", 30);
        param.put("job", "SALESMAN");

        List<Emp> result = sqlTemplate()
                .file("sql/selectByParam.sql")
                .param(param)
                .forList(Emp.class);
        assertEquals(4, result.size());
        assertEquals(7499, result.get(0).empno);
        assertEquals(7844, result.get(3).empno);
    }

    @Test
    public void testForList_EntityArg() {
        Emp param = new Emp();
        param.deptno = 30;
        param.job = "SALESMAN";

        List<Emp> result = sqlTemplate()
                .file("sql/selectByParam.sql")
                .param(param)
                .forList(Emp.class);
        assertEquals(4, result.size());
        assertEquals(7499, result.get(0).empno);
        assertEquals(7844, result.get(3).empno);
    }

    @Test
    public void testForList_EntityArgWithAccessor() {
        AccessorEmp param = new AccessorEmp();
        param.setDeptno(30);
        param.setJob("SALESMAN");

        List<AccessorEmp> result = sqlTemplate()
                .file("sql/selectByParam.sql")
                .param(param)
                .forList(AccessorEmp.class);
        assertEquals(4, result.size());
        assertEquals(7499, result.get(0).getEmpno());
        assertEquals(7844, result.get(3).getEmpno());
    }

    @Test
    public void testForList_SingleArg() {
        List<Emp> result = sqlTemplate()
                .file("sql/selectByDeptno.sql")
                .params(10)
                .forList(Emp.class);
        assertEquals(3, result.size());
        assertEquals(7782, result.get(0).empno);
        assertEquals(7934, result.get(2).empno);
    }

    @Test
    public void testForList_MultiArg() {
        List<Emp> result = sqlTemplate()
                .file("sql/selectByArgs.sql")
                .params(30, "SALESMAN")
                .forList(Emp.class);
        assertEquals(4, result.size());
        assertEquals(7499, result.get(0).empno);
        assertEquals(7844, result.get(3).empno);
    }

    @Test
    public void testForList_ReturnSimple() {
        List<Integer> result = sqlTemplate().file("sql/selectEmpno.sql").forList(Integer.class);
        assertEquals(14, result.size());
        assertEquals(7369, result.get(0));
        assertEquals(7934, result.get(13));
    }

    @Test
    public void testForStream_NoArg() {
        int[] result = sqlTemplate()
                .file("sql/selectAll.sql")
                .forStream(Emp.class, stream -> stream.mapToInt(emp -> emp.empno).toArray());
        assertEquals(14, result.length);
        assertEquals(7369, result[0]);
        assertEquals(7934, result[13]);
    }

    @Test
    public void testForStream_MapArg() {
        Map<String, Object> param = new HashMap<>();
        param.put("deptno", 30);
        param.put("job", "SALESMAN");
        int[] result = sqlTemplate()
                .file("sql/selectByParam.sql")
                .param(param)
                .forStream(Emp.class, stream -> stream.mapToInt(emp -> emp.empno).toArray());
        assertEquals(4, result.length);
        assertEquals(7499, result[0]);
        assertEquals(7844, result[3]);
    }

    @Test
    public void testForStream_EntityArg() {
        Emp param = new Emp();
        param.deptno = 30;
        param.job = "SALESMAN";

        int[] result = sqlTemplate()
                .file("sql/selectByParam.sql")
                .param(param)
                .forStream(Emp.class, stream -> stream.mapToInt(emp -> emp.empno).toArray());
        assertEquals(4, result.length);
        assertEquals(7499, result[0]);
        assertEquals(7844, result[3]);
    }

    @Test
    public void testForStream_EntityArgWithAccessor() {
        AccessorEmp param = new AccessorEmp();
        param.setDeptno(30);
        param.setJob("SALESMAN");

        int[] result = sqlTemplate()
                .file("sql/selectByParam.sql")
                .param(param)
                .forStream(AccessorEmp.class, stream -> stream.mapToInt(AccessorEmp::getEmpno).toArray());
        assertEquals(4, result.length);
        assertEquals(7499, result[0]);
        assertEquals(7844, result[3]);
    }

    @Test
    public void testForStream_SingleArg() {
        int[] result = sqlTemplate()
                .file("sql/selectByDeptno.sql")
                .params(10)
                .forStream(Emp.class, stream -> stream.mapToInt(emp -> emp.empno).toArray());
        assertEquals(3, result.length);
        assertEquals(7782, result[0]);
        assertEquals(7934, result[2]);
    }

    @Test
    public void testForStream_MultiArg() {
        int[] result = sqlTemplate()
                .file("sql/selectByArgs.sql")
                .params(30, "SALESMAN")
                .forStream(Emp.class, stream -> stream.mapToInt(emp -> emp.empno).toArray());
        assertEquals(4, result.length);
        assertEquals(7499, result[0]);
        assertEquals(7844, result[3]);
    }

    @Test
    public void testForStream_ReturnSimple() {
        int[] result = sqlTemplate().file("sql/selectEmpno.sql")
                .forStream(Integer.class, stream -> stream.mapToInt(empno -> empno).toArray());
        assertEquals(14, result.length);
        assertEquals(7369, result[0]);
        assertEquals(7934, result[13]);
    }

    @Test
    public void testForMap_NoArgs() {
        Map<String, Object> result = sqlTemplate().file("sql/selectSingle.sql").forMap();
        assertEquals(7369, result.get("empno"));
    }

    @Test
    public void testForMap_MapArg() {
        Map<String, Object> param = new HashMap<>();
        param.put("deptno", 30);
        param.put("job", "SALESMAN");

        Map<String, Object> result = sqlTemplate()
                .file("sql/selectSingleByParam.sql")
                .param(param)
                .forMap();
        assertEquals(7499, result.get("empno"));
    }

    @Test
    public void testForMap_EntityArg() {
        Emp param = new Emp();
        param.deptno = 30;
        param.job = "SALESMAN";

        Map<String, Object> result = sqlTemplate()
                .file("sql/selectSingleByParam.sql")
                .param(param)
                .forMap();
        assertEquals(7499, result.get("empno"));
    }

    @Test
    public void testForMap_SingleArg() {
        Map<String, Object> result = sqlTemplate()
                .file("sql/selectByEmpno.sql")
                .params(7839)
                .forMap();
        assertEquals(7839, result.get("empno"));
    }

    @Test
    public void testForMap_MultiArg() {
        Map<String, Object> result = sqlTemplate()
                .file("sql/selectSingleByArgs.sql")
                .params(30, "SALESMAN")
                .forMap();
        assertEquals(7499, result.get("empno"));
    }

    @Test
    public void testForMap_ReturnSimple() {
        Map<String, Object> result = sqlTemplate().file("sql/selectSingleEmpno.sql").forMap();
        assertEquals(1, result.size());
        assertEquals(7369, result.get("empno"));
    }


    @Test
    public void testForListMap_NoArg() {
        List<Map<String, Object>> result = sqlTemplate().file("sql/selectAll.sql").forList();
        assertEquals(14, result.size());
        assertEquals(7369, result.get(0).get("empno"));
        assertEquals(7934, result.get(13).get("empno"));
    }

    @Test
    public void testForListMap_MapArg() {
        Map<String, Object> param = new HashMap<>();
        param.put("deptno", 30);
        param.put("job", "SALESMAN");

        List<Map<String, Object>> result = sqlTemplate()
                .file("sql/selectByParam.sql")
                .param(param)
                .forList();
        assertEquals(4, result.size());
        assertEquals(7499, result.get(0).get("empno"));
        assertEquals(7844, result.get(3).get("empno"));
    }

    @Test
    public void testForListMap_EntityArg() {
        Emp param = new Emp();
        param.deptno = 30;
        param.job = "SALESMAN";

        List<Map<String, Object>> result = sqlTemplate()
                .file("sql/selectByParam.sql")
                .param(param)
                .forList();
        assertEquals(4, result.size());
        assertEquals(7499, result.get(0).get("empno"));
        assertEquals(7844, result.get(3).get("empno"));
    }

    @Test
    public void testForListMap_SingleArg() {
        List<Map<String, Object>> result = sqlTemplate()
                .file("sql/selectByDeptno.sql")
                .params(10)
                .forList();
        assertEquals(3, result.size());
        assertEquals(7782, result.get(0).get("empno"));
        assertEquals(7934, result.get(2).get("empno"));
    }

    @Test
    public void testForListMap_MultiArg() {
        List<Map<String, Object>> result = sqlTemplate()
                .file("sql/selectByArgs.sql")
                .params(30, "SALESMAN")
                .forList();
        assertEquals(4, result.size());
        assertEquals(7499, result.get(0).get("empno"));
        assertEquals(7844, result.get(3).get("empno"));
    }

    @Test
    public void testForStreamMap_NoArg() {
        int[] result = sqlTemplate().file("sql/selectAll.sql")
                .forStream(stream -> stream.mapToInt(map -> (Integer) map.get("empno")).toArray());
        assertEquals(14, result.length);
        assertEquals(7369, result[0]);
        assertEquals(7934, result[13]);
    }

    @Test
    public void testForStreamMap_MapArg() {
        Map<String, Object> param = new HashMap<>();
        param.put("deptno", 30);
        param.put("job", "SALESMAN");

        int[] result = sqlTemplate().file("sql/selectByParam.sql")
                .param(param)
                .forStream(stream -> stream.mapToInt(map -> (Integer) map.get("empno")).toArray());
        assertEquals(4, result.length);
        assertEquals(7499, result[0]);
        assertEquals(7844, result[3]);
    }

    @Test
    public void testForStreamMap_EntityArg() {
        Emp param = new Emp();
        param.deptno = 30;
        param.job = "SALESMAN";

        int[] result = sqlTemplate().file("sql/selectByParam.sql")
                .param(param)
                .forStream(stream -> stream.mapToInt(map -> (Integer) map.get("empno")).toArray());
        assertEquals(4, result.length);
        assertEquals(7499, result[0]);
        assertEquals(7844, result[3]);
    }

    @Test
    public void testForStreamMap_SingleArg() {
        int[] result = sqlTemplate().file("sql/selectByDeptno.sql")
                .params(10)
                .forStream(stream -> stream.mapToInt(map -> (Integer) map.get("empno")).toArray());
        assertEquals(3, result.length);
        assertEquals(7782, result[0]);
        assertEquals(7934, result[2]);
    }

    @Test
    public void testForStreamMap_MultiArg() {
        int[] result = sqlTemplate().file("sql/selectByArgs.sql")
                .params(30, "SALESMAN")
                .forStream(stream -> stream.mapToInt(map -> (Integer) map.get("empno")).toArray());
        assertEquals(4, result.length);
        assertEquals(7499, result[0]);
        assertEquals(7844, result[3]);
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

        int count = sqlTemplate().file("sql/insertByParam.sql")
                .param(emp)
                .update();
        assertEquals(1, count);

        Emp result = sqlTemplate()
                .file("sql/selectByEmpno.sql")
                .params(1000)
                .forObject(Emp.class);
        assertEquals(emp.ename, result.ename);
        assertEquals(emp.hiredate, result.hiredate);
        assertEquals(emp.deptno, result.deptno);
    }

    @Test
    public void testUpdate_updateByMap() {
        Map<String, Object> param = new HashMap<>();
        param.put("job", "ANALYST");
        param.put("mgr", 7566);
        param.put("empno", 7876);

        int count = sqlTemplate().file("sql/updateByParam.sql")
                .param(param)
                .update();
        assertEquals(1, count);

        Emp result = sqlTemplate()
                .file("sql/selectByEmpno.sql")
                .params(7876)
                .forObject(Emp.class);

        assertEquals("ANALYST", result.job);
        assertEquals(7566, result.mgr);
    }

    @Test
    public void testUpdate_deleteByArg() {
        int count = sqlTemplate().file("sql/deleteByArg.sql")
                .params(7566)
                .update();
        assertEquals(1, count);

        Emp result = sqlTemplate()
                .file("sql/selectByEmpno.sql")
                .params(7566)
                .forObject(Emp.class);
        assertNull(result);
    }

    @Test
    public void testUpdate_deleteByArgs() {
        int count = sqlTemplate().file("sql/deleteByArgs.sql").params(30, "SALESMAN").update();
        assertEquals(4, count);

        List<Emp> result = sqlTemplate()
                .file("sql/selectByDeptno.sql")
                .params(30)
                .forList(Emp.class);
        assertEquals(2, result.size());
        assertEquals(7698, result.get(0).empno);
        assertEquals(7900, result.get(1).empno);
    }

    @Test
    public void testQuery_forObject() {
        Emp emp = sqlTemplate().file("sql/selectSingleByParam.sql")
                .addParam("job", "SALESMAN")
                .addParam("deptno", 30)
                .forObject(Emp.class);
        assertEquals(7499, emp.empno);
    }

    @Test
    public void testQuery_forList() {
        List<Emp> result = sqlTemplate().file("sql/selectByParam.sql")
                .addParam("job", "SALESMAN")
                .addParam("deptno", 30)
                .forList(Emp.class);
        assertEquals(4, result.size());
        assertEquals(7499, result.get(0).empno);
        assertEquals(7844, result.get(3).empno);
    }

    @Test
    public void testQuery_forStream() {
        int[] result = sqlTemplate().file("sql/selectByParam.sql")
                .addParam("job", "SALESMAN")
                .addParam("deptno", 30)
                .forStream(Emp.class, stream -> stream.mapToInt(emp -> emp.empno).toArray());
        assertEquals(4, result.length);
        assertEquals(7499, result[0]);
        assertEquals(7844, result[3]);
    }

    @Test
    public void testQuery_forMap() {
        Map<String, Object> result = sqlTemplate()
                .file("sql/selectSingleByParam.sql")
                .addParam("job", "SALESMAN")
                .addParam("deptno", 30)
                .forMap();
        assertEquals(7499, result.get("empno"));
    }

    @Test
    public void testQuery_forListMap() {
        List<Map<String, Object>> result = sqlTemplate()
                .file("sql/selectByParam.sql")
                .addParam("job", "SALESMAN")
                .addParam("deptno", 30)
                .forList();
        assertEquals(4, result.size());
        assertEquals(7499, result.get(0).get("empno"));
        assertEquals(7844, result.get(3).get("empno"));
    }

    @Test
    public void testQuery_forStreamMap() {
        int[] result = sqlTemplate()
                .file("sql/selectByParam.sql")
                .addParam("job", "SALESMAN")
                .addParam("deptno", 30)
                .forStream(stream -> stream.mapToInt(map -> (Integer) map.get("empno")).toArray());
        assertEquals(4, result.length);
        assertEquals(7499, result[0]);
        assertEquals(7844, result[3]);
    }

    @Test
    public void testForObject_noFile() {
        try {
            sqlTemplate().file("x").forObject(Emp.class);
            throw new RuntimeException("Test failed");
        } catch (UncheckedIOException ex) {
            assertTrue(ex.getCause().getMessage().contains("not found"));
        }
    }

    @Test
    public void testForObject_DateTime() {
        // prepare
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Tokyo")));

        // execute
        DateTimeEntity result = sqlTemplate()
                .query("SELECT * FROM date_time")
                .forObject(DateTimeEntity.class);

        // assert
        assertEquals("2001-01-23 12:34:56.789", result.utilDate.toString());
        assertEquals("2001-01-24", result.sqlDate.toString());
        assertEquals("12:34:57", result.sqlTime.toString());
        assertEquals("2001-01-25 12:34:58.789", result.sqlTimestamp.toString());
        assertEquals("2001-01-26T12:34:59.789", result.localDateTime.toString());
        assertEquals("2001-01-27", result.localDate.toString());
        assertEquals("12:35:01", result.localTime.toString());
        assertEquals("2001-01-28T12:35:02.789+09:00[Asia/Tokyo]", result.zonedDateTime.toString());
        assertEquals("2001-01-29T12:35:03.789+09:00", result.offsetDateTime.toString());
        assertEquals("12:35:04+09:00", result.offsetTime.toString());
    }

    @Test
    public void testForList_SingleDateTime() {
        // prepare
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Tokyo")));

        // execute
        // assert
        List<LocalDateTime> localDateTime = sqlTemplate()
                .query("SELECT local_date_time FROM date_time")
                .forList(LocalDateTime.class);
        assertEquals("2001-01-26T12:34:59.789", localDateTime.get(0).toString());

        List<LocalDate> localDate = sqlTemplate()
                .query("SELECT local_date FROM date_time")
                .forList(LocalDate.class);
        assertEquals("2001-01-27", localDate.get(0).toString());

        List<LocalTime> localTime = sqlTemplate()
                .query("SELECT local_time FROM date_time")
                .forList(LocalTime.class);
        assertEquals("12:35:01", localTime.get(0).toString());
    }

    @Test
    public void testForObject_SingleDateTime() {
        // prepare
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Tokyo")));

        // execute
        // assert
        LocalDateTime localDateTime = sqlTemplate().query("SELECT local_date_time FROM date_time").forObject(LocalDateTime.class);
        assertEquals("2001-01-26T12:34:59.789", localDateTime.toString());

        LocalDate localDate = sqlTemplate().query("SELECT local_date FROM date_time").forObject(LocalDate.class);
        assertEquals("2001-01-27", localDate.toString());

        LocalTime localTime = sqlTemplate().query("SELECT local_time FROM date_time").forObject(LocalTime.class);
        assertEquals("12:35:01", localTime.toString());
    }

    @Test
    public void testForListMap_DateTime() {
        // prepare
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Tokyo")));

        // execute
        List<Map<String, Object>> maps = sqlTemplate().query("SELECT * FROM date_time").forList();

        // assert
        Map<String, Object> result = maps.get(0);
        assertEquals(Timestamp.class, result.get("local_date_time").getClass());
        assertEquals("2001-01-26 12:34:59.789", result.get("local_date_time").toString());
        assertEquals(java.sql.Date.class, result.get("local_date").getClass());
        assertEquals("2001-01-27", result.get("local_date").toString());
        assertEquals(Time.class, result.get("local_time").getClass());
        assertEquals("12:35:01", result.get("local_time").toString());
    }

    @Test
    public void testUpdate_DateTime() {
        // prepare
        jdbcTemplate.update("DELETE from date_time");
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Tokyo")));

        DateTimeEntity entity = new DateTimeEntity();
        entity.utilDate = java.util.Date.from(LocalDateTime.of(2001, 1, 23, 12, 34, 56, 789000000).atZone(ZoneId.systemDefault()).toInstant());
        entity.sqlDate = java.sql.Date.valueOf(LocalDate.of(2001, 1, 24));
        entity.sqlTime = java.sql.Time.valueOf(LocalTime.of(12, 34, 57));
        entity.sqlTimestamp = Timestamp.valueOf(LocalDateTime.of(2001, 1, 25, 12, 34, 58, 789000000));
        entity.localDateTime = LocalDateTime.of(2001, 1, 26, 12, 34, 59, 789000000);
        entity.localDate = LocalDate.of(2001, 1, 27);
        entity.localTime = LocalTime.of(12, 35, 1);
        entity.zonedDateTime = LocalDateTime.of(2001, 1, 28, 12, 35, 2, 789000000).atZone(ZoneId.systemDefault());
        entity.offsetDateTime = LocalDateTime.of(2001, 1, 29, 12, 35, 3, 789000000).atOffset(ZoneId.systemDefault().getRules().getOffset(Instant.now()));
        entity.offsetTime = LocalTime.of(12, 35, 4).atOffset(ZoneId.systemDefault().getRules().getOffset(Instant.now()));

        // execute
        SqlTemplate template = new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate);
        int num = template.query("INSERT INTO date_time VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .params(entity.utilDate, entity.sqlDate, entity.sqlTime, entity.sqlTimestamp, entity.localDateTime,
                        entity.localDate, entity.localTime, entity.zonedDateTime, entity.offsetDateTime, entity.offsetTime)
                .update();

        // assert
        assertEquals(1, num);

        DateTimeEntity result = template.query("SELECT * FROM date_time").forObject(DateTimeEntity.class);
        assertEquals("2001-01-23 12:34:56.789", result.utilDate.toString());
        assertEquals("2001-01-24", result.sqlDate.toString());
        assertEquals("12:34:57", result.sqlTime.toString());
        assertEquals("2001-01-25 12:34:58.789", result.sqlTimestamp.toString());
        assertEquals("2001-01-26T12:34:59.789", result.localDateTime.toString());
        assertEquals("2001-01-27", result.localDate.toString());
        assertEquals("12:35:01", result.localTime.toString());
        assertEquals("2001-01-28T12:35:02.789+09:00[Asia/Tokyo]", result.zonedDateTime.toString());
        assertEquals("2001-01-29T12:35:03.789+09:00", result.offsetDateTime.toString());
        assertEquals("12:35:04+09:00", result.offsetTime.toString());
    }

    @Test
    public void testUpdate_DateTime_Null() {
        // prepare
        jdbcTemplate.update("DELETE from date_time");
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Tokyo")));
        DateTimeEntity entity = new DateTimeEntity();

        // execute
        int num = sqlTemplate().query("INSERT INTO date_time VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .params(entity.utilDate, entity.sqlDate, entity.sqlTime, entity.sqlTimestamp, entity.localDateTime,
                        entity.localDate, entity.localTime, entity.zonedDateTime, entity.offsetDateTime, entity.offsetTime)
                .update();

        // assert
        assertEquals(1, num);

        DateTimeEntity result = sqlTemplate()
                .query("SELECT * FROM date_time")
                .forObject(DateTimeEntity.class);

        assertNull(result.utilDate);
        assertNull(result.sqlDate);
        assertNull(result.sqlTime);
        assertNull(result.sqlTimestamp);
        assertNull(result.localDateTime);
        assertNull(result.localDate);
        assertNull(result.localTime);
        assertNull(result.zonedDateTime);
        assertNull(result.offsetDateTime);
        assertNull(result.offsetTime);
    }

    @Test
    public void testUpdate_DateTime_ZoneAware() {
        // prepare
        jdbcTemplate.update("DELETE from date_time");
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Tokyo")));

        DateTimeEntity entity = new DateTimeEntity();
        entity.utilDate = java.util.Date.from(LocalDateTime.of(2001, 1, 23, 12, 34, 56, 789000000).atZone(ZoneId.systemDefault()).toInstant());
        entity.sqlDate = java.sql.Date.valueOf(LocalDate.of(2001, 1, 24));
        entity.sqlTime = java.sql.Time.valueOf(LocalTime.of(12, 34, 57));
        entity.sqlTimestamp = Timestamp.valueOf(LocalDateTime.of(2001, 1, 25, 12, 34, 58, 789000000));
        entity.localDateTime = LocalDateTime.of(2001, 1, 26, 12, 34, 59, 789000000);
        entity.localDate = LocalDate.of(2001, 1, 27);
        entity.localTime = LocalTime.of(12, 35, 1);
        entity.zonedDateTime = LocalDateTime.of(2001, 1, 28, 12, 35, 2, 789000000).atZone(ZoneId.systemDefault());
        entity.offsetDateTime = LocalDateTime.of(2001, 1, 29, 12, 35, 3, 789000000).atOffset(ZoneId.systemDefault().getRules().getOffset(Instant.now()));
        entity.offsetTime = LocalTime.of(12, 35, 4).atOffset(ZoneId.systemDefault().getRules().getOffset(Instant.now()));

        // execute
        SqlTemplate template = new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate, ZoneId.of("GMT"));
        int num = template.query("INSERT INTO date_time VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .params(entity.utilDate, entity.sqlDate, entity.sqlTime, entity.sqlTimestamp, entity.localDateTime,
                        entity.localDate, entity.localTime, entity.zonedDateTime, entity.offsetDateTime, entity.offsetTime)
                .update();

        // assert
        assertEquals(1, num);

        DateTimeEntity result = template.query("SELECT * FROM date_time").forObject(DateTimeEntity.class);
        assertEquals("2001-01-23 12:34:56.789", result.utilDate.toString());
        assertEquals("2001-01-24", result.sqlDate.toString());
        assertEquals("12:34:57", result.sqlTime.toString());
        assertEquals("2001-01-25 12:34:58.789", result.sqlTimestamp.toString());
        assertEquals("2001-01-26T12:34:59.789", result.localDateTime.toString());
        assertEquals("2001-01-27", result.localDate.toString());
        assertEquals("12:35:01", result.localTime.toString());
        assertEquals("2001-01-28T03:35:02.789Z[GMT]", result.zonedDateTime.toString());
        assertEquals("2001-01-29T03:35:03.789Z", result.offsetDateTime.toString());
        assertEquals("03:35:04Z", result.offsetTime.toString());
    }

    @Test
    public void testUpdateByAdd() {
        int count = sqlTemplate().file("sql/updateByParam.sql")
                .addParam("job", "TEST")
                .addParam("mgr", 1234)
                .addParam("empno", 7876)
                .update();
        assertEquals(1, count);

        Emp result = sqlTemplate()
                .file("sql/selectByEmpno.sql")
                .params(7876)
                .forObject(Emp.class);
        assertEquals("TEST", result.job);
        assertEquals(1234, result.mgr);
    }

    @Test
    public void testBatchUpdate_byQueries() {
        // execute
        int[] counts = sqlTemplate().batchUpdate()
                .queries("delete from emp",
                        "insert into emp (empno) values (1234)");

        // assert
        assertArrayEquals(new int[]{14, 1}, counts);

        List<Emp> result = sqlTemplate().file("sql/selectAll.sql").forList(Emp.class);
        assertEquals(1, result.size());
        assertEquals(1234, result.get(0).empno);
    }

    @Test
    public void testBatchUpdate_byQuery() {
        // prepare
        jdbcTemplate.update("DELETE from emp");

        // execute
        int[] counts = sqlTemplate().batchUpdate()
                .query("insert into emp (empno) values (?)")
                .addBatch(111)
                .addBatch(222)
                .execute();

        // assert
        assertArrayEquals(new int[]{1, 1}, counts);

        List<Emp> result = sqlTemplate().file("sql/selectAll.sql").forList(Emp.class);
        assertEquals(2, result.size());
        assertEquals(111, result.get(0).empno);
        assertEquals(222, result.get(1).empno);
    }

    @Test
    public void testBatchUpdate_byQueryNoBatch() {
        // prepare
        jdbcTemplate.update("DELETE from emp");

        // execute
        int[] counts = sqlTemplate().batchUpdate()
                .query("insert into emp (empno) values (1234)")
                .execute();

        // assert
        assertArrayEquals(new int[]{1}, counts);

        List<Emp> result = sqlTemplate().file("sql/selectAll.sql").forList(Emp.class);
        assertEquals(1, result.size());
        assertEquals(1234, result.get(0).empno);
    }

    @Test
    public void testBatchUpdate_byArgs() {
        // execute
        int[] counts = sqlTemplate().batchUpdate().file("sql/deleteByArgs.sql")
                .addBatch(30, "SALESMAN")
                .addBatch(30, "CLERK")
                .execute();

        // assert
        assertArrayEquals(new int[]{4, 1}, counts);

        Emp result = sqlTemplate()
                .file("sql/selectByDeptno.sql")
                .params(30)
                .forObject(Emp.class);
        assertEquals(7698, result.empno);
    }

    @Test
    public void testBatchUpdate_byMap() {
        // prepare
        Map<String, Object> arg1 = new HashMap<>();
        arg1.put("empno", 7369);
        arg1.put("job", "SALESMAN");
        arg1.put("mgr", 7698);

        Map<String, Object> arg2 = new HashMap<>();
        arg2.put("empno", 7499);
        arg2.put("job", "CLERK");
        arg2.put("mgr", 7902);

        // execute
        int[] counts = sqlTemplate().batchUpdate().file("sql/updateByParam.sql")
                .addBatch(arg1)
                .addBatch(arg2)
                .execute();

        // assert
        assertArrayEquals(new int[]{1, 1}, counts);

        Emp result1 = sqlTemplate()
                .file("sql/selectByEmpno.sql")
                .params(7369)
                .forObject(Emp.class);
        assertEquals("SMITH", result1.ename);
        assertEquals("SALESMAN", result1.job);
        assertEquals(7698, result1.mgr);

        Emp result2 = sqlTemplate()
                .file("sql/selectByEmpno.sql")
                .params(7499)
                .forObject(Emp.class);
        assertEquals("ALLEN", result2.ename);
        assertEquals("CLERK", result2.job);
        assertEquals(7902, result2.mgr);
    }

    @Test
    public void testBatchUpdate_byMaps() {
        // prepare
        Map<String, Object> arg1 = new HashMap<>();
        arg1.put("empno", 7369);
        arg1.put("job", "SALESMAN2");
        arg1.put("mgr", 7698);

        Map<String, Object> arg2 = new HashMap<>();
        arg2.put("empno", 7499);
        arg2.put("job", "CLERK2");
        arg2.put("mgr", 7902);

        List<Map<String, Object>> maps = Arrays.asList(arg1, arg2);

        // execute
        int[] counts = sqlTemplate().batchUpdate().file("sql/updateByParam.sql")
                .addBatches(maps)
                .execute();

        // assert
        assertArrayEquals(new int[]{1, 1}, counts);

        Emp result1 = sqlTemplate()
                .file("sql/selectByEmpno.sql")
                .params(7369)
                .forObject(Emp.class);
        assertEquals("SMITH", result1.ename);
        assertEquals("SALESMAN2", result1.job);
        assertEquals(7698, result1.mgr);

        Emp result2 = sqlTemplate()
                .file("sql/selectByEmpno.sql")
                .params(7499)
                .forObject(Emp.class);
        assertEquals("ALLEN", result2.ename);
        assertEquals("CLERK2", result2.job);
        assertEquals(7902, result2.mgr);
    }

    @Test
    public void testBatchUpdate_bySimpleArgs() {
        // execute
        int[] counts = sqlTemplate().batchUpdate().file("sql/deleteByArg.sql")
                .addBatch(7782)
                .addBatch(7934)
                .execute();

        // assert
        assertArrayEquals(new int[]{1, 1}, counts);

        Emp result = sqlTemplate()
                .file("sql/selectByDeptno.sql")
                .params(10)
                .forObject(Emp.class);
        assertEquals(7839, result.empno);
    }

    @Test
    public void testBatchUpdate_byEntity() {
        // prepare
        Emp emp1 = new Emp();
        emp1.empno = 1001;
        emp1.ename = "TEST1";
        emp1.job = "MANAGER";
        emp1.mgr = 7839;
        emp1.hiredate = LocalDate.of(2015, 4, 1);
        emp1.sal = new BigDecimal(4000);
        emp1.comm = new BigDecimal(400);
        emp1.deptno = 10;

        Emp emp2 = new Emp();
        emp2.empno = 1002;
        emp2.ename = "TEST2";
        emp2.job = "MANAGER";
        emp2.mgr = 7839;
        emp2.hiredate = LocalDate.of(2015, 4, 2);
        emp2.sal = new BigDecimal(4000);
        emp2.comm = new BigDecimal(400);
        emp2.deptno = 20;

        // execute
        int[] counts = sqlTemplate().batchUpdate().file("sql/insertByParam.sql")
                .addBatch(emp1)
                .addBatch(emp2)
                .execute();

        // assert
        assertArrayEquals(new int[]{1, 1}, counts);

        Emp result1 = sqlTemplate()
                .file("sql/selectByEmpno.sql")
                .params(1001)
                .forObject(Emp.class);
        assertEquals(emp1.ename, result1.ename);
        assertEquals(emp1.hiredate, result1.hiredate);
        assertEquals(emp1.deptno, result1.deptno);

        Emp result2 = sqlTemplate()
                .file("sql/selectByEmpno.sql")
                .params(1002)
                .forObject(Emp.class);
        assertEquals(emp2.ename, result2.ename);
        assertEquals(emp2.hiredate, result2.hiredate);
        assertEquals(emp2.deptno, result2.deptno);
    }

    @Test
    public void testBatchUpdate_byEntities() {
        // prepare
        Emp emp1 = new Emp();
        emp1.empno = 1001;
        emp1.ename = "TEST1";
        emp1.job = "MANAGER";
        emp1.mgr = 7839;
        emp1.hiredate = LocalDate.of(2015, 4, 1);
        emp1.sal = new BigDecimal(4000);
        emp1.comm = new BigDecimal(400);
        emp1.deptno = 10;

        Emp emp2 = new Emp();
        emp2.empno = 1002;
        emp2.ename = "TEST2";
        emp2.job = "MANAGER";
        emp2.mgr = 7839;
        emp2.hiredate = LocalDate.of(2015, 4, 2);
        emp2.sal = new BigDecimal(4000);
        emp2.comm = new BigDecimal(400);
        emp2.deptno = 20;

        List<Emp> emps = Arrays.asList(emp1, emp2);

        // execute
        int[] counts = sqlTemplate().batchUpdate().file("sql/insertByParam.sql")
                .addBatches(emps)
                .execute();

        // assert
        assertArrayEquals(new int[]{1, 1}, counts);

        Emp result1 = sqlTemplate()
                .file("sql/selectByEmpno.sql")
                .params(1001)
                .forObject(Emp.class);
        assertEquals(emp1.ename, result1.ename);
        assertEquals(emp1.hiredate, result1.hiredate);
        assertEquals(emp1.deptno, result1.deptno);

        Emp result2 = sqlTemplate()
                .file("sql/selectByEmpno.sql")
                .params(1002)
                .forObject(Emp.class);
        assertEquals(emp2.ename, result2.ename);
        assertEquals(emp2.hiredate, result2.hiredate);
        assertEquals(emp2.deptno, result2.deptno);
    }

    SqlTemplate sqlTemplate() {
        return new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate);
    }
}
