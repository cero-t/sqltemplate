package ninja.cero.sqltemplate.core.template;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextFile implements TemplateEngine {
    /** template cache */
    protected final ConcurrentMap<String, String> templateCache = new ConcurrentHashMap<>();

    @Override
    public String get(String resource, Object[] args) {
        String template = templateCache.get(resource);

        if (template != null) {
            return template;
        }

        try {
            InputStream in = getClass().getResourceAsStream("/" + resource);
            if (in == null) {
                throw new FileNotFoundException("Template '" + resource + "' not found");
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                 Stream<String> stream = br.lines()) {
                template = stream.collect(Collectors.joining("\n"));
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        templateCache.put(resource, template);
        return template;
    }

    @Override
    public String get(String resource, Object object) {
        return get(resource, new Object[]{object});
    }
}
