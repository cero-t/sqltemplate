package ninja.cero.sqltemplate.core.stream;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class ResultSetIteratorTest {

    private String currentRow = null;

    private List<String> remainingRows = new ArrayList<>(Arrays.asList("foo", "bar", "baz"));

    private final RowMapper<String> mapper = (rs, rowNum) -> {
        assertNotNull(currentRow);
        return currentRow;
    };

    private final InvocationHandler invocationHandler = (proxy, method, args) -> {
        assertEquals("next", method.getName());
        if (remainingRows.isEmpty()) {
            return false;
        }
        this.currentRow = remainingRows.remove(0);
        return true;
    };

    private final ResultSet rs = (ResultSet) Proxy.newProxyInstance(
            ResultSetIteratorTest.class.getClassLoader(),
            new Class<?>[] { ResultSet.class },
            invocationHandler);

    private final SQLExceptionTranslator excTranslator = (task, sql, ex) -> {
        return new DataAccessException(task + " " + sql, ex) {};
    };

    private final ResultSetIterator<String> it = new ResultSetIterator<>(
            "SELECT 1", rs, mapper, excTranslator);

    @Test
    public void test_iteration() {
        assertTrue(it.hasNext());
        assertEquals("foo", it.next());

        assertTrue(it.hasNext());
        assertEquals("bar", it.next());

        assertTrue(it.hasNext());
        assertEquals("baz", it.next());

        assertFalse(it.hasNext());

        try {
            it.next();
            throw new RuntimeException("Test failed");
        } catch (NoSuchElementException ex) {
            assertTrue(true);
        }
    }

    @Test
    public void test_resultSetNext_return_successfully() {
        assertEquals(42, it.wrapSqlException(() -> 42));
    }

    @Test
    public void test_resultSetNext_throw_wrapping_exception() {
        SQLException sqlException = new SQLException("something wrong");
        try {
            it.wrapSqlException(() -> { throw sqlException; });
        } catch (DataAccessException daException) {
            assertEquals(sqlException, daException.getCause());
        }
    }

}
