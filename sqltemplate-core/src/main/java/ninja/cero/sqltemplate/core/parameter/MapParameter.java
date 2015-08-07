package ninja.cero.sqltemplate.core.parameter;

import org.springframework.jdbc.core.namedparam.AbstractSqlParameterSource;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * {@link org.springframework.jdbc.core.namedparam.SqlParameterSource} implementation that holds a given Map of parameters.
 * Supports {@link java.time.LocalDateTime} and {@link java.time.LocalDate} of JSR-310.
 */
public class MapParameter extends AbstractSqlParameterSource {
    /** the Map holding parameters */
    protected Map<String, Object> values;

    /**
     * Create a new MapParameter.
     * @param values the Map holding parameters
     * @return a new MapParameter
     */
    public static MapParameter of(Map<String, Object> values) {
        return new MapParameter(values);
    }

    /**
     * Create a new MapParameter.
     * @param values the Map holding parameters
     */
    protected MapParameter(Map<String, Object> values) {
        this.values = values;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasValue(String paramName) {
        return values.containsKey(paramName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(String paramName) {
        Object value = values.get(paramName);
        if (value == null) {
            return null;
        }

        if (value instanceof LocalDateTime) {
            return Timestamp.valueOf((LocalDateTime) value);
        } else if (value instanceof LocalDate) {
            return Date.valueOf((LocalDate) value);
        } else if (value instanceof LocalTime) {
            return Time.valueOf((LocalTime) value);
        } else if (value instanceof OffsetDateTime) {
            ZonedDateTime zonedDateTime = ((OffsetDateTime) value).atZoneSameInstant(ZoneId.systemDefault());
            return Timestamp.valueOf(zonedDateTime.toLocalDateTime());
        } else if (value instanceof ZonedDateTime) {
            ZonedDateTime zonedDateTime = ((ZonedDateTime) value).withZoneSameInstant(ZoneId.systemDefault());
            return Timestamp.valueOf(zonedDateTime.toLocalDateTime());
        }

        return value;
    }
}
