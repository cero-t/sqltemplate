package ninja.cero.sqltemplate.core.executor;

import ninja.cero.sqltemplate.core.mapper.MapperBuilder;
import ninja.cero.sqltemplate.core.parameter.ParamBuilder;
import ninja.cero.sqltemplate.core.stream.StreamResultSetExtractor;
import ninja.cero.sqltemplate.core.template.TemplateEngine;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class ArrayExecutor extends AbstractQueryExecutor {
    private JdbcTemplate jdbcTemplate;
    private ParamBuilder paramBuilder;
    private MapperBuilder mapperBuilder;
    private TemplateEngine templateEngine;
    private String template;
    private Object[] params;

    public ArrayExecutor(JdbcTemplate jdbcTemplate, ParamBuilder paramBuilder, MapperBuilder mapperBuilder, TemplateEngine templateEngine, String template, Object... params) {
        this.jdbcTemplate = jdbcTemplate;
        this.paramBuilder = paramBuilder;
        this.mapperBuilder = mapperBuilder;
        this.templateEngine = templateEngine;
        this.template = template;
        this.params = params;
    }

    @Override
    public <T> List<T> forList(Class<T> clazz) {
        String sql = templateEngine.get(template, params);
        return jdbcTemplate.query(sql, paramBuilder.byArgs(params), mapperBuilder.mapper(clazz));
    }

    @Override
    public List<Map<String, Object>> forList() {
        String sql = templateEngine.get(template, params);
        // TODO: queryForMap使える？
        return jdbcTemplate.query(sql, paramBuilder.byArgs(params), new ColumnMapRowMapper());
    }

    @Override
    public <T, U> U forStream(Class<T> clazz, Function<? super Stream<T>, U> handler) {
        String sql = templateEngine.get(template, params);
        SQLExceptionTranslator excTranslator = jdbcTemplate.getExceptionTranslator();
        ResultSetExtractor<U> extractor = new StreamResultSetExtractor<>(sql, mapperBuilder.mapper(clazz), handler, excTranslator);
        return jdbcTemplate.query(sql, paramBuilder.byArgs(params), extractor);
    }

    @Override
    public <U> U forStream(Function<? super Stream<Map<String, Object>>, U> handler) {
        String sql = templateEngine.get(template, params);
        SQLExceptionTranslator excTranslator = jdbcTemplate.getExceptionTranslator();
        // TODO: can it work with zoneId?
        ResultSetExtractor<U> extractor = new StreamResultSetExtractor<>(sql, new ColumnMapRowMapper(), handler, excTranslator);
        return jdbcTemplate.query(sql, paramBuilder.byArgs(params), extractor);
    }

    @Override
    public int update() {
        String sql = templateEngine.get(template, params);
        return jdbcTemplate.update(sql, paramBuilder.byArgs(params));
    }
}
