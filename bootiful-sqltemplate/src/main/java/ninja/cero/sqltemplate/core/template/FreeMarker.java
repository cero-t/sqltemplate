package ninja.cero.sqltemplate.core.template;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;

public class FreeMarker implements TemplateEngine {
    protected static final Configuration CONFIG = new Configuration(Configuration.VERSION_2_3_30);

    static {
        CONFIG.setClassForTemplateLoading(FreeMarker.class, "/");
        BeansWrapper wrapper = new BeansWrapper(Configuration.VERSION_2_3_30);
        wrapper.setExposeFields(true);
        CONFIG.setObjectWrapper(wrapper);
        CONFIG.setDefaultEncoding("UTF-8");
    }

    @Override
    public String get(String resource, Object context) {
        Template template = getTemplate(resource);
        return processTemplate(context, template);
    }

    @Override
    public String get(String resource, Object[] context) {
        Template template = getTemplate(resource);
        return processTemplate(context, template);
    }

    protected static Template getTemplate(String templateName) {
        try {
            return CONFIG.getTemplate(templateName);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    protected static String processTemplate(Object context, Template template) {
        try (StringWriter writer = new StringWriter()) {
            template.process(context, writer);
            return writer.getBuffer().toString();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } catch (TemplateException ex) {
            throw new UncheckedIOException(new IOException(ex));
        }
    }
}