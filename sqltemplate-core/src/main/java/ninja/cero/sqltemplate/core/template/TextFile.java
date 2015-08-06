package ninja.cero.sqltemplate.core.template;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextFile implements TemplateEngine {
    /** template cache */
    protected final ConcurrentMap<String, String> templateCache = new ConcurrentHashMap<>();

    @Override
    public String get(String resource, Object[] args) throws IOException {
        String template = templateCache.get(resource);

        if (template != null) {
            return template;
        }

        InputStream in = getClass().getResourceAsStream("/" + resource);
        if (in == null) {
            throw new FileNotFoundException("Template '" + resource + "' not found");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
             Stream<String> stream = br.lines()) {
            template = stream.collect(Collectors.joining("\n"));
        }

        templateCache.put(resource, template);
        return template;
    }

    @Override
    public String get(String resource, Object object) throws IOException {
        return get(resource, new Object[]{object});
    }
}
