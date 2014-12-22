package ninja.cero.sqltemplate.core.template;

import java.io.IOException;

/**
 *
 */
public interface TemplateEngine {
    /**
     * Get template text
     * @param fileName the filename of template
     * @param args     the arguments to set
     * @return the template text processed by template engine
     * @throws IOException in case of template read failure
     */
    String get(String fileName, Object[] args) throws IOException;

    String get(String fileName, Object object) throws IOException;

    public static TemplateEngine NO_ENGINE = new NoEngine();
    public static TemplateEngine FREEMARKER = new FreeMarker();
}
