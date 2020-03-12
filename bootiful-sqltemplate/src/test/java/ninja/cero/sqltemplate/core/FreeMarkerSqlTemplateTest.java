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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class FreeMarkerSqlTemplateTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    public void testForList_MapArgs() {
        Map<String, Object> param = new HashMap<>();
        param.put("deptno", 30);
        param.put("job", "SALESMAN");

        List<Emp> result = sqlTemplate().forList("ftl/selectByArgs.sql", Emp.class, param);
        assertEquals(4, result.size());
        assertEquals(7499, result.get(0).empno);
        assertEquals(7844, result.get(3).empno);
    }

    @Test
    public void testForList_empty() {
        List<Emp> result = sqlTemplate().forList("ftl/selectByArgs.sql", Emp.class);
        assertEquals(14, result.size());
        assertEquals(7369, result.get(0).empno);
        assertEquals(7934, result.get(13).empno);
    }

    SqlTemplate sqlTemplate() {
        return new FreeMarkerSqlTemplate(jdbcTemplate, namedParameterJdbcTemplate);
    }
}
