package springboot.sqltemplate.core.builder;

import springboot.sqltemplate.core.template.TemplateEngine;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public abstract class QueryBuilder<T> {
    protected String fileName;
    protected Class<T> clazz;
    protected TemplateEngine templateEngine;

    public abstract T forObject();

    public abstract List<T> forList();

    public QueryBuilder(String fileName, Class<T> clazz, TemplateEngine templateEngine) {
        this.fileName = fileName;
        this.clazz = clazz;
        this.templateEngine = templateEngine;
    }

    protected String get(String fileName, Object[] args) {
        try {
            return templateEngine.get(fileName, args);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    protected String get(String fileName, Object param) {
        try {
            return templateEngine.get(fileName, param);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
