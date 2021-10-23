package ninja.cero.sqltemplate.core.mapper;

import ninja.cero.sqltemplate.core.util.Jsr310JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link org.springframework.jdbc.core.RowMapper} implementation for record class.
 * Supports {@link java.time.LocalDateTime} and {@link java.time.LocalDate} of JSR-310
 *
 * @param <T> The record class
 */
public class RecordMapper<T> implements RowMapper<T> {
    /** Logger available to subclasses */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** The class we are mapping to */
    protected Class<T> mappedClass;

    /** The constructor of mapping class */
    protected Constructor<T> constructor;

    /** The constructor parameters of mapping class */
    protected Class<?>[] paramTypes;

    /** Map of indexes of constructor parameters */
    protected Map<String, Integer> indexes = new HashMap<>();

    /** ZoneId for OffsetDateTime and ZonedDateTime */
    protected ZoneId zoneId;

    /** Method of Record#getRecordComponents */
    private final Method RECORD_GET_RECORD_COMPONENTS;

    /** Method of RecordComponent#getName */
    private final Method RECORD_COMPONENT_GET_NAME;

    /** Method of RecordComponent#getType */
    private final Method RECORD_COMPONENT_GET_TYPE;

    /**
     * Create a new BeanMapper.
     *
     * @param mappedClass the class we are mapping to
     * @param zoneId      the zoneId of JSR-310 DateTime
     */
    public RecordMapper(Class<T> mappedClass, ZoneId zoneId) {
        try {
            RECORD_GET_RECORD_COMPONENTS = Class.class.getMethod("getRecordComponents");
            Class<?> c = Class.forName("java.lang.reflect.RecordComponent");
            RECORD_COMPONENT_GET_NAME = c.getMethod("getName");
            RECORD_COMPONENT_GET_TYPE = c.getMethod("getType");

            this.mappedClass = mappedClass;
            this.zoneId = zoneId;

            Object[] components = (Object[]) RECORD_GET_RECORD_COMPONENTS.invoke(mappedClass);
            paramTypes = new Class<?>[components.length];
            for (int i = 0; i < components.length; i++) {
                paramTypes[i] = (Class<?>) RECORD_COMPONENT_GET_TYPE.invoke(components[i]);
            }

            constructor = mappedClass.getConstructor(paramTypes);
            for (int i = 0; i < components.length; i++) {
                String name = (String) RECORD_COMPONENT_GET_NAME.invoke(components[i]);

                indexes.put(name.toLowerCase(), i);
                String underscoredName = underscoreName(name);
                if (!name.toLowerCase()
                        .equals(underscoredName)) {
                    indexes.put(underscoredName, i);
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("java.lang.Record couldn't be supported", e);
        }
    }

    /**
     * {@see org.springframework.jdbc.core.BeanPropertyRowMapper#un}
     * Convert a name in camelCase to an underscored name in lower case.
     * Any upper case letters are converted to lower case with a preceding underscore.
     *
     * @param name the string containing original name
     * @return the converted name
     */
    private String underscoreName(String name) {
        if (!StringUtils.hasLength(name)) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        result.append(name.substring(0, 1)
                .toLowerCase());
        for (int i = 1; i < name.length(); i++) {
            String s = name.substring(i, i + 1);
            String slc = s.toLowerCase();
            if (!s.equals(slc)) {
                result.append("_")
                        .append(slc);
            } else {
                result.append(s);
            }
        }
        return result.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
        Object[] params = new Object[constructor.getParameterCount()];

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int index = 1; index <= columnCount; index++) {
            String column = JdbcUtils.lookupColumnName(metaData, index);

            String name = column.replace(" ", "").toLowerCase();
            if (indexes.containsKey(name)) {
                int i = indexes.get(name);
                params[i] = getColumnValue(rs, index, paramTypes[i]);

                if (logger.isDebugEnabled() && rowNumber == 0) {
                    logger.debug("Mapping column '" + column + "' to constructor parameter at '" + i + "' of type " + paramTypes[i]);
                }
            }
        }

        try {
            return constructor.newInstance(params);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Couldn't create record instance.", e);
        }
    }

    /**
     * Get the column value.
     *
     * @param rs           ResultSet
     * @param index        column index
     * @param requiredType the required value type
     * @return column value
     * @throws SQLException in case of extraction failure
     */
    protected Object getColumnValue(ResultSet rs, int index, Class<?> requiredType) throws SQLException {
        return Jsr310JdbcUtils.getResultSetValue(rs, index, requiredType, zoneId);
    }
}
