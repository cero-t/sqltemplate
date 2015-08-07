package ninja.cero.sqltemplate.core.parameter;

import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * {@inheritDoc}
 * Supports {@link java.time.LocalDateTime} and {@link java.time.LocalDate} of JSR-310
 */
public class ArgsParameter extends ArgumentPreparedStatementSetter {
    protected ZoneId zoneId = ZoneId.systemDefault();

    /**
     * Create a new ArgsParameter for the given arguments.
     * @param args the arguments to set
     * @return ArgsParameter instance
     */
    public static ArgsParameter of(Object... args) {
        return new ArgsParameter(args);
    }

    /**
     * Create a new ArgsParameter for the given arguments.
     * @param args the arguments to set
     * @return ArgsParameter instance
     */
    public static ArgsParameter of(ZoneId zoneId, Object... args) {
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
     * Create a new ArgPreparedStatementSetter for the given arguments.
     * @param args the arguments to set
     */
    public ArgsParameter(Object[] args, ZoneId zoneId) {
        super(args);
        this.zoneId = zoneId;
    }

    /**
     * {@inheritDoc}
     */
    protected void doSetValue(PreparedStatement ps, int parameterPosition, Object argValue) throws SQLException {
        super.doSetValue(ps, parameterPosition, convertDate(argValue));
    }

    private Object convertDate(Object value) {
        if (value instanceof LocalDateTime) {
            return Timestamp.valueOf((LocalDateTime) value);
        } else if (value instanceof LocalDate) {
            return Date.valueOf((LocalDate) value);
        } else if (value instanceof LocalTime) {
            return Time.valueOf((LocalTime) value);
        } else if (value instanceof OffsetDateTime) {
            ZonedDateTime zonedDateTime = ((OffsetDateTime) value).atZoneSameInstant(zoneId);
            return Timestamp.valueOf(zonedDateTime.toLocalDateTime());
        } else if (value instanceof ZonedDateTime) {
            ZonedDateTime zonedDateTime = ((ZonedDateTime) value).withZoneSameInstant(zoneId);
            return Timestamp.valueOf(zonedDateTime.toLocalDateTime());
        }
        return value;
    }
}
