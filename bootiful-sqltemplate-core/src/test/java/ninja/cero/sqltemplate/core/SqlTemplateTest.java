package ninja.cero.sqltemplate.core;

import ninja.cero.sqltemplate.core.template.PlainText;
import ninja.cero.sqltemplate.test.TestConfig;
import ninja.cero.sqltemplate.test.entity.DateTimeEntity;
import ninja.cero.sqltemplate.test.entity.Emp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class SqlTemplateTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    public void testForObject_NoArgs() {
        Emp emp = sqlTemplate().forObject("sql/selectSingle.sql", Emp.class);
        assertThat(emp.empno, is(7369));
    }

    @Test
    public void testForObject_MapArg() {
        Map<String, Object> param = new HashMap<>();
        param.put("deptno", 30);
        param.put("job", "SALESMAN");

        Emp emp = sqlTemplate().forObject("sql/selectSingleByParam.sql", Emp.class, param);
        assertThat(emp.empno, is(7499));
    }

    @Test
    public void testForObject_EntityArg() {
        Emp param = new Emp();
        param.deptno = 30;
        param.job = "SALESMAN";

        Emp emp = sqlTemplate().forObject("sql/selectSingleByParam.sql", Emp.class, param);
        assertThat(emp.empno, is(7499));
    }

    @Test
    public void testForObject_SingleArg() {
        Emp emp = sqlTemplate().forObject("sql/selectByEmpno.sql", Emp.class, 7839);
        assertThat(emp.empno, is(7839));
    }

    @Test
    public void testForObject_MultiArg() {
        Emp emp = sqlTemplate().forObject("sql/selectSingleByArgs.sql", Emp.class, 30, "SALESMAN");
        assertThat(emp.empno, is(7499));
    }

    @Test
    public void testForObject_ReturnSimple() {
        Integer result = sqlTemplate().forObject("sql/selectSingleEmpno.sql", Integer.class);
        assertThat(result, is(7369));
    }

    @Test
    public void testForList_NoArg() {
        List<Emp> result = sqlTemplate().forList("sql/selectAll.sql", Emp.class);
        assertThat(result.size(), is(14));
        assertThat(result.get(0).empno, is(7369));
        assertThat(result.get(13).empno, is(7934));
    }

    @Test
    public void testForList_MapArg() {
        Map<String, Object> param = new HashMap<>();
        param.put("deptno", 30);
        param.put("job", "SALESMAN");

        List<Emp> result = sqlTemplate().forList("sql/selectByParam.sql", Emp.class, param);
        assertThat(result.size(), is(4));
        assertThat(result.get(0).empno, is(7499));
        assertThat(result.get(3).empno, is(7844));
    }

    @Test
    public void testForList_EntityArg() {
        Emp param = new Emp();
        param.deptno = 30;
        param.job = "SALESMAN";

        List<Emp> result = sqlTemplate().forList("sql/selectByParam.sql", Emp.class, param);
        assertThat(result.size(), is(4));
        assertThat(result.get(0).empno, is(7499));
        assertThat(result.get(3).empno, is(7844));
    }

    @Test
    public void testForList_SingleArg() {
        List<Emp> result = sqlTemplate().forList("sql/selectByDeptno.sql", Emp.class, 10);
        assertThat(result.size(), is(3));
        assertThat(result.get(0).empno, is(7782));
        assertThat(result.get(2).empno, is(7934));
    }

    @Test
    public void testForList_MultiArg() {
        List<Emp> result = sqlTemplate().forList("sql/selectByArgs.sql", Emp.class, 30, "SALESMAN");
        assertThat(result.size(), is(4));
        assertThat(result.get(0).empno, is(7499));
        assertThat(result.get(3).empno, is(7844));
    }

    @Test
    public void testForList_ReturnSimple() {
        List<Integer> result = sqlTemplate().forList("sql/selectEmpno.sql", Integer.class);
        assertThat(result.size(), is(14));
        assertThat(result.get(0), is(7369));
        assertThat(result.get(13), is(7934));
    }

    @Test
    public void testForMap_NoArgs() {
        Map<String, Object> result = sqlTemplate().forMap("sql/selectSingle.sql");
        assertThat(result.get("empno"), is(7369));
    }

    @Test
    public void testForMap_MapArg() {
        Map<String, Object> param = new HashMap<>();
        param.put("deptno", 30);
        param.put("job", "SALESMAN");

        Map<String, Object> result = sqlTemplate().forMap("sql/selectSingleByParam.sql", param);
        assertThat(result.get("empno"), is(7499));
    }

    @Test
    public void testForMap_EntityArg() {
        Emp param = new Emp();
        param.deptno = 30;
        param.job = "SALESMAN";

        Map<String, Object> result = sqlTemplate().forMap("sql/selectSingleByParam.sql", param);
        assertThat(result.get("empno"), is(7499));
    }

    @Test
    public void testForMap_SingleArg() {
        Map<String, Object> result = sqlTemplate().forMap("sql/selectByEmpno.sql", 7839);
        assertThat(result.get("empno"), is(7839));
    }

    @Test
    public void testForMap_MultiArg() {
        Map<String, Object> result = sqlTemplate().forMap("sql/selectSingleByArgs.sql", 30, "SALESMAN");
        assertThat(result.get("empno"), is(7499));
    }

    @Test
    public void testForMap_ReturnSimple() {
        Map<String, Object> result = sqlTemplate().forMap("sql/selectSingleEmpno.sql");
        assertThat(result.size(), is(1));
        assertThat(result.get("empno"), is(7369));
    }


    @Test
    public void testForListMap_NoArg() {
        List<Map<String, Object>> result = sqlTemplate().forList("sql/selectAll.sql");
        assertThat(result.size(), is(14));
        assertThat(result.get(0).get("empno"), is(7369));
        assertThat(result.get(13).get("empno"), is(7934));
    }

    @Test
    public void testForListMap_MapArg() {
        Map<String, Object> param = new HashMap<>();
        param.put("deptno", 30);
        param.put("job", "SALESMAN");

        List<Map<String, Object>> result = sqlTemplate().forList("sql/selectByParam.sql", param);
        assertThat(result.size(), is(4));
        assertThat(result.get(0).get("empno"), is(7499));
        assertThat(result.get(3).get("empno"), is(7844));
    }

    @Test
    public void testForListMap_EntityArg() {
        Emp param = new Emp();
        param.deptno = 30;
        param.job = "SALESMAN";

        List<Map<String, Object>> result = sqlTemplate().forList("sql/selectByParam.sql", param);
        assertThat(result.size(), is(4));
        assertThat(result.get(0).get("empno"), is(7499));
        assertThat(result.get(3).get("empno"), is(7844));
    }

    @Test
    public void testForListMap_SingleArg() {
        List<Map<String, Object>> result = sqlTemplate().forList("sql/selectByDeptno.sql", 10);
        assertThat(result.size(), is(3));
        assertThat(result.get(0).get("empno"), is(7782));
        assertThat(result.get(2).get("empno"), is(7934));
    }

    @Test
    public void testForListMap_MultiArg() {
        List<Map<String, Object>> result = sqlTemplate().forList("sql/selectByArgs.sql", 30, "SALESMAN");
        assertThat(result.size(), is(4));
        assertThat(result.get(0).get("empno"), is(7499));
        assertThat(result.get(3).get("empno"), is(7844));
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

        int count = sqlTemplate().update("sql/insertByParam.sql", emp);
        assertThat(count, is(1));

        Emp result = sqlTemplate().forObject("sql/selectByEmpno.sql", Emp.class, 1000);
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

        int count = sqlTemplate().update("sql/updateByParam.sql", param);
        assertThat(count, is(1));

        Emp result = sqlTemplate().forObject("sql/selectByEmpno.sql", Emp.class, 7876);
        assertThat(result.job, is("ANALYST"));
        assertThat(result.mgr, is(7566));
    }

    @Test
    public void testUpdate_deleteByArg() {
        int count = sqlTemplate().update("sql/deleteByArg.sql", 7566);
        assertThat(count, is(1));

        Emp result = sqlTemplate().forObject("sql/selectByEmpno.sql", Emp.class, 7566);
        assertNull(result);
    }

    @Test
    public void testUpdate_deleteByArgs() {
        int count = sqlTemplate().update("sql/deleteByArgs.sql", 30, "SALESMAN");
        assertThat(count, is(4));

        List<Emp> result = sqlTemplate().forList("sql/selectByDeptno.sql", Emp.class, 30);
        assertThat(result.size(), is(2));
        assertThat(result.get(0).empno, is(7698));
        assertThat(result.get(1).empno, is(7900));
    }

    @Test
    public void testQuery_forList() {
        List<Emp> result = sqlTemplate().query("sql/selectByParam.sql", Emp.class)
                .add("job", "SALESMAN")
                .add("deptno", 30)
                .forList();
        assertThat(result.size(), is(4));
        assertThat(result.get(0).empno, is(7499));
        assertThat(result.get(3).empno, is(7844));
    }

    @Test
    public void testQuery_forObject() {
        Emp emp = sqlTemplate().query("sql/selectSingleByParam.sql", Emp.class)
                .add("job", "SALESMAN").add("deptno", 30)
                .forObject();
        assertThat(emp.empno, is(7499));
    }

    @Test
    public void testForObject_noFile() {
        try {
            Emp emp = sqlTemplate().forObject("x", Emp.class);
            fail();
        } catch (UncheckedIOException ex) {
            assertThat(ex.getCause().getMessage(), is("Template 'x' not found"));
        }
    }

    @Test
    public void testForObject_DateTime() {
        // prepare
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Tokyo")));

        // execute
        SqlTemplate template = new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate, new PlainText());
        DateTimeEntity result = template.forObject("SELECT * FROM date_time", DateTimeEntity.class);

        // assert
        assertThat(result.utilDate.toString(), is("2001-01-23 12:34:56.789"));
        assertThat(result.sqlDate.toString(), is("2001-01-24"));
        assertThat(result.sqlTime.toString(), is("12:34:57"));
        assertThat(result.sqlTimestamp.toString(), is("2001-01-25 12:34:58.789"));
        assertThat(result.localDateTime.toString(), is("2001-01-26T12:34:59.789"));
        assertThat(result.localDate.toString(), is("2001-01-27"));
        assertThat(result.localTime.toString(), is("12:35:01"));
        assertThat(result.zonedDateTime.toString(), is("2001-01-28T12:35:02.789+09:00[Asia/Tokyo]"));
        assertThat(result.offsetDateTime.toString(), is("2001-01-29T12:35:03.789+09:00"));
        assertThat(result.offsetTime.toString(), is("12:35:04+09:00"));
    }

    @Test
    public void testForList_SingleDateTime() {
        // prepare
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Tokyo")));

        // execute
        // assert
        SqlTemplate template = new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate, new PlainText());

        List<LocalDateTime> localDateTime = template.forList("SELECT local_date_time FROM date_time", LocalDateTime.class);
        assertThat(localDateTime.get(0).toString(), is("2001-01-26T12:34:59.789"));

        List<LocalDate> localDate= template.forList("SELECT local_date FROM date_time", LocalDate.class);
        assertThat(localDate.get(0).toString(), is("2001-01-27"));

        List<LocalTime> localTime = template.forList("SELECT local_time FROM date_time", LocalTime.class);
        assertThat(localTime.get(0).toString(), is("12:35:01"));
    }

    @Test
    public void testForObject_SingleDateTime() {
        // prepare
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Tokyo")));

        // execute
        // assert
        SqlTemplate template = new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate, new PlainText());

        LocalDateTime localDateTime = template.forObject("SELECT local_date_time FROM date_time", LocalDateTime.class);
        assertThat(localDateTime.toString(), is("2001-01-26T12:34:59.789"));

        LocalDate localDate= template.forObject("SELECT local_date FROM date_time", LocalDate.class);
        assertThat(localDate.toString(), is("2001-01-27"));

        LocalTime localTime = template.forObject("SELECT local_time FROM date_time", LocalTime.class);
        assertThat(localTime.toString(), is("12:35:01"));
    }

    @Test
    public void testForListMap_DateTime() {
        // prepare
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Tokyo")));

        // execute
        SqlTemplate template = new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate, new PlainText());
        List<Map<String, Object>> maps = template.forList("SELECT * FROM date_time");

        // assert
        Map<String, Object> result = maps.get(0);
        assertThat(result.get("local_date_time"), instanceOf(Timestamp.class));
        assertThat(result.get("local_date_time").toString(), is("2001-01-26 12:34:59.789"));
        assertThat(result.get("local_date"), instanceOf(java.sql.Date.class));
        assertThat(result.get("local_date").toString(), is("2001-01-27"));
        assertThat(result.get("local_time"), instanceOf(Time.class));
        assertThat(result.get("local_time").toString(), is("12:35:01"));
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
        SqlTemplate template = new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate, new PlainText());
        int num = template.update("INSERT INTO date_time VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                entity.utilDate, entity.sqlDate, entity.sqlTime, entity.sqlTimestamp, entity.localDateTime,
                entity.localDate, entity.localTime, entity.zonedDateTime, entity.offsetDateTime, entity.offsetTime);

        // assert
        assertThat(num, is(1));

        DateTimeEntity result = template.forObject("SELECT * FROM date_time", DateTimeEntity.class);
        assertThat(result.utilDate.toString(), is("2001-01-23 12:34:56.789"));
        assertThat(result.sqlDate.toString(), is("2001-01-24"));
        assertThat(result.sqlTime.toString(), is("12:34:57"));
        assertThat(result.sqlTimestamp.toString(), is("2001-01-25 12:34:58.789"));
        assertThat(result.localDateTime.toString(), is("2001-01-26T12:34:59.789"));
        assertThat(result.localDate.toString(), is("2001-01-27"));
        assertThat(result.localTime.toString(), is("12:35:01"));
        assertThat(result.zonedDateTime.toString(), is("2001-01-28T12:35:02.789+09:00[Asia/Tokyo]"));
        assertThat(result.offsetDateTime.toString(), is("2001-01-29T12:35:03.789+09:00"));
        assertThat(result.offsetTime.toString(), is("12:35:04+09:00"));
    }

    @Test
    public void testUpdate_DateTime_Null() {
        // prepare
        jdbcTemplate.update("DELETE from date_time");
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Tokyo")));
        DateTimeEntity entity = new DateTimeEntity();

        // execute
        SqlTemplate template = new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate, new PlainText());
        int num = template.update("INSERT INTO date_time VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                entity.utilDate, entity.sqlDate, entity.sqlTime, entity.sqlTimestamp, entity.localDateTime,
                entity.localDate, entity.localTime, entity.zonedDateTime, entity.offsetDateTime, entity.offsetTime);

        // assert
        assertThat(num, is(1));

        DateTimeEntity result = template.forObject("SELECT * FROM date_time", DateTimeEntity.class);

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
        SqlTemplate template = new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate, new PlainText(), ZoneId.of("GMT"));
        int num = template.update("INSERT INTO date_time VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                entity.utilDate, entity.sqlDate, entity.sqlTime, entity.sqlTimestamp, entity.localDateTime,
                entity.localDate, entity.localTime, entity.zonedDateTime, entity.offsetDateTime, entity.offsetTime);

        // assert
        assertThat(num, is(1));

        DateTimeEntity result = template.forObject("SELECT * FROM date_time", DateTimeEntity.class);
        assertThat(result.utilDate.toString(), is("2001-01-23 12:34:56.789"));
        assertThat(result.sqlDate.toString(), is("2001-01-24"));
        assertThat(result.sqlTime.toString(), is("12:34:57"));
        assertThat(result.sqlTimestamp.toString(), is("2001-01-25 12:34:58.789"));
        assertThat(result.localDateTime.toString(), is("2001-01-26T12:34:59.789"));
        assertThat(result.localDate.toString(), is("2001-01-27"));
        assertThat(result.localTime.toString(), is("12:35:01"));
        assertThat(result.zonedDateTime.toString(), is("2001-01-28T03:35:02.789Z[GMT]"));
        assertThat(result.offsetDateTime.toString(), is("2001-01-29T03:35:03.789Z"));
        assertThat(result.offsetTime.toString(), is("03:35:04Z"));
    }

    @Test
    public void testUpdateByAdd() {
        int count = sqlTemplate().update("sql/updateByParam.sql")
                .add("job", "TEST")
                .add("mgr", 1234)
                .add("empno", 7876)
                .execute();
        assertThat(count, is(1));

        Emp result = sqlTemplate().forObject("sql/selectByEmpno.sql", Emp.class, 7876);
        assertThat(result.job, is("TEST"));
        assertThat(result.mgr, is(1234));
    }

    SqlTemplate sqlTemplate() {
        return new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate);
    }
}
