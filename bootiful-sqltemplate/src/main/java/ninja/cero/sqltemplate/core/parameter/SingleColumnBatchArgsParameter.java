package ninja.cero.sqltemplate.core.parameter;

import ninja.cero.sqltemplate.core.util.Jsr310JdbcUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZoneId;

public class SingleColumnBatchArgsParameter implements BatchPreparedStatementSetter {
    protected Object[] batchParams;

    /** ZoneId for OffsetDateTime and ZonedDateTime */
    protected ZoneId zoneId;

    public SingleColumnBatchArgsParameter(Object[] batchParams, ZoneId zoneId) {
        this.batchParams = batchParams;
        this.zoneId = zoneId;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        Object param = batchParams[i];

        StatementCreatorUtils.setParameterValue(ps, 1, SqlTypeValue.TYPE_UNKNOWN,
                Jsr310JdbcUtils.convertIfNecessary(param, zoneId));
    }

    @Override
    public int getBatchSize() {
        return batchParams.length;
    }
}
