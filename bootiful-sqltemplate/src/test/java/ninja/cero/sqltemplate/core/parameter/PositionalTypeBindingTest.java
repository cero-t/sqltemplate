package ninja.cero.sqltemplate.core.parameter;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DatabaseMetaData;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for the issue #14 fix. The positional parameter sources ({@link ArrayParameter},
 * {@link BatchArgsParameter}, {@link SingleParamBatchArgsParameter}) now bind each non-null value
 * with the SQL type derived from its runtime class (instead of {@code TYPE_UNKNOWN}), while a
 * {@code null} - which carries no type - is still bound as {@code TYPE_UNKNOWN}.
 *
 * <p>Verified against a recording {@link PreparedStatement} (a dynamic proxy), so it is deterministic
 * and needs no database.
 */
class PositionalTypeBindingTest {

    enum Color {RED, GREEN}

    @Test
    void batchArgs_nonNull_boundWithProperSqlType() throws SQLException {
        Recorder rec = new Recorder();

        new BatchArgsParameter(
                new Object[][]{{42, Color.RED, LocalDate.of(2026, 1, 2)}}, ZoneId.systemDefault())
                .setValues(rec.preparedStatement(), 0);

        // Integer -> INTEGER, passed explicitly to setObject(index, value, sqlType)
        assertEquals(List.of("setObject", 1, 42, Types.INTEGER), rec.call(0));
        // enum -> VARCHAR: value converted to name() and bound as a String
        assertEquals(List.of("setString", 2, "RED"), rec.call(1));
        // LocalDate -> DATE: value converted to java.sql.Date and bound via setDate
        assertEquals(List.of("setDate", 3, Date.valueOf(LocalDate.of(2026, 1, 2))), rec.call(2));
    }

    @Test
    void batchArgs_null_boundAsUnknownType() throws SQLException {
        Recorder rec = new Recorder();

        new BatchArgsParameter(new Object[][]{{null}}, ZoneId.systemDefault())
                .setValues(rec.preparedStatement(), 0);

        // null has no runtime class -> TYPE_UNKNOWN -> Spring falls back to setNull(index, Types.NULL)
        assertEquals(List.of("setNull", 1, Types.NULL), rec.call(0));
    }

    @Test
    void singleParamBatchArgs_nonNull_boundWithProperSqlType() throws SQLException {
        Recorder rec = new Recorder();

        new SingleParamBatchArgsParameter(new Object[]{7L}, ZoneId.systemDefault())
                .setValues(rec.preparedStatement(), 0);

        // Long -> BIGINT
        assertEquals(List.of("setObject", 1, 7L, Types.BIGINT), rec.call(0));
    }

    @Test
    void arrayParameter_nonNull_boundWithProperSqlType() throws SQLException {
        Recorder rec = new Recorder();

        new ArrayParameter(new Object[]{42, Color.RED}, ZoneId.systemDefault())
                .setValues(rec.preparedStatement());

        assertEquals(List.of("setObject", 1, 42, Types.INTEGER), rec.call(0));
        assertEquals(List.of("setString", 2, "RED"), rec.call(1));
    }

    @Test
    void arrayParameter_null_boundAsUnknownType() throws SQLException {
        Recorder rec = new Recorder();

        new ArrayParameter(new Object[]{null}, ZoneId.systemDefault())
                .setValues(rec.preparedStatement());

        assertEquals(List.of("setNull", 1, Types.NULL), rec.call(0));
    }

    /**
     * A {@link PreparedStatement} proxy that records every {@code set*} call. It emulates a driver
     * with no usable parameter metadata (as for a null bound as {@code TYPE_UNKNOWN}): Spring then
     * resolves the null to {@code setNull(index, Types.NULL)}.
     */
    static class Recorder {
        final List<List<Object>> calls = new ArrayList<>();
        final PreparedStatement ps;
        private final Connection conn;
        private final DatabaseMetaData dbmd;
        private final ParameterMetaData pmd;

        Recorder() {
            ClassLoader cl = getClass().getClassLoader();
            InvocationHandler h = (proxy, method, args) -> handle(method, args);
            this.ps = proxy(cl, PreparedStatement.class, h);
            this.conn = proxy(cl, Connection.class, h);
            this.dbmd = proxy(cl, DatabaseMetaData.class, h);
            this.pmd = proxy(cl, ParameterMetaData.class, h);
        }

        @SuppressWarnings("unchecked")
        private static <T> T proxy(ClassLoader cl, Class<T> type, InvocationHandler h) {
            return (T) Proxy.newProxyInstance(cl, new Class[]{type}, h);
        }

        private Object handle(Method method, Object[] args) throws SQLException {
            String name = method.getName();
            if (name.startsWith("set")) {
                List<Object> call = new ArrayList<>();
                call.add(name);
                if (args != null) {
                    for (Object a : args) {
                        call.add(a);
                    }
                }
                calls.add(call);
                return null; // set* is void
            }
            switch (name) {
                case "getConnection":
                    return conn;
                case "getMetaData":
                    return dbmd;
                case "getParameterMetaData":
                    return pmd;
                case "getDriverName":
                    return "H2 JDBC Driver";
                case "getDatabaseProductName":
                    return "H2";
                case "getParameterType":
                    throw new SQLException("no parameter metadata"); // force fallback to Types.NULL
                case "toString":
                    return "recordingPreparedStatement";
                case "hashCode":
                    return 0;
                case "equals":
                    return false;
                default:
                    Class<?> rt = method.getReturnType();
                    if (rt == boolean.class) return false;
                    if (rt == int.class) return 0;
                    if (rt == long.class) return 0L;
                    return null;
            }
        }

        PreparedStatement preparedStatement() {
            return ps;
        }

        List<Object> call(int index) {
            return calls.get(index);
        }
    }
}
