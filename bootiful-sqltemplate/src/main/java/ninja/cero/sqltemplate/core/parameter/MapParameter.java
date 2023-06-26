package ninja.cero.sqltemplate.core.parameter;

import ninja.cero.sqltemplate.core.util.Jsr310JdbcUtils;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.namedparam.AbstractSqlParameterSource;

import java.time.ZoneId;
import java.util.Map;

/**
 * {@link org.springframework.jdbc.core.namedparam.SqlParameterSource} implementation that holds a given Map of parameters.
 * Supports {@link java.time.LocalDateTime} and {@link java.time.LocalDate} of JSR-310.
 */
public class MapParameter extends AbstractSqlParameterSource {
    /** the Map holding parameters */
    protected Map<String, Object> values;

    /** ZoneId for OffsetDateTime and ZonedDateTime */
    protected ZoneId zoneId;

    /**
     * Create a new MapParameter.
     * @param values the Map holding parameters
     * @param zoneId zoneId
     */
    public MapParameter(Map<String, Object> values, ZoneId zoneId) {
        this.values = values;
        this.zoneId = zoneId;
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

        return Jsr310JdbcUtils.convertIfNecessary(value, zoneId);
    }

    @Override
    public int getSqlType(String paramName) {
        int sqlType = super.getSqlType(paramName);
        if (sqlType != TYPE_UNKNOWN) {
            return sqlType;
        }
        Object value = values.get(paramName);
        if (value == null) {
            return TYPE_UNKNOWN;
        }
        if (value.getClass().isEnum()) {
            return StatementCreatorUtils.javaTypeToSqlParameterType(String.class);
        }
        return Jsr310JdbcUtils.getSqlType(value.getClass());
    }
}
