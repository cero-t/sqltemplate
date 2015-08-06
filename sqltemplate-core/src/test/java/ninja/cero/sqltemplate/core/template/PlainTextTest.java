package ninja.cero.sqltemplate.core.template;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class PlainTextTest {
    @Test
    public void testGet_Args() throws Exception {
        String result = new PlainText().get("select * from emp", new String[]{"1", "2"});
        assertThat(result, is("select * from emp"));
    }

    @Test
    public void testGet_Object() throws Exception {
        String result = new PlainText().get("select * from emp", "1");
        assertThat(result, is("select * from emp"));
    }
}