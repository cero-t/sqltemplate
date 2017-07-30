package ninja.cero.sqltemplate.core.stream;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamResultSetExtractor<T, U> implements ResultSetExtractor<U> {

    private final String sql;

    private final RowMapper<T> mapper;

    private final Function<? super Stream<T>, U> handleStream;

    private final SQLExceptionTranslator excTranslator;

    public StreamResultSetExtractor(
            String sql,
            RowMapper<T> mapper,
            Function<? super Stream<T>, U> handleStream,
            SQLExceptionTranslator excTranslator) {
        this.sql = sql;
        this.mapper = mapper;
        this.handleStream = handleStream;
        this.excTranslator = excTranslator;
    }

    @Override
    public U extractData(ResultSet rs) {
        try (Stream<T> stream = makeStream(rs, mapper)) {
            return handleStream.apply(stream);
        }
    }

    private <T> Stream<T> makeStream(ResultSet rs, RowMapper<T> mapper) {
        Iterator<T> it = makeIterator(rs, mapper);
        Iterable<T> iterable = () -> it;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private <T> Iterator<T> makeIterator(ResultSet rs, RowMapper<T> mapper) {
        return new Iterator<T>() {
            private T object = null;
            private boolean hasNext = true;
            private void fetch() {
                if (this.object != null || ! this.hasNext) {
                    return;
                }
                this.hasNext = wrapSqlException(() -> rs.next());
                if (this.hasNext) {
                    this.object = wrapSqlException(() -> mapper.mapRow(rs, 1));
                }
            }
            @Override public boolean hasNext() {
                fetch();
                return this.hasNext;
            }
            @Override public T next() {
                fetch();
                T result = this.object;
                this.object = null;
                return result;
            }
        };
    }

    private <R> R wrapSqlException(SqlSupplier<R> supplier) {
        try {
            return supplier.get();
        } catch (SQLException sqlException) {
            throw excTranslator.translate("StreamResultSetExtractor", this.sql, sqlException);
        }
    }

    @FunctionalInterface
    private interface SqlSupplier<R> {
        public abstract R get() throws SQLException;
    }

}
