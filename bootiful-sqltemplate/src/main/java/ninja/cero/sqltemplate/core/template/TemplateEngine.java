package ninja.cero.sqltemplate.core.template;

/**
 * TemplateEngine
 */
public interface TemplateEngine {
    /**
     * Get template text
     *
     * @param resource the resource name of template such as template filename
     * @param args the arguments to set
     * @return the template text processed by template engine
     */
    String get(String resource, Object args);

    /**
     * Get template text
     *
     * @param resource the resource name of template such as template filename
     * @return the template text processed by template engine
     */
    String get(String resource);

    TemplateEngine TEXT_FILE = new TextFile();
    TemplateEngine PLAIN_TEXT = new PlainText();
    TemplateEngine FREEMARKER = new FreeMarker();
}
