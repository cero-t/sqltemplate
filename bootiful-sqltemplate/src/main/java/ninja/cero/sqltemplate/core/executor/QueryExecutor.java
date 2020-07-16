package ninja.cero.sqltemplate.core.executor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public interface QueryExecutor {
    <T> T forObject(Class<T> clazz);

    <T> List<T> forList(Class<T> clazz);

    <T, U> U forStream(Class<T> clazz, Function<? super Stream<T>, U> handler);

    Map<String, Object> forMap();

    List<Map<String, Object>> forList();

    <U> U forStream(Function<? super Stream<Map<String, Object>>, U> handler);

    int update();
}
