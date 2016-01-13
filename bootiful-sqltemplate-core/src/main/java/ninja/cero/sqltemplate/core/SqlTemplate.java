package ninja.cero.sqltemplate.core;

import ninja.cero.sqltemplate.core.parameter.MapperBuilder;
import ninja.cero.sqltemplate.core.template.TemplateEngine;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlTemplate {
    protected final JdbcTemplate jdbcTemplate;

    protected final NamedParameterJdbcTemplate namedJdbcTemplate;

    protected TemplateEngine templateEngine;

    protected MapperBuilder paramBuilder;

    public SqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate) {
        this(jdbcTemplate, namedJdbcTemplate, TemplateEngine.TEXT_FILE, new MapperBuilder());
    }

    public SqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate, TemplateEngine templateEngine) {
        this(jdbcTemplate, namedJdbcTemplate, templateEngine, new MapperBuilder());
    }

    public SqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate, ZoneId zoneId) {
        this(jdbcTemplate, namedJdbcTemplate, TemplateEngine.TEXT_FILE, new MapperBuilder(zoneId));
    }

    public SqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate, TemplateEngine templateEngine, ZoneId zoneId) {
        this(jdbcTemplate, namedJdbcTemplate, templateEngine, new MapperBuilder(zoneId));
    }

    public SqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate, TemplateEngine templateEngine, MapperBuilder paramBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.templateEngine = templateEngine;
        this.paramBuilder = paramBuilder;
    }

    public <T> T forObject(String fileName, Class<T> clazz, Object... args) {
        List<T> list = forList(fileName, clazz, args);
        return DataAccessUtils.singleResult(list);
    }

    public <T> T forObject(String fileName, Class<T> clazz, Map<String, Object> params) {
        List<T> list = forList(fileName, clazz, params);
        return DataAccessUtils.singleResult(list);
    }

    public <T> T forObject(String fileName, Class<T> clazz, Object entity) {
        List<T> list = forList(fileName, clazz, entity);
        return DataAccessUtils.singleResult(list);
    }

    public <T> List<T> forList(String fileName, Class<T> clazz, Object... args) {
        String sql = getTemplate(fileName, args);
        return jdbcTemplate.query(sql, paramBuilder.byArgs(args), paramBuilder.mapper(clazz));
    }

    public <T> List<T> forList(String fileName, Class<T> clazz, Map<String, Object> params) {
        String sql = getTemplate(fileName, params);
        return namedJdbcTemplate.query(sql, paramBuilder.byMap(params), paramBuilder.mapper(clazz));
    }

    public <T> List<T> forList(String fileName, Class<T> clazz, Object entity) {
        String sql = getTemplate(fileName, entity);

        if (BeanUtils.isSimpleValueType(entity.getClass())) {
            return jdbcTemplate.query(sql, paramBuilder.byArgs(entity), paramBuilder.mapper(clazz));
        }

        return namedJdbcTemplate.query(sql, paramBuilder.byBean(entity), paramBuilder.mapper(clazz));
    }

    public int update(String fileName, Object... args) {
        String sql = getTemplate(fileName, args);
        return jdbcTemplate.update(sql, paramBuilder.byArgs(args));
    }

    public int update(String fileName, Map<String, Object> params) {
        String sql = getTemplate(fileName, params);
        return namedJdbcTemplate.update(sql, paramBuilder.byMap(params));
    }

    public int update(String fileName, Object entity) {
        String sql = getTemplate(fileName, entity);

        if (BeanUtils.isSimpleValueType(entity.getClass())) {
            return jdbcTemplate.update(sql, paramBuilder.byArgs(entity));
        }

        return namedJdbcTemplate.update(sql, paramBuilder.byBean(entity));
    }

    public MapUpdateBuilder update(String fileName) {
        return new MapUpdateBuilder(fileName);
    }

    public <T> MapQueryBuilder<T> query(String fileName, Class<T> clazz) {
        return new MapQueryBuilder<>(fileName, clazz);
    }

    protected String getTemplate(String fileName, Object[] args) {
        try {
            return templateEngine.get(fileName, args);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    protected String getTemplate(String fileName, Object param) {
        try {
            return templateEngine.get(fileName, param);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public class MapQueryBuilder<T> {
        protected Map<String, Object> params = new HashMap<>();
        protected String fileName;
        protected Class<T> clazz;

        public MapQueryBuilder(String fileName, Class<T> clazz) {
            this.fileName = fileName;
            this.clazz = clazz;
        }

        public MapQueryBuilder<T> add(String key, Object value) {
            params.put(key, value);
            return this;
        }

        public T forObject() {
            List<T> list = forList();
            return DataAccessUtils.singleResult(list);
        }

        public List<T> forList() {
            String sql = getTemplate(fileName, params);
            return namedJdbcTemplate.query(sql, paramBuilder.byMap(params), paramBuilder.mapper(clazz));
        }
    }

    public class MapUpdateBuilder {
        protected Map<String, Object> params = new HashMap<>();
        protected String fileName;

        public MapUpdateBuilder(String fileName) {
            this.fileName = fileName;
        }

        public MapUpdateBuilder add(String key, Object value) {
            params.put(key, value);
            return this;
        }

        public int execute() {
            return update(fileName, params);
        }
    }
}
