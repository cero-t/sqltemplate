package ninja.cero.sqltemplate.core.template;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NoEngine implements TemplateEngine {
    /**
     * template cache
     */
    protected final ConcurrentMap<String, String> templateCache = new ConcurrentHashMap<>();

    @Override
    public String get(String fileName, Object[] args) throws IOException {
        return templateCache.computeIfAbsent(fileName, x -> {
            URL resource = getClass().getResource("/" + x);
            if (resource == null) {
                throw new IllegalArgumentException("Tempalte file does not exist - " + x);
            }

            Path path = Paths.get(resource.getFile());
            try (Stream<String> stream = Files.lines(path)) {
                return stream.collect(Collectors.joining("\n"));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    @Override
    public String get(String fileName, Object object) throws IOException {
        return get(fileName, new Object[]{object});
    }
}
