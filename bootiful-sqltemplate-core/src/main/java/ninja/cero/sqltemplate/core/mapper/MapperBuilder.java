package ninja.cero.sqltemplate.core.mapper;

import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;

import java.time.ZoneId;

public class MapperBuilder {
    protected ZoneId zoneId;

    public MapperBuilder() {
        this.zoneId = ZoneId.systemDefault();
    }

    public MapperBuilder(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public <T> RowMapper<T> mapper(Class<T> mappedClass) {
        if (BeanUtils.isSimpleValueType(mappedClass)) {
            return new SingleClassMapper<>(mappedClass, zoneId);
        }
        return new BeanMapper<>(mappedClass, zoneId);
    }
}
