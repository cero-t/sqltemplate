package springboot.sqltemplate.core.template;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;

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
    public String get(String templateName, Object context) {
        Template template = getTemplate(templateName);
        return processTemplate(context, template);
    }

    @Override
    public String get(String templateName, Object[] context) {
        Template template = getTemplate(templateName);
        return processTemplate(context, template);
    }

    protected static Template getTemplate(String templateName) {
        Template template;
        try {
            template = CONFIG.getTemplate(templateName);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        return template;
    }

    protected static String processTemplate(Object context, Template template) {
        try (StringWriter writer = new StringWriter()) {
            template.process(context, writer);
            return writer.getBuffer().toString();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } catch (TemplateException ex) {
            throw new RuntimeException(ex);
        }
    }
}
