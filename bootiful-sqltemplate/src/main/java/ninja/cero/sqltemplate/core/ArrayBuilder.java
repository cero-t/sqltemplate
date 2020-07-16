package ninja.cero.sqltemplate.core;

import ninja.cero.sqltemplate.core.executor.ArrayExecutor;
import ninja.cero.sqltemplate.core.executor.EntityExecutor;
import ninja.cero.sqltemplate.core.executor.MapExecutor;
import ninja.cero.sqltemplate.core.executor.QueryExecutor;
import ninja.cero.sqltemplate.core.mapper.MapperBuilder;
import ninja.cero.sqltemplate.core.parameter.ParamBuilder;
import ninja.cero.sqltemplate.core.template.TemplateEngine;
import ninja.cero.sqltemplate.core.util.TypeUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.Map;

public class ArrayBuilder extends ArrayExecutor {
    protected JdbcTemplate jdbcTemplate;
    protected NamedParameterJdbcTemplate namedJdbcTemplate;
    protected ParamBuilder paramBuilder;
    protected MapperBuilder mapperBuilder;
    protected TemplateEngine templateEngine;
    protected String template;

    public ArrayBuilder(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate, ParamBuilder paramBuilder, MapperBuilder mapperBuilder, TemplateEngine templateEngine, String template) {
        super(jdbcTemplate, paramBuilder, mapperBuilder, templateEngine, template);
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.paramBuilder = paramBuilder;
        this.mapperBuilder = mapperBuilder;
        this.templateEngine = templateEngine;
        this.template = template;
    }

    public QueryExecutor args(Object... args) {
        return new ArrayExecutor(jdbcTemplate, paramBuilder, mapperBuilder, templateEngine, template, args);
    }

    public QueryExecutor args(Object entity) {
        if (TypeUtils.isSimpleValueType(entity.getClass())) {
            return new ArrayExecutor(jdbcTemplate, paramBuilder, mapperBuilder, templateEngine, template, entity);
        }

        return new EntityExecutor(jdbcTemplate, namedJdbcTemplate, paramBuilder, mapperBuilder, templateEngine, template, entity);
    }

    public QueryExecutor args(Map<String, Object> params) {
        return new MapExecutor(jdbcTemplate, namedJdbcTemplate, paramBuilder, mapperBuilder, templateEngine, template, params);
    }

    public MapQueryBuilder add(String key, Object value) {
        return new MapQueryBuilder(namedJdbcTemplate, paramBuilder, mapperBuilder, templateEngine, template, new HashMap<>())
                .add(key, value);
    }

    public class MapQueryBuilder extends MapExecutor {
        protected Map<String, Object> params;

        public MapQueryBuilder(NamedParameterJdbcTemplate namedJdbcTemplate, ParamBuilder paramBuilder, MapperBuilder mapperBuilder, TemplateEngine templateEngine, String template, Map<String, Object> params) {
            super(jdbcTemplate, namedJdbcTemplate, paramBuilder, mapperBuilder, templateEngine, template, params);
            this.params = params;
        }

        public MapQueryBuilder add(String key, Object value) {
            params.put(key, value);
            return this;
        }
    }
}
