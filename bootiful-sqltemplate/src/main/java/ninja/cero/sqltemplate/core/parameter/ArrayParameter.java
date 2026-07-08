package ninja.cero.sqltemplate.core.parameter;

import ninja.cero.sqltemplate.core.util.JdbcValueUtils;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.StatementCreatorUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZoneId;

/**
 * {@inheritDoc}
 * Supports {@link java.time.LocalDateTime} and {@link java.time.LocalDate} of JSR-310
 */
public class ArrayParameter extends ArgumentPreparedStatementSetter {
    /** ZoneId for OffsetDateTime and ZonedDateTime */
    protected ZoneId zoneId;

    /**
     * Create a new ArgPreparedStatementSetter for the given arguments.
     *
     * @param args   the arguments to set
     * @param zoneId zoneId
     */
    public ArrayParameter(Object[] args, ZoneId zoneId) {
        super(args);
        this.zoneId = zoneId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetValue(PreparedStatement ps, int parameterPosition, Object argValue) throws SQLException {
        // A SqlParameterValue carries its own type; otherwise bind with the type of the value.
        if (argValue instanceof SqlParameterValue) {
            super.doSetValue(ps, parameterPosition, argValue);
            return;
        }
        StatementCreatorUtils.setParameterValue(ps, parameterPosition,
                JdbcValueUtils.getSqlType(argValue),
                JdbcValueUtils.convertIfNecessary(argValue, zoneId));
    }
}
