package ninja.cero.sqltemplate.core;

import ninja.cero.sqltemplate.core.mapper.MapperBuilder;
import ninja.cero.sqltemplate.core.parameter.BeanParameter;
import ninja.cero.sqltemplate.core.parameter.MapParameter;
import ninja.cero.sqltemplate.core.parameter.ParamBuilder;
import ninja.cero.sqltemplate.core.template.TemplateEngine;
import ninja.cero.sqltemplate.core.util.TypeUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BatchBuilder {
    protected JdbcTemplate jdbcTemplate;
    protected NamedParameterJdbcTemplate namedJdbcTemplate;
    protected ParamBuilder paramBuilder;
    protected MapperBuilder mapperBuilder;

    public BatchBuilder(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate, ParamBuilder paramBuilder, MapperBuilder mapperBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.paramBuilder = paramBuilder;
        this.mapperBuilder = mapperBuilder;
    }

    public int[] queries(String... queries) {
        return jdbcTemplate.batchUpdate(queries);
    }

    public BatchArgsBuilder file(String filename) {
        return new BatchArgsBuilder(jdbcTemplate, namedJdbcTemplate, paramBuilder, mapperBuilder, TemplateEngine.TEXT_FILE, filename);
    }

    public BatchArgsBuilder query(String query) {
        return new BatchArgsBuilder(jdbcTemplate, namedJdbcTemplate, paramBuilder, mapperBuilder, TemplateEngine.PLAIN_TEXT, query);
    }
}
