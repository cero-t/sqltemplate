package ninja.cero.sqltemplate.core.stream;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Iterator over a ResultSet which converts each row by a RowMapper.
 *
 * <p>{@link #hasNext()} and {@link #next()} throws a
 * {@link org.springframework.dao.DataAccessException} if the underlying ResultSet
 * throws an {@link SQLException}.</p>
 *
 * @param <T> the row type
 */
class ResultSetIterator<T> implements Iterator<T> {

    /** The SQL. */
    private final String sql;

    /** The result set to iterate over. */
    private final ResultSet rs;

    /** The mapper which converts each row. */
    private final RowMapper<T> mapper;

    /** The translator of {@link SQLException}s. */
    private final SQLExceptionTranslator excTranslator;

    /** The fetched row. */
    private T row = null;

    /** true if the iterator has reached the end-of-stream. */
    private boolean hasReachedEos = false;

    /**
     * Constructs an iterator.
     *
     * @param sql the SQL
     * @param rs the result set to iterate over
     * @param mapper the mapper which converts each row
     * @param excTranslator the translator of {@link SQLException}s.
     */
    ResultSetIterator(
            String sql,
            ResultSet rs,
            RowMapper<T> mapper,
            SQLExceptionTranslator excTranslator) {
        this.sql = sql;
        this.rs = rs;
        this.mapper = mapper;
        this.excTranslator = excTranslator;
    }

    @Override
    public boolean hasNext() {
        return fetchRow().isPresent();
    }

    @Override
    public T next() {
        return fetchRow()
            .map(r -> {
                this.row = null;
                return r;
            })
            .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Fetches a row if not fetched yet.
     */
    private Optional<T> fetchRow() {
        if (this.row != null) {
            // already fetched
            return Optional.of(this.row);
        }

        this.hasReachedEos = hasReachedEos || ! wrapSqlException(() -> rs.next());
        if (this.hasReachedEos) {
            return Optional.empty();
        }

        this.row = wrapSqlException(() -> mapper.mapRow(rs, 1));
        return Optional.ofNullable(this.row);
    }

    /**
     * Performs an action which may throw an {@link SQLException}.
     * If SQLException is thrown, it converts the exception to
     * {@link org.springframework.dao.DataAccessException}.
     */
    <R> R wrapSqlException(SqlAction<R> action) {
        try {
            return action.perform();
        } catch (SQLException sqlException) {
            throw excTranslator.translate("StreamResultSetExtractor", sql, sqlException);
        }
    }

    /**
     * An action which may throw an {@link SQLException}.
     *
     * @param <R> the result type
     */
    @FunctionalInterface
    interface SqlAction<R> {

        /**
         * Performs the action.
         */
        public abstract R perform() throws SQLException;

    }

}
