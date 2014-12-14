package springboot.sqltemplate.core.parameter;


import org.springframework.jdbc.core.namedparam.AbstractSqlParameterSource;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * {@link org.springframework.jdbc.core.namedparam.SqlParameterSource} implementation that holds a given Map of parameters.
 * Supports {@Link LocalDateTime} and {@Link LocalDate} of JSR-310.
 */
public class MapParameter extends AbstractSqlParameterSource {
    /**
     * the Map holding parameters
     */
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

        if (value instanceof LocalDate) {
            return Date.valueOf((LocalDate) value);
        } else if (value instanceof LocalDateTime) {
            return Timestamp.valueOf((LocalDateTime) value);
        }

        return value;
    }
}
