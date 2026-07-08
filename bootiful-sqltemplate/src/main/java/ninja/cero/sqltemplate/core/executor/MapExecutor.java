package ninja.cero.sqltemplate.core.executor;

import ninja.cero.sqltemplate.core.mapper.MapperBuilder;
import ninja.cero.sqltemplate.core.parameter.ParamBuilder;
import ninja.cero.sqltemplate.core.template.TemplateEngine;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class MapExecutor extends AbstractQueryExecutor {
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    private ParamBuilder paramBuilder;
    private MapperBuilder mapperBuilder;
    private TemplateEngine templateEngine;
    private String template;
    private Map<String, Object> params;

    public MapExecutor(NamedParameterJdbcTemplate namedJdbcTemplate, ParamBuilder paramBuilder, MapperBuilder mapperBuilder, TemplateEngine templateEngine, String template, Map<String, Object> params) {
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.paramBuilder = paramBuilder;
        this.mapperBuilder = mapperBuilder;
        this.templateEngine = templateEngine;
        this.template = template;
        this.params = params;
    }

    @Override
    public <T> List<T> forList(Class<T> clazz) {
        String sql = templateEngine.get(template, params);
        return namedJdbcTemplate.query(sql, paramBuilder.byMap(params), mapperBuilder.mapper(clazz));
    }

    @Override
    public List<Map<String, Object>> forList() {
        String sql = templateEngine.get(template, params);
        return namedJdbcTemplate.queryForList(sql, paramBuilder.byMap(params));
    }

    @Override
    public <T, U> U forStream(Class<T> clazz, Function<? super Stream<T>, U> handler) {
        String sql = templateEngine.get(template, params);
        try (Stream<T> stream = namedJdbcTemplate.queryForStream(sql, paramBuilder.byMap(params), mapperBuilder.mapper(clazz))) {
            return handler.apply(stream);
        }
    }

    @Override
    public <U> U forStream(Function<? super Stream<Map<String, Object>>, U> handler) {
        String sql = templateEngine.get(template, params);
        try (Stream<Map<String, Object>> stream = namedJdbcTemplate.queryForStream(sql, paramBuilder.byMap(params), new ColumnMapRowMapper())) {
            return handler.apply(stream);
        }
    }

    @Override
    public <T> Stream<T> forStream(Class<T> clazz) {
        String sql = templateEngine.get(template, params);
        return namedJdbcTemplate.queryForStream(sql, paramBuilder.byMap(params), mapperBuilder.mapper(clazz));
    }

    @Override
    public Stream<Map<String, Object>> forStream() {
        String sql = templateEngine.get(template, params);
        return namedJdbcTemplate.queryForStream(sql, paramBuilder.byMap(params), new ColumnMapRowMapper());
    }

    @Override
    public int update() {
        String sql = templateEngine.get(template, params);
        return namedJdbcTemplate.update(sql, paramBuilder.byMap(params));
    }
}
