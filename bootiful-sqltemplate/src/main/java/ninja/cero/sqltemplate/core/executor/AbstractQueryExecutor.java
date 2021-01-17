package ninja.cero.sqltemplate.core.executor;

import org.springframework.dao.support.DataAccessUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class AbstractQueryExecutor implements QueryExecutor {
    @Override
    public <T> T forObject(Class<T> clazz) {
        List<T> list = forList(clazz);
        return DataAccessUtils.singleResult(list);
    }

    @Override
    public Map<String, Object> forMap() {
        List<Map<String, Object>> list = forList();
        return DataAccessUtils.singleResult(list);
    }

    @Override
    public <T> Optional<T> forOptional(Class<T> clazz) {
        return forStream(clazz, Stream::findFirst);
    }

    @Override
    public Optional<Map<String, Object>> forOptional() {
        return forStream(Stream::findFirst);
    }
}
