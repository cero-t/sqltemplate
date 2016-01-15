package ninja.cero.sqltemplate.core.mapper;

import ninja.cero.sqltemplate.core.util.Jsr310JdbcUtils;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;

public class SingleClassMapper<T> extends SingleColumnRowMapper<T> {
    /** ZoneId for OffsetDateTime and ZonedDateTime */
    protected ZoneId zoneId;

    public SingleClassMapper(Class<T> requiredType, ZoneId zoneId) {
        super(requiredType);
        this.zoneId = zoneId;
    }

    @Override
    protected Object getColumnValue(ResultSet rs, int index, Class<?> requiredType) throws SQLException {
        if (requiredType != null) {
            return Jsr310JdbcUtils.getResultSetValue(rs, index, requiredType, zoneId);
        }
        return JdbcUtils.getResultSetValue(rs, index);
    }
}
