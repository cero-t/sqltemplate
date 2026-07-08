package ninja.cero.sqltemplate.core.util;

import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.*;
import java.time.*;

/**
 * Central JDBC value / type conversion shared by every parameter source and row mapper:
 * <ul>
 *   <li>{@link #convertIfNecessary} converts a Java value to what JDBC expects (write side),</li>
 *   <li>{@link #getResultSetValue} extracts a column into the required Java type (read side),</li>
 *   <li>{@link #getSqlType} provides the SQL type hint for a parameter.</li>
 * </ul>
 * Handles JSR-310 date/time types and {@code enum} values (mapped by {@link Enum#name()} in both
 * directions); anything else falls back to Spring's {@link JdbcUtils} / {@link StatementCreatorUtils}.
 *
 * <p>Enum support originated from PR #41 / #43 (Enum.name() mapping).
 */
public class JdbcValueUtils {
    /**
     * Convert a parameter value to the representation JDBC expects.
     * JSR-310 date/time values become {@code java.sql} equivalents and enums become their
     * {@link Enum#name()}; everything else is passed through unchanged.
     */
    public static Object convertIfNecessary(Object value, ZoneId zoneId) {
        if (value instanceof LocalDateTime localDateTime) {
            return Timestamp.valueOf(localDateTime);
        } else if (value instanceof LocalDate localDate) {
            return Date.valueOf(localDate);
        } else if (value instanceof LocalTime localTime) {
            return Time.valueOf(localTime);
        } else if (value instanceof OffsetDateTime offsetDateTime) {
            OffsetDateTime adjusted = offsetDateTime.withOffsetSameInstant(zoneId.getRules().getOffset(Instant.now()));
            return Timestamp.valueOf(adjusted.toLocalDateTime());
        } else if (value instanceof OffsetTime offsetTime) {
            OffsetTime adjusted = offsetTime.withOffsetSameInstant(zoneId.getRules().getOffset(Instant.now()));
            return Time.valueOf(adjusted.toLocalTime());
        } else if (value instanceof ZonedDateTime zonedDateTime) {
            ZonedDateTime adjusted = zonedDateTime.withZoneSameInstant(zoneId);
            return Timestamp.valueOf(adjusted.toLocalDateTime());
        } else if (value instanceof Instant instant) {
            // An Instant is an absolute point on the timeline, so it is converted straight through
            // the epoch (Timestamp.from / toInstant). zoneId is intentionally NOT applied here.
            return Timestamp.from(instant);
        } else if (value instanceof Enum<?> enumValue) {
            // Bind enums by name() so read and write stay symmetric.
            return enumValue.name();
        }

        return value;
    }

    /**
     * Extract a column value as {@code requiredType}. JSR-310 date/time types and enums get
     * dedicated handling; everything else defers to Spring's {@link JdbcUtils}.
     */
    public static Object getResultSetValue(ResultSet rs, int index, Class<?> requiredType, ZoneId zoneId) throws SQLException {
        if (LocalDateTime.class.equals(requiredType)) {
            return getAsLocalDateTime(rs, index);
        } else if (LocalDate.class.equals(requiredType)) {
            return getAsLocalDate(rs, index);
        } else if (LocalTime.class.equals(requiredType)) {
            return getAsLocalTime(rs, index);
        } else if (OffsetDateTime.class.equals(requiredType)) {
            return getAsOffsetDateTime(rs, index, zoneId);
        } else if (OffsetTime.class.equals(requiredType)) {
            return getAsOffsetTime(rs, index, zoneId);
        } else if (ZonedDateTime.class.equals(requiredType)) {
            return getAsZonedDateTime(rs, index, zoneId);
        } else if (Instant.class.equals(requiredType)) {
            return getAsInstant(rs, index);
        } else if (requiredType != null && requiredType.isEnum()) {
            return getAsEnum(rs, index, requiredType);
        }

        return JdbcUtils.getResultSetValue(rs, index, requiredType);
    }

    /**
     * Determine the SQL type for a parameter of the given Java type.
     * Enums are bound as {@link Types#VARCHAR}; JSR-310 types map to their SQL equivalents.
     */
    public static int getSqlType(Class<?> type) {
        // Resolve JSR-310 / enum types with our own mapping BEFORE Spring's: convertIfNecessary turns
        // these into naive java.sql values, so the SQL type must be naive too. Spring's
        // javaTypeToSqlParameterType maps OffsetDateTime/OffsetTime to *_WITH_TIMEZONE, which strict
        // drivers (e.g. PostgreSQL) reject against the naive Timestamp/Time we actually bind.
        if (Enum.class.isAssignableFrom(type)) {
            return Types.VARCHAR;
        }
        if (LocalDate.class.isAssignableFrom(type)) {
            return Types.DATE;
        }
        if (LocalDateTime.class.isAssignableFrom(type)) {
            return Types.TIMESTAMP;
        }
        if (LocalTime.class.isAssignableFrom(type)) {
            return Types.TIME;
        }
        if (ZonedDateTime.class.isAssignableFrom(type)) {
            return Types.TIMESTAMP;
        }
        if (Instant.class.isAssignableFrom(type)) {
            return Types.TIMESTAMP;
        }
        if (OffsetDateTime.class.isAssignableFrom(type)) {
            return Types.TIMESTAMP;
        }
        if (OffsetTime.class.isAssignableFrom(type)) {
            return Types.TIME;
        }

        return StatementCreatorUtils.javaTypeToSqlParameterType(type);
    }

    /**
     * Determine the SQL type for a parameter <em>value</em> (delegates to {@link #getSqlType(Class)}).
     * A {@code null} value has no type and yields {@link SqlTypeValue#TYPE_UNKNOWN}.
     */
    public static int getSqlType(Object value) {
        return (value == null) ? SqlTypeValue.TYPE_UNKNOWN : getSqlType(value.getClass());
    }

    /**
     * Get a string column (CHAR / VARCHAR family) as an enum constant, matched by {@link Enum#name()}.
     *
     * @throws IllegalArgumentException if the column is not a string type, or no constant matches
     */
    private static Object getAsEnum(ResultSet rs, int index, Class<?> requiredType) throws SQLException {
        switch (rs.getMetaData().getColumnType(index)) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.NCHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
                return getEnumByName(rs.getString(index), requiredType);
            default:
                throw new IllegalArgumentException("Cannot map column " + index + " of SQL type "
                        + rs.getMetaData().getColumnTypeName(index) + " to enum " + requiredType.getCanonicalName()
                        + "; only string columns are supported (matched by Enum.name()).");
        }
    }

    private static Object getEnumByName(String value, Class<?> requiredType) {
        if (value == null) {
            return null;
        }
        for (Object enumConstant : requiredType.getEnumConstants()) {
            if (((Enum<?>) enumConstant).name().equals(value)) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("No enum constant " + requiredType.getCanonicalName() + "." + value);
    }

    /**
     * Get the column value as LocalDateTime.
     * @param rs    ResultSet
     * @param index column index
     * @return column value
     * @throws SQLException in case of extraction failure
     */
    protected static LocalDateTime getAsLocalDateTime(ResultSet rs, int index) throws SQLException {
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
    protected static LocalDate getAsLocalDate(ResultSet rs, int index) throws SQLException {
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
    protected static LocalTime getAsLocalTime(ResultSet rs, int index) throws SQLException {
        Time time = rs.getTime(index);
        if (time != null) {
            return time.toLocalTime();
        }
        return null;
    }

    /**
     * Get the column value as ZonedDateTime.
     * @param rs     ResultSet
     * @param index  column index
     * @param zoneId zoneId
     * @return column value
     * @throws SQLException in case of extraction failure
     */
    protected static ZonedDateTime getAsZonedDateTime(ResultSet rs, int index, ZoneId zoneId) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(index);
        if (timestamp != null) {
            return timestamp.toLocalDateTime().atZone(zoneId);
        }
        return null;
    }

    /**
     * Get the column value as Instant.
     * @param rs    ResultSet
     * @param index column index
     * @return column value
     * @throws SQLException in case of extraction failure
     */
    protected static Instant getAsInstant(ResultSet rs, int index) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(index);
        if (timestamp != null) {
            return timestamp.toInstant();
        }
        return null;
    }

    /**
     * Get the column value as OffsetDateTime.
     * @param rs     ResultSet
     * @param index  column index
     * @param zoneId zoneId
     * @return column value
     * @throws SQLException in case of extraction failure
     */
    protected static OffsetDateTime getAsOffsetDateTime(ResultSet rs, int index, ZoneId zoneId) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(index);
        if (timestamp != null) {
            return timestamp.toLocalDateTime().atZone(zoneId).toOffsetDateTime();
        }
        return null;
    }

    /**
     * Get the column value as OffsetTime.
     * @param rs     ResultSet
     * @param index  column index
     * @param zoneId zoneId
     * @return column value
     * @throws SQLException in case of extraction failure
     */
    protected static OffsetTime getAsOffsetTime(ResultSet rs, int index, ZoneId zoneId) throws SQLException {
        Time time = rs.getTime(index);
        if (time != null) {
            return time.toLocalTime().atOffset(zoneId.getRules().getOffset(Instant.now()));
        }
        return null;
    }
}
