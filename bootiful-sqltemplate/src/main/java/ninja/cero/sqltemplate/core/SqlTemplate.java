package ninja.cero.sqltemplate.core;

import ninja.cero.sqltemplate.core.mapper.MapperBuilder;
import ninja.cero.sqltemplate.core.parameter.BeanParameter;
import ninja.cero.sqltemplate.core.parameter.MapParameter;
import ninja.cero.sqltemplate.core.parameter.ParamBuilder;
import ninja.cero.sqltemplate.core.template.TemplateEngine;
import ninja.cero.sqltemplate.core.util.TypeUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SqlTemplate {
    public static final Object[] EMPTY_ARGS = new Object[0];
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

    public ArgsBuilder file(String filename) {
        return new ArgsBuilder(jdbcTemplate, namedJdbcTemplate, paramBuilder, mapperBuilder, TemplateEngine.FREEMARKER, filename);
    }

    public ArgsBuilder query(String query) {
        return new ArgsBuilder(jdbcTemplate, namedJdbcTemplate, paramBuilder, mapperBuilder, TemplateEngine.PLAIN_TEXT, query);
    }

    public int[] batchUpdate(String... queries) {
        return jdbcTemplate.batchUpdate(queries);
    }

    public int[] batchUpdate(String fileName, Object[][] batchParams) {
        String sql = TemplateEngine.TEXT_FILE.get(fileName, EMPTY_ARGS);
        return jdbcTemplate.batchUpdate(sql, paramBuilder.byBatchArgs(batchParams));
    }

    public int[] batchUpdate(String fileName, Map<String, Object>[] batchParams) {
        String sql = TemplateEngine.TEXT_FILE.get(fileName, EMPTY_ARGS);

        MapParameter[] params = new MapParameter[batchParams.length];
        for (int i = 0; i < batchParams.length; i++) {
            params[i] = paramBuilder.byMap(batchParams[i]);
        }

        return namedJdbcTemplate.batchUpdate(sql, params);
    }

    public int[] batchUpdate(String fileName, Object[] batchParams) {
        String sql = TemplateEngine.TEXT_FILE.get(fileName, EMPTY_ARGS);
        if (batchParams.length == 0) {
            return jdbcTemplate.batchUpdate(sql);
        }

        if (TypeUtils.isSimpleValueType(batchParams[0].getClass())) {
            return jdbcTemplate.batchUpdate(sql, paramBuilder.byBatchArgs(batchParams));
        }

        BeanParameter[] params = new BeanParameter[batchParams.length];
        for (int i = 0; i < batchParams.length; i++) {
            params[i] = paramBuilder.byBean(batchParams[i]);
        }

        return namedJdbcTemplate.batchUpdate(sql, params);
    }
}
