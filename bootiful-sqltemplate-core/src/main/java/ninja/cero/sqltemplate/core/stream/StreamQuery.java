package ninja.cero.sqltemplate.core.stream;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A query which produces a row stream and passes it to a function.
 *
 * <p>Usage:</p>
 *
 * <pre>
 * int totalSalary = sqlTemplate.forStream("/path/to/query.sql", Emp.class)
 *     .in(empStream -&gt; empStream.mapToInt(Emp::getSalary).sum());
 * </pre>
 *
 * @param <T> the row type
 */
@FunctionalInterface
public interface StreamQuery<T> {

    /**
     * Executes the query,
     * passing the rows to {@code handleStream} as a stream.
     *
     * @param handleStream the function which is applied to the stream
     * @param <U> the result type
     * @return the result of {@code handleStream}
     * @throws org.springframework.dao.DataAccessException if there is any problem
     */
    public abstract <U> U in(Function<? super Stream<T>, U> handleStream);

}
