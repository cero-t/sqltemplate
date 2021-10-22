package ninja.cero.sqltemplate.core.mapper;

import ninja.cero.sqltemplate.core.util.TypeUtils;
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
        if (TypeUtils.isSimpleValueType(mappedClass)) {
            return new SingleColumnMapper<>(mappedClass, zoneId);
        }

        try {
            Class<?> recordClass = Class.forName("java.lang.Record");
            if (recordClass.isAssignableFrom(mappedClass)) {
                return new RecordMapper<>(mappedClass, zoneId);
            }
        } catch (ClassNotFoundException e) {
            // ignore
        }

        return new BeanMapper<>(mappedClass, zoneId);
    }
}
