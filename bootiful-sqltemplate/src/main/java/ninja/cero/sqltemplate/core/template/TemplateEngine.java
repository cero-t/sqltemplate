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

    public static TemplateEngine TEXT_FILE = new TextFile();
    public static TemplateEngine PLAIN_TEXT = new PlainText();
    public static TemplateEngine FREEMARKER = new FreeMarker();
}
