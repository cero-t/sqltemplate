package ninja.cero.sqltemplate.core.template;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TextFileTest {
    @Test
    public void testGet_Args() throws Exception {
        // execute
        String result = new TextFile().get("sql/selectByArgs.sql", new String[]{"1", "2"});

        // assert
        String[] results = result.split("\n");

        int i = 0;
        assertEquals("SELECT", results[i++]);
        assertEquals("        *", results[i++]);
        assertEquals("    FROM", results[i++]);
        assertEquals("        emp", results[i++]);
        assertEquals("    WHERE", results[i++]);
        assertEquals("        deptno = ?", results[i++]);
        assertEquals("        AND job = ?", results[i++]);
        assertEquals("    ORDER BY", results[i++]);
        assertEquals("        empno", results[i++]);
        assertEquals(i, results.length);
    }

    @Test
    public void testGet_Object() throws Exception {
        // execute
        String result = new TextFile().get("sql/selectByArgs.sql", "1");

        // assert
        String[] results = result.split("\n");

        int i = 0;
        assertEquals("SELECT", results[i++]);
        assertEquals("        *", results[i++]);
        assertEquals("    FROM", results[i++]);
        assertEquals("        emp", results[i++]);
        assertEquals("    WHERE", results[i++]);
        assertEquals("        deptno = ?", results[i++]);
        assertEquals("        AND job = ?", results[i++]);
        assertEquals("    ORDER BY", results[i++]);
        assertEquals("        empno", results[i++]);
        assertEquals(i, results.length);
    }
}
