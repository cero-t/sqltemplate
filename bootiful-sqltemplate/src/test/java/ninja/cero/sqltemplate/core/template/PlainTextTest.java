package ninja.cero.sqltemplate.core.template;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlainTextTest {
    @Test
    public void testGet_Args() throws Exception {
        String result = new PlainText().get("select * from emp", new String[]{"1", "2"});
        assertEquals("select * from emp", result);
    }

    @Test
    public void testGet_Object() throws Exception {
        String result = new PlainText().get("select * from emp", "1");
        assertEquals("select * from emp", result);
    }
}