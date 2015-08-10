package ninja.cero.sqltemplate.core.template;

import java.io.IOException;

/**
 *
 */
public interface TemplateEngine {
    /**
     * Get template text
     * @param resource the resource name of template such as template filename
     * @param args     the arguments to set
     * @return the template text processed by template engine
     * @throws IOException in case of template read failure
     */
    String get(String resource, Object[] args) throws IOException;

    /**
     * Get template text
     * @param resource the resource name of template such as template filename
     * @param object   the arguments to set
     * @return the template text processed by template engine
     * @throws IOException in case of template read failure
     */
    String get(String resource, Object object) throws IOException;

    public static TemplateEngine TEXT_FILE = new TextFile();
    public static TemplateEngine PLAIN_TEXT = new PlainText();
    public static TemplateEngine FREEMARKER = new FreeMarker();
}
