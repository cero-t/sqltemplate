package ninja.cero.sqltemplate.core.template;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TextFileTest {
    @Test
    public void testGet_Args() throws Exception {
        // execute
        String result = new TextFile().get("sql/selectByArgs.sql", new String[]{"1", "2"});

        // assert
        String[] results = result.split("\n");

        int i = 0;
        assertThat("SELECT", is(results[i++]));
        assertThat("        *", is(results[i++]));
        assertThat("    FROM", is(results[i++]));
        assertThat("        emp", is(results[i++]));
        assertThat("    WHERE", is(results[i++]));
        assertThat("        deptno = ?", is(results[i++]));
        assertThat("        AND job = ?", is(results[i++]));
        assertThat("    ORDER BY", is(results[i++]));
        assertThat("        empno", is(results[i++]));
        assertThat(results.length, is(i));
    }

    @Test
    public void testGet_Object() throws Exception {
        // execute
        String result = new TextFile().get("sql/selectByArgs.sql", "1");

        // assert
        String[] results = result.split("\n");

        int i = 0;
        assertThat("SELECT", is(results[i++]));
        assertThat("        *", is(results[i++]));
        assertThat("    FROM", is(results[i++]));
        assertThat("        emp", is(results[i++]));
        assertThat("    WHERE", is(results[i++]));
        assertThat("        deptno = ?", is(results[i++]));
        assertThat("        AND job = ?", is(results[i++]));
        assertThat("    ORDER BY", is(results[i++]));
        assertThat("        empno", is(results[i++]));
        assertThat(results.length, is(i));
    }
}
