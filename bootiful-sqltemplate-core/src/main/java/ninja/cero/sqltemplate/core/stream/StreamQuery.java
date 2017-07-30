package ninja.cero.sqltemplate.core.stream;

import java.util.function.Function;
import java.util.stream.Stream;

@FunctionalInterface
public interface StreamQuery<T> {

    public abstract <U> U in(Function<? super Stream<T>, U> handleStream);

}
