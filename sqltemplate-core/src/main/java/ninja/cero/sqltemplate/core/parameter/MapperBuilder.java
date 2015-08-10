package ninja.cero.sqltemplate.core.parameter;

import ninja.cero.sqltemplate.core.mapper.BeanMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import java.time.ZoneId;
import java.util.Map;

public class MapperBuilder {
    protected ZoneId zoneId;

    public MapperBuilder() {
        this.zoneId = ZoneId.systemDefault();
    }

    public MapperBuilder(ZoneId zoneId) {
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

    public <T> RowMapper<T> mapper(Class<T> mappedClass) {
        if (BeanUtils.isSimpleValueType(mappedClass)) {
            return new SingleColumnRowMapper<>(mappedClass);
        }
        return new BeanMapper<>(mappedClass, zoneId);
    }
}
