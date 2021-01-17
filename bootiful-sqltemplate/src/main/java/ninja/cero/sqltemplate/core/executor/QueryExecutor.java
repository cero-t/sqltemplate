package ninja.cero.sqltemplate.core.executor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface QueryExecutor {
    <T> T forObject(Class<T> clazz);

    Map<String, Object> forMap();

    <T> Optional<T> forOptional(Class<T> clazz);

    Optional<Map<String, Object>> forOptional();

    <T> List<T> forList(Class<T> clazz);

    List<Map<String, Object>> forList();

    <T, U> U forStream(Class<T> clazz, Function<? super Stream<T>, U> handler);

    <U> U forStream(Function<? super Stream<Map<String, Object>>, U> handler);

    int update();
}
