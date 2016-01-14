package ninja.cero.sqltemplate.core.mapper;

import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;

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
        // TODO: Need custom single column row mapper to map to JSR-310
        // TODO: Need custom row mapper for Map<String, Object> to map to JSR-310

        if (BeanUtils.isSimpleValueType(mappedClass)) {
            return new SingleColumnRowMapper<>(mappedClass);
        }
        return new BeanMapper<>(mappedClass, zoneId);
    }
}
