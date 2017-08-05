package ninja.cero.sqltemplate.core.stream;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetIteratorTest {

    private String currentRow = null;

    private List<String> remainingRows = new ArrayList<>(Arrays.asList("foo", "bar", "baz"));

    private final RowMapper<String> mapper = (rs, rowNum) -> {
        assertThat(currentRow, not((Object) null));
        return currentRow;
    };

    private final InvocationHandler invocationHandler = (proxy, method, args) -> {
        assertThat(method.getName(), is("next"));
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
        assertThat(it.hasNext(), is(true));
        assertThat(it.next(), is("foo"));

        assertThat(it.hasNext(), is(true));
        assertThat(it.next(), is("bar"));

        assertThat(it.hasNext(), is(true));
        assertThat(it.next(), is("baz"));

        assertThat(it.hasNext(), is(false));
        assertThat(it.hasNext(), is(false));

        try {
            it.next();
            fail();
        } catch (NoSuchElementException ex) {
            assertTrue(true);
        }
    }

    @Test
    public void test_resultSetNext_return_successfully() {
        assertThat(it.wrapSqlException(() -> 42), is(42));
    }

    @Test
    public void test_resultSetNext_throw_wrapping_exception() {
        SQLException sqlException = new SQLException("something wrong");
        try {
            it.wrapSqlException(() -> { throw sqlException; });
        } catch (DataAccessException daException) {
            assertThat(daException.getCause(), is(sqlException));
        }
    }

}
