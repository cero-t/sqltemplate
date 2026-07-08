package ninja.cero.sqltemplate.core.template;

import org.junit.jupiter.api.Test;

import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertEquals("SELECT", results[i++]);
        assertEquals("        *", results[i++]);
        assertEquals("    FROM", results[i++]);
        assertEquals("        emp", results[i++]);
        assertEquals("    WHERE", results[i++]);
        assertEquals("        1 = 1", results[i++]);
        assertEquals("        AND job = :job", results[i++]);
        assertEquals("        AND deptno = :deptno", results[i++]);
        assertEquals(i, results.length);
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
        assertEquals("SELECT", results[i++]);
        assertEquals("        *", results[i++]);
        assertEquals("    FROM", results[i++]);
        assertEquals("        emp", results[i++]);
        assertEquals("    WHERE", results[i++]);
        assertEquals("        1 = 1", results[i++]);
        assertEquals("        AND job = :job", results[i++]);
        assertEquals("        AND mgr = :mgr", results[i++]);
        assertEquals("        AND deptno = :deptno", results[i++]);
        assertEquals(i, results.length);
    }

    @Test
    public void testGet_TemplateNotFound() {
        UncheckedIOException ex = assertThrows(UncheckedIOException.class,
                () -> new FreeMarker().get("x", ""));
        assertTrue(ex.getMessage().contains("not found"));
    }

    public static class Param {
        public Integer deptno;
        public String job;
        public Integer mgr;
    }
}