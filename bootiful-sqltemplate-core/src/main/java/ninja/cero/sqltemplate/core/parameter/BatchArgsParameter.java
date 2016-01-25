package ninja.cero.sqltemplate.core.parameter;

import ninja.cero.sqltemplate.core.util.Jsr310JdbcUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZoneId;

public class BatchArgsParameter implements BatchPreparedStatementSetter {
    protected Object[][] batchParams;

    /** ZoneId for OffsetDateTime and ZonedDateTime */
    protected ZoneId zoneId;

    public BatchArgsParameter(Object[][] batchParams, ZoneId zoneId) {
        this.batchParams = batchParams;
        this.zoneId = zoneId;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        Object[] params = batchParams[i];

        int colIndex = 0;
        for (Object param : params) {
            colIndex++;
            StatementCreatorUtils.setParameterValue(ps, colIndex, SqlTypeValue.TYPE_UNKNOWN,
                    Jsr310JdbcUtils.convertIfNecessary(param, zoneId));
        }
    }

    @Override
    public int getBatchSize() {
        return batchParams.length;
    }
}
