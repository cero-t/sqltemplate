package ninja.cero.sqltemplate.core;

import ninja.cero.sqltemplate.test.FreeMarkerConfig;
import ninja.cero.sqltemplate.test.entity.Emp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FreeMarkerConfig.class)
@Transactional
public class FreeMarkerTest {
    @Autowired
    SqlTemplate template;

    @Test
    public void testForList_MapArgs() {
        Map<String, Object> param = new HashMap<>();
        param.put("deptno", 30);
        param.put("job", "SALESMAN");

        List<Emp> result = template.forList("ftl/selectByArgs.sql", Emp.class, param);
        assertThat(result.size(), is(4));
        assertThat(result.get(0).empno, is(7499));
        assertThat(result.get(3).empno, is(7844));
    }

    @Test
    public void testForList_empty() {
        List<Emp> result = template.forList("ftl/selectByArgs.sql", Emp.class);
        assertThat(result.size(), is(14));
        assertThat(result.get(0).empno, is(7369));
        assertThat(result.get(13).empno, is(7934));
    }

    @Test
    public void testForObject_noFile() {
        try {
            Emp emp = template.forObject("x", Emp.class);
            fail();
        } catch (UncheckedIOException ex) {
            assertThat(ex.getCause().getMessage(), is("Template \"x\" not found. The quoted name was interpreted by this template loader: ClassTemplateLoader(baseClass=ninja.cero.sqltemplate.core.template.FreeMarker, packagePath=\"/\")."));
        }
    }
}
