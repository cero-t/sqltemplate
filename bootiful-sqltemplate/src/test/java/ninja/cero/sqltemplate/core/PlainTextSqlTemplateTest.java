package ninja.cero.sqltemplate.core;

import ninja.cero.sqltemplate.test.TestConfig;
import ninja.cero.sqltemplate.test.entity.Emp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class PlainTextSqlTemplateTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    public void testForObject_NoArgs() {
        Emp emp = sqlTemplate()
                .query("select * from emp where empno=7369")
                .forObject(Emp.class);
        assertEquals(7369, emp.empno);
    }

    SqlTemplate sqlTemplate() {
        return new PlainTextSqlTemplate(jdbcTemplate, namedParameterJdbcTemplate);
    }
}
