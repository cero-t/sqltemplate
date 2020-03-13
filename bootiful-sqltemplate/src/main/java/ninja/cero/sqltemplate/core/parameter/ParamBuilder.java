package ninja.cero.sqltemplate.core.parameter;

import java.time.ZoneId;
import java.util.Map;

public class ParamBuilder {
    protected ZoneId zoneId;

    public ParamBuilder() {
        this.zoneId = ZoneId.systemDefault();
    }

    public ParamBuilder(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public ArrayParameter byArgs(Object[] args) {
        return new ArrayParameter(args, zoneId);
    }

    public MapParameter byMap(Map<String, Object> values) {
        return new MapParameter(values, zoneId);
    }

    public BeanParameter byBean(Object entity) {
        return new BeanParameter(entity, zoneId);
    }

    public BatchArgsParameter byBatchArgs(Object[][] batchArgs) {
        return new BatchArgsParameter(batchArgs, zoneId);
    }

    public SingleParamBatchArgsParameter byBatchArgs(Object[] batchArgs) {
        return new SingleParamBatchArgsParameter(batchArgs, zoneId);
    }
}
