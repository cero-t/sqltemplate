package ninja.cero.sqltemplate.core.parameter;

import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * {@inheritDoc}
 * Supports {@link java.time.LocalDateTime} and {@link java.time.LocalDate} of JSR-310
 */
public class ArgsParameter extends ArgumentPreparedStatementSetter {
    /**
     * Create a new ArgsParameter for the given arguments.
     * @param args the arguments to set
     * @return ArgsParameter instance
     */
    public static ArgsParameter of(Object... args) {
        return new ArgsParameter(args);
    }

    /**
     * Create a new ArgPreparedStatementSetter for the given arguments.
     * @param args the arguments to set
     */
    public ArgsParameter(Object[] args) {
        super(args);
    }

    /**
     * {@inheritDoc}
     */
    protected void doSetValue(PreparedStatement ps, int parameterPosition, Object argValue) throws SQLException {
        if (argValue instanceof LocalDate) {
            LocalDate paramValue = (LocalDate) argValue;
            ps.setDate(parameterPosition, Date.valueOf(paramValue));
        } else if (argValue instanceof LocalDateTime) {
            LocalDateTime paramValue = (LocalDateTime) argValue;
            ps.setTimestamp(parameterPosition, Timestamp.valueOf(paramValue));
        } else {
            super.doSetValue(ps, parameterPosition, argValue);
        }
    }
}
