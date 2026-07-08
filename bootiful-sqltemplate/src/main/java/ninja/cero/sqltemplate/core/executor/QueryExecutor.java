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

    /**
     * Executes the query and returns the result as a {@link Stream}.
     * <p>Unlike {@link #forStream(Class, Function)}, the returned {@code Stream} is <em>not</em>
     * closed automatically. The caller must close it (for example, with a try-with-resources block),
     * otherwise the underlying JDBC resources (Connection, etc.) will leak.
     */
    <T> Stream<T> forStream(Class<T> clazz);

    /**
     * Executes the query and returns the result as a {@link Stream} of {@code Map<String, Object>}.
     * <p>Unlike {@link #forStream(Function)}, the returned {@code Stream} is <em>not</em>
     * closed automatically. The caller must close it (for example, with a try-with-resources block),
     * otherwise the underlying JDBC resources (Connection, etc.) will leak.
     */
    Stream<Map<String, Object>> forStream();

    int update();
}
