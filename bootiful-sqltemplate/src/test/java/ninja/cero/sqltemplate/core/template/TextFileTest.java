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
        assertEquals(results[i++], "SELECT");
        assertEquals(results[i++], "        *");
        assertEquals(results[i++], "    FROM");
        assertEquals(results[i++], "        emp");
        assertEquals(results[i++], "    WHERE");
        assertEquals(results[i++], "        deptno = ?");
        assertEquals(results[i++], "        AND job = ?");
        assertEquals(results[i++], "    ORDER BY");
        assertEquals(results[i++], "        empno");
        assertEquals(i, results.length);
    }

    @Test
    public void testGet_Object() throws Exception {
        // execute
        String result = new TextFile().get("sql/selectByArgs.sql", "1");

        // assert
        String[] results = result.split("\n");

        int i = 0;
        assertEquals(results[i++], "SELECT");
        assertEquals(results[i++], "        *");
        assertEquals(results[i++], "    FROM");
        assertEquals(results[i++], "        emp");
        assertEquals(results[i++], "    WHERE");
        assertEquals(results[i++], "        deptno = ?");
        assertEquals(results[i++], "        AND job = ?");
        assertEquals(results[i++], "    ORDER BY");
        assertEquals(results[i++], "        empno");
        assertEquals(i, results.length);
    }
}
