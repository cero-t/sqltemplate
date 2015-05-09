package ninja.cero.sqltemplate.core;

import ninja.cero.sqltemplate.core.mapper.BeanMapper;
import ninja.cero.sqltemplate.core.parameter.ArgsParameter;
import ninja.cero.sqltemplate.core.parameter.BeanParameter;
import ninja.cero.sqltemplate.core.parameter.MapParameter;
import ninja.cero.sqltemplate.core.template.TemplateEngine;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlTemplate {
    protected final JdbcTemplate jdbcTemplate;

    protected final NamedParameterJdbcTemplate namedJdbcTemplate;

    protected TemplateEngine templateEngine;

    public SqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate) {
        this(jdbcTemplate, namedJdbcTemplate, TemplateEngine.NO_ENGINE);
    }

    public SqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate, TemplateEngine templateEngine) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.templateEngine = templateEngine;
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
        String sql = get(fileName, args);
        return jdbcTemplate.query(sql, ArgsParameter.of(args), BeanMapper.of(clazz));
    }

    public <T> List<T> forList(String fileName, Class<T> clazz, Map<String, Object> params) {
        String sql = get(fileName, params);
        return namedJdbcTemplate.query(sql, MapParameter.of(params), BeanMapper.of(clazz));
    }

    public <T> List<T> forList(String fileName, Class<T> clazz, Object entity) {
        String sql = get(fileName, entity);

        if (BeanUtils.isSimpleValueType(entity.getClass())) {
            return jdbcTemplate.query(sql, ArgsParameter.of(entity), BeanMapper.of(clazz));
        }

        return namedJdbcTemplate.query(sql, BeanParameter.of(entity), BeanMapper.of(clazz));
    }

    public int update(String fileName, Map<String, Object> params) {
        String sql = get(fileName, params);
        return namedJdbcTemplate.update(sql, MapParameter.of(params));
    }

    public int update(String fileName, Object entity) {
        String sql = get(fileName, entity);

        if (BeanUtils.isSimpleValueType(entity.getClass())) {
            return jdbcTemplate.update(sql, ArgsParameter.of(entity));
        }

        return namedJdbcTemplate.update(sql, BeanParameter.of(entity));
    }

    public int update(String fileName, Object... args) {
        String sql = get(fileName, args);
        return jdbcTemplate.update(sql, ArgsParameter.of(args));
    }

    public <T> MapQueryBuilder<T> query(String fileName, Class<T> clazz) {
        return new MapQueryBuilder<>(fileName, clazz);
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
            String sql = get(fileName, params);
            return namedJdbcTemplate.query(sql, MapParameter.of(params), BeanMapper.of(clazz));
        }
    }
}
