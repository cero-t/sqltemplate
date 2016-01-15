package ninja.cero.sqltemplate.core.mapper;

import ninja.cero.sqltemplate.core.util.BeanFields;
import ninja.cero.sqltemplate.core.util.Jsr310JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * Yet another {@link org.springframework.jdbc.core.BeanPropertyRowMapper} implementation for public fields.
 * Supports {@link java.time.LocalDateTime} and {@link java.time.LocalDate} of JSR-310
 * @param <T> The class
 */
public class BeanMapper<T> implements RowMapper<T> {
    /** Logger available to subclasses */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** The class we are mapping to */
    protected Class<T> mappedClass;

    /** Map of the fields we provide mapping for */
    protected Map<String, Field> mappedFields = new HashMap<>();

    /** ZoneId for OffsetDateTime and ZonedDateTime */
    protected ZoneId zoneId;

    /**
     * Create a new BeanMapper.
     * @param mappedClass the class we are mapping to
     * @param zoneId      the zoneId of JSR-310 DateTime
     */
    public BeanMapper(Class<T> mappedClass, ZoneId zoneId) {
        this.mappedClass = mappedClass;
        this.zoneId = zoneId;

        Field[] fields = BeanFields.get(mappedClass);
        for (Field field : fields) {
            this.mappedFields.put(field.getName()
                    .toLowerCase(), field);
            String underscoredName = underscoreName(field.getName());
            if (!field.getName()
                    .toLowerCase()
                    .equals(underscoredName)) {
                this.mappedFields.put(underscoredName, field);
            }
        }
    }

    /**
     * {@see org.springframework.jdbc.core.BeanPropertyRowMapper#un}
     * Convert a name in camelCase to an underscored name in lower case.
     * Any upper case letters are converted to lower case with a preceding underscore.
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
        Assert.state(this.mappedClass != null, "Mapped class was not specified");
        T mappedObject = BeanUtils.instantiate(this.mappedClass);

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int index = 1; index <= columnCount; index++) {
            String column = JdbcUtils.lookupColumnName(metaData, index);
            Field field = this.mappedFields.get(column.replaceAll(" ", "")
                    .toLowerCase());
            if (field == null) {
                continue;
            }

            Object value = getColumnValue(rs, index, field.getType());
            if (logger.isDebugEnabled() && rowNumber == 0) {
                logger.debug("Mapping column '" + column + "' to property '" + field.getName() + "' of type "
                        + field.getType());
            }

            try {
                field.set(mappedObject, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return mappedObject;
    }

    /**
     * Get the column value.
     * @param rs    ResultSet
     * @param index column index
     * @param requiredType the required value type
     * @return column value
     * @throws SQLException in case of extraction failure
     */
    protected Object getColumnValue(ResultSet rs, int index, Class<?> requiredType) throws SQLException {
        return Jsr310JdbcUtils.getResultSetValue(rs, index, requiredType, zoneId);
    }
}
