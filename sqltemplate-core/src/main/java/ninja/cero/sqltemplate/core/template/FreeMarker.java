package ninja.cero.sqltemplate.core.template;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;

public class FreeMarker implements TemplateEngine {
    protected static final Configuration CONFIG = new Configuration(Configuration.VERSION_2_3_21);

    static {
        CONFIG.setClassForTemplateLoading(FreeMarker.class, "/");
        BeansWrapper wrapper = new BeansWrapper(Configuration.VERSION_2_3_21);
        wrapper.setExposeFields(true);
        CONFIG.setObjectWrapper(wrapper);
        CONFIG.setDefaultEncoding("UTF-8");
    }

    @Override
    public String get(String resource, Object context) throws IOException {
        Template template = getTemplate(resource);
        return processTemplate(context, template);
    }

    @Override
    public String get(String resource, Object[] context) throws IOException {
        Template template = getTemplate(resource);
        return processTemplate(context, template);
    }

    protected static Template getTemplate(String templateName) throws IOException {
        return CONFIG.getTemplate(templateName);
    }

    protected static String processTemplate(Object context, Template template) throws IOException {
        try (StringWriter writer = new StringWriter()) {
            template.process(context, writer);
            return writer.getBuffer().toString();
        } catch (TemplateException ex) {
            throw new IOException(ex);
        }
    }
}
