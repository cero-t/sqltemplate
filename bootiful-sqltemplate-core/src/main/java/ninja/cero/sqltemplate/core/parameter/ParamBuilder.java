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

    public ArgsParameter byArgs(Object arg) {
        return new ArgsParameter(new Object[]{arg}, zoneId);
    }

    public ArgsParameter byArgs(Object[] args) {
        return new ArgsParameter(args, zoneId);
    }

    public MapParameter byMap(Map<String, Object> values) {
        return new MapParameter(values, zoneId);
    }

    public BeanParameter byBean(Object entity) {
        return new BeanParameter(entity, zoneId);
    }
}
