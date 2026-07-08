package ninja.cero.sqltemplate.core.util;

import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.*;
import java.time.*;

public class Jsr310JdbcUtils {
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
        }

        return value;
    }

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
        }

        return JdbcUtils.getResultSetValue(rs, index, requiredType);
    }

    public static int getSqlType(Class<?> type) {
        int sqlType = StatementCreatorUtils.javaTypeToSqlParameterType(type);
        if (sqlType != SqlTypeValue.TYPE_UNKNOWN) {
            return sqlType;
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
        if (OffsetDateTime.class.isAssignableFrom(type)) {
            return Types.TIMESTAMP;
        }
        if (OffsetTime.class.isAssignableFrom(type)) {
            return Types.TIME;
        }

        return SqlTypeValue.TYPE_UNKNOWN;

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

