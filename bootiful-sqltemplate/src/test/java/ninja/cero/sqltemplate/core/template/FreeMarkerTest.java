package ninja.cero.sqltemplate.core.template;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertEquals(results[i++], "SELECT");
        assertEquals(results[i++], "        *");
        assertEquals(results[i++], "    FROM");
        assertEquals(results[i++], "        emp");
        assertEquals(results[i++], "    WHERE");
        assertEquals(results[i++], "        1 = 1");
        assertEquals(results[i++], "        AND job = :job");
        assertEquals(results[i++], "        AND deptno = :deptno");
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
        assertEquals(results[i++], "SELECT");
        assertEquals(results[i++], "        *");
        assertEquals(results[i++], "    FROM");
        assertEquals(results[i++], "        emp");
        assertEquals(results[i++], "    WHERE");
        assertEquals(results[i++], "        1 = 1");
        assertEquals(results[i++], "        AND job = :job");
        assertEquals(results[i++], "        AND mgr = :mgr");
        assertEquals(results[i++], "        AND deptno = :deptno");
        assertEquals(i, results.length);
    }

    @Test
    public void testGet_TemplateNotFound() throws Exception {
        // execute
        try {
            new FreeMarker().get("x", "");
            throw new RuntimeException("Expected exception not occurred");
        } catch (IOException ex) {
            assertTrue(ex.getMessage().contains("not found"));
        }
    }

    public static class Param {
        public Integer deptno;
        public String job;
        public Integer mgr;
    }
}