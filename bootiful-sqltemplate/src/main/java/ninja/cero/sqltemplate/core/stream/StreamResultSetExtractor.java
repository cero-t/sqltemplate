package ninja.cero.sqltemplate.core.stream;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import java.sql.ResultSet;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Extractor which converts a ResultSet into a row stream,
 * then applies the handler function to the whole stream to extract a result.
 *
 * <p>This class is intended for internal use,
 * thus does not constitute the library API.</p>
 *
 * @param <T> the row type
 * @param <U> the result type
 */
public class StreamResultSetExtractor<T, U> implements ResultSetExtractor<U> {

    /** The SQL. */
    private final String sql;

    /** The row mapper. */
    private final RowMapper<T> mapper;

    /** The handler function which extracts a result from the row stream. */
    private final Function<? super Stream<T>, U> handleStream;

    /** The translator of SQLException to DataAccessException. */
    private final SQLExceptionTranslator excTranslator;

    /**
     * Constructs an extractor.
     *
     * @param sql the SQL
     * @param mapper the row mapper
     * @param handleStream the handler function which extracts a result from the row stream
     * @param excTranslator the translator of SQL to DataAccessException
     */
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

    /**
     * Applies {@code handleStream} to the row stream converted from the ResultSet
     * to extract a result.
     *
     * @param rs the ResultSet
     * @return the result of {@code handleStream}
     */
    @Override
    public U extractData(ResultSet rs) {
        Iterable<T> iterable = () -> new ResultSetIterator(sql, rs, mapper, excTranslator);
        try (Stream<T> stream = StreamSupport.stream(iterable.spliterator(), false)) {
            return handleStream.apply(stream);
        }
    }
}
