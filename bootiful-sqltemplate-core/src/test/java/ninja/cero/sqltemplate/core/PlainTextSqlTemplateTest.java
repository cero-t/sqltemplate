package ninja.cero.sqltemplate.core;

import ninja.cero.sqltemplate.test.TestConfig;
import ninja.cero.sqltemplate.test.entity.Emp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class PlainTextSqlTemplateTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    public void testForObject_NoArgs() {
        Emp emp = sqlTemplate().forObject("select * from emp where empno=7369", Emp.class);
        assertThat(emp.empno, is(7369));
    }

    SqlTemplate sqlTemplate() {
        return new PlainTextSqlTemplate(jdbcTemplate, namedParameterJdbcTemplate);
    }
}
