package ninja.cero.sqltemplate.core.mapper;

import ninja.cero.sqltemplate.core.util.BeanFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    /**
     * Create a new BeanMapper.
     * @param mappedClass the class we are mapping to
     * @param <T>         the class we are mapping to
     * @return a new BeanMapper
     */
    public static <T> RowMapper<T> of(Class<T> mappedClass) {
        if (BeanUtils.isSimpleValueType(mappedClass)) {
            return new SingleColumnRowMapper<T>(mappedClass);
        }
        return new BeanMapper<>(mappedClass);
    }

    /**
     * Create a new BeanMapper.
     * @param mappedClass the class we are mapping to
     */
    protected BeanMapper(Class<T> mappedClass) {
        this.mappedClass = mappedClass;

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

            Object value = getColumnValue(rs, index, field);
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
     * @param field the field to be set the value
     * @return column value
     * @throws SQLException in case of extraction failure
     */
    protected Object getColumnValue(ResultSet rs, int index, Field field) throws SQLException {
        Class<?> requiredType = field.getType();

        if (LocalDateTime.class.equals(requiredType)) {
            return getAsLocalDateTime(rs, index);
        } else if (LocalDate.class.equals(requiredType)) {
            return getAsLocalDate(rs, index);
        } else if (LocalTime.class.equals(requiredType)) {
            return getAsLocalTime(rs, index);
        } else if (OffsetDateTime.class.equals(requiredType)) {
            return getAsOffsetDateTime(rs, index);
        } else if (OffsetTime.class.equals(requiredType)) {
            return getAsOffsetTime(rs, index);
        } else if (ZonedDateTime.class.equals(requiredType)) {
            return getAsZonedDateTime(rs, index);
        }

        return JdbcUtils.getResultSetValue(rs, index, field.getType());
    }

    /**
     * Get the column value as LocalDateTime.
     * @param rs    ResultSet
     * @param index column index
     * @return column value
     * @throws SQLException in case of extraction failure
     */
    protected LocalDateTime getAsLocalDateTime(ResultSet rs, int index) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(index);
        if (timestamp != null) {
            return timestamp.toLocalDateTime();
        }
        return null;
    }

    /**
     * Get the column value as LocalDate.
     * @param rs    ResultSet
     * @param index column index
     * @return column value
     * @throws SQLException in case of extraction failure
     */
    protected LocalDate getAsLocalDate(ResultSet rs, int index) throws SQLException {
        Date date = rs.getDate(index);
        if (date != null) {
            return date.toLocalDate();
        }
        return null;
    }

    /**
     * Get the column value as LocalTime.
     * @param rs    ResultSet
     * @param index column index
     * @return column value
     * @throws SQLException in case of extraction failure
     */
    protected LocalTime getAsLocalTime(ResultSet rs, int index) throws SQLException {
        Time time = rs.getTime(index);
        if (time != null) {
            return time.toLocalTime();
        }
        return null;
    }

    /**
     * Get the column value as ZonedDateTime.
     * @param rs    ResultSet
     * @param index column index
     * @return column value
     * @throws SQLException in case of extraction failure
     */
    protected ZonedDateTime getAsZonedDateTime(ResultSet rs, int index) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(index);
        if (timestamp != null) {
            return timestamp.toLocalDateTime().atZone(ZoneId.systemDefault());
        }
        return null;
    }

    /**
     * Get the column value as OffsetDateTime.
     * @param rs    ResultSet
     * @param index column index
     * @return column value
     * @throws SQLException in case of extraction failure
     */
    protected OffsetDateTime getAsOffsetDateTime(ResultSet rs, int index) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(index);
        if (timestamp != null) {
            return timestamp.toLocalDateTime().atZone(ZoneId.systemDefault()).toOffsetDateTime();
        }
        return null;
    }


    /**
     * Get the column value as OffsetTime.
     * @param rs    ResultSet
     * @param index column index
     * @return column value
     * @throws SQLException in case of extraction failure
     */
    protected OffsetTime getAsOffsetTime(ResultSet rs, int index) throws SQLException {
        Time time = rs.getTime(index);
        if (time != null) {
            return time.toLocalTime().atOffset(ZoneId.systemDefault().getRules().getOffset(Instant.now()));
        }
        return null;

    }
}
