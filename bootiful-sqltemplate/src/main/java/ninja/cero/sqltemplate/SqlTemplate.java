package ninja.cero.sqltemplate;

import ninja.cero.sqltemplate.core.ArrayBuilder;
import ninja.cero.sqltemplate.core.BatchArgsBuilder;
import ninja.cero.sqltemplate.core.BatchBuilder;
import ninja.cero.sqltemplate.core.mapper.MapperBuilder;
import ninja.cero.sqltemplate.core.parameter.BeanParameter;
import ninja.cero.sqltemplate.core.parameter.MapParameter;
import ninja.cero.sqltemplate.core.parameter.ParamBuilder;
import ninja.cero.sqltemplate.core.template.TemplateEngine;
import ninja.cero.sqltemplate.core.util.TypeUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.time.ZoneId;
import java.util.Map;

public class SqlTemplate {
    protected final JdbcTemplate jdbcTemplate;

    protected final NamedParameterJdbcTemplate namedJdbcTemplate;

    protected ParamBuilder paramBuilder;

    protected MapperBuilder mapperBuilder;

    public SqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate) {
        this(jdbcTemplate, namedJdbcTemplate, new ParamBuilder(), new MapperBuilder());
    }

    public SqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate, ZoneId zoneId) {
        this(jdbcTemplate, namedJdbcTemplate, new ParamBuilder(zoneId), new MapperBuilder(zoneId));
    }

    public SqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate, ParamBuilder paramBuilder, MapperBuilder mapperBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.paramBuilder = paramBuilder;
        this.mapperBuilder = mapperBuilder;
    }

    public ArrayBuilder file(String filename) {
        return new ArrayBuilder(jdbcTemplate, namedJdbcTemplate, paramBuilder, mapperBuilder, TemplateEngine.FREEMARKER, filename);
    }

    public ArrayBuilder query(String query) {
        return new ArrayBuilder(jdbcTemplate, namedJdbcTemplate, paramBuilder, mapperBuilder, TemplateEngine.PLAIN_TEXT, query);
    }

    public BatchBuilder batch() {
        return new BatchBuilder(jdbcTemplate, namedJdbcTemplate, paramBuilder, mapperBuilder);
    }
}
