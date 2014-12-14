package springboot.sqltemplate.core.template;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class NoEngine implements TemplateEngine {
    /**
     * template cache
     */
    protected ConcurrentMap<String, String> templateCache = new ConcurrentHashMap<>();

    @Override
    public String get(String fileName, Object[] args) throws IOException {
        String text = templateCache.get(fileName);
        if (text != null) {
            return text;
        }

        text = Files.readAllLines(Paths.get(getClass().getResource("/" + fileName).getFile())).stream()
                .collect(Collectors.joining("\n"));
        templateCache.putIfAbsent(fileName, text);

        return text;
    }

    @Override
    public String get(String fileName, Object object) throws IOException {
        return get(fileName, new Object[]{object});
    }
}
