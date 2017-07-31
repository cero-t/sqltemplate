package ninja.cero.sqltemplate.core.stream;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

class ResultSetIterator<T> implements Iterator<T> {

    private final String sql;

    private final ResultSet rs;

    private final RowMapper<T> mapper;

    private final SQLExceptionTranslator excTranslator;

    private T row = null;

    private boolean eos = false;

    ResultSetIterator(String sql, ResultSet rs, RowMapper<T> mapper, SQLExceptionTranslator excTranslator) {
        this.sql = sql;
        this.rs = rs;
        this.mapper = mapper;
        this.excTranslator = excTranslator;
    }

    private Optional<T> fetchRow() {
        if (this.row != null) {
            return Optional.of(this.row);
        }
        this.eos = this.eos || ! resultSetNext();
        if (this.eos) {
            return Optional.empty();
        }
        this.row = mapRow();
        return Optional.ofNullable(this.row);
    }

    @Override public boolean hasNext() {
        return fetchRow().isPresent();
    }

    @Override public T next() {
        return fetchRow()
            .map(r -> {
                this.row = null;
                return r;
            })
            .orElseThrow(NoSuchElementException::new);
    }

    private boolean resultSetNext() {
        try {
            return rs.next();
        } catch (SQLException sqlException) {
            throw excTranslator.translate("StreamResultSetExtractor", sql, sqlException);
        }
    }

    private T mapRow() {
        try {
            return mapper.mapRow(rs, 1);
        } catch (SQLException sqlException) {
            throw excTranslator.translate("StreamResultSetExtractor", sql, sqlException);
        }
    }

}
