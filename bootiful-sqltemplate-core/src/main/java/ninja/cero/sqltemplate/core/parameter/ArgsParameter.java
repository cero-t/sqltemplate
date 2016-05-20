package ninja.cero.sqltemplate.core.parameter;

import ninja.cero.sqltemplate.core.util.Jsr310JdbcUtils;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZoneId;

/**
 * {@inheritDoc}
 * Supports {@link java.time.LocalDateTime} and {@link java.time.LocalDate} of JSR-310
 */
public class ArgsParameter extends ArgumentPreparedStatementSetter {
    /** ZoneId for OffsetDateTime and ZonedDateTime */
    protected ZoneId zoneId;

    /**
     * Create a new ArgPreparedStatementSetter for the given arguments.
     *
     * @param args   the arguments to set
     * @param zoneId zoneId
     */
    public ArgsParameter(Object[] args, ZoneId zoneId) {
        super(args);
        this.zoneId = zoneId;
    }

    /**
     * {@inheritDoc}
     */
    protected void doSetValue(PreparedStatement ps, int parameterPosition, Object argValue) throws SQLException {
        super.doSetValue(ps, parameterPosition, Jsr310JdbcUtils.convertIfNecessary(argValue, zoneId));
    }
}
