package ninja.cero.sqltemplate.core.template;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class FreeMarkerTest {
    @Test
    public void testGet_Map() throws Exception {
        // prepare
        Map<String, Object> param = new HashMap<>();
        param.put("deptno", 30);
        param.put("job", "SALESMAN");

        // execute
        String result = new FreeMarker().get("ftl/selectByArgs.sql", param);

        // assert
        String[] results = result.split("\n");

        int i = 0;
        assertThat("SELECT", is(results[i++]));
        assertThat("        *", is(results[i++]));
        assertThat("    FROM", is(results[i++]));
        assertThat("        emp", is(results[i++]));
        assertThat("    WHERE", is(results[i++]));
        assertThat("        1 = 1", is(results[i++]));
        assertThat("        AND job = :job", is(results[i++]));
        assertThat("        AND deptno = :deptno", is(results[i++]));
        assertThat(results.length, is(i));
    }

    @Test
    public void testGet_Object() throws Exception {
        // prepare
        Param param = new Param();
        param.deptno = 30;
        param.job = "SALESMAN";
        param.mgr = 7698;

        // execute
        String result = new FreeMarker().get("ftl/selectByArgs.sql", param);

        // assert
        String[] results = result.split("\n");

        int i = 0;
        assertThat("SELECT", is(results[i++]));
        assertThat("        *", is(results[i++]));
        assertThat("    FROM", is(results[i++]));
        assertThat("        emp", is(results[i++]));
        assertThat("    WHERE", is(results[i++]));
        assertThat("        1 = 1", is(results[i++]));
        assertThat("        AND job = :job", is(results[i++]));
        assertThat("        AND mgr = :mgr", is(results[i++]));
        assertThat("        AND deptno = :deptno", is(results[i++]));
        assertThat(results.length, is(i));
    }

    @Test
    public void testGet_TemplateNotFound() throws Exception {
        // execute
        try {
            new FreeMarker().get("x", "");
            fail("Expected exception not occurred");
        } catch (IOException ex) {
            // assert
            assertThat(ex.getMessage(),
                    is("Template \"x\" not found. The quoted name was interpreted by this template loader: ClassTemplateLoader(baseClass=ninja.cero.sqltemplate.core.template.FreeMarker, packagePath=\"/\")."));
        }
    }

    public static class Param {
        public Integer deptno;
        public String job;
        public Integer mgr;
    }
}