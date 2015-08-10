package ninja.cero.sqltemplate.core.parameter;

import ninja.cero.sqltemplate.core.util.BeanFields;
import ninja.cero.sqltemplate.core.util.Jsr310JdbcUtils;
import org.springframework.jdbc.core.namedparam.AbstractSqlParameterSource;

import java.lang.reflect.Field;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link org.springframework.jdbc.core.namedparam.SqlParameterSource} implementation that obtains parameter values
 * from public fields of a given value object.
 * Supports {@link java.time.LocalDateTime} and {@link java.time.LocalDate} of JSR-310
 */
public class BeanParameter extends AbstractSqlParameterSource {
    /** the value object for parameters */
    protected Object entity;

    /** Map of the fields we provide mapping for */
    protected Map<String, Field> mappedFields = new HashMap<>();

    /** ZoneId for OffsetDateTime and ZonedDateTime */
    protected ZoneId zoneId;

    /**
     * Create a new BeanParameter for the given value object.
     * @param entity the value object for parameters
     * @param zoneId zoneId
     */
    public BeanParameter(Object entity, ZoneId zoneId) {
        init(entity);
        this.zoneId = zoneId;
    }

    protected void init(Object entity) {
        this.entity = entity;
        for (Field field : BeanFields.get(entity.getClass())) {
            mappedFields.put(field.getName(), field);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasValue(String paramName) {
        return mappedFields.containsKey(paramName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(String paramName) {
        Field field = mappedFields.get(paramName);
        if (field == null) {
            return null;
        }

        Object value;
        try {
            value = field.get(entity);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
        if (value == null) {
            return null;
        }

        return Jsr310JdbcUtils.convertIfNecessary(value, zoneId);
    }
}
