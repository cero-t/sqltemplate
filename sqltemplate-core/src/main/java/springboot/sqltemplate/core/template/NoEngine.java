package springboot.sqltemplate.core.template;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class NoEngine implements TemplateEngine {
    /**
     * template cache
     */
    protected final ConcurrentMap<String, String> templateCache = new ConcurrentHashMap<>();

    @Override
    public String get(String fileName, Object[] args) throws IOException {
        return templateCache.computeIfAbsent(fileName, x -> {
            try {
                return Files.readAllLines(Paths.get(getClass().getResource("/" + x).getFile())).stream()
                        .collect(Collectors.joining("\n"));
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
