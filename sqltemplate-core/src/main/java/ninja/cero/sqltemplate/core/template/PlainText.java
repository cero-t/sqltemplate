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

public class PlainText implements TemplateEngine {
    /**
     * template cache
     */
    protected final ConcurrentMap<String, String> templateCache = new ConcurrentHashMap<>();

    @Override
    public String get(String resource, Object[] args) throws IOException {
        return resource;
    }

    @Override
    public String get(String resource, Object object) throws IOException {
        return resource;
    }
}
