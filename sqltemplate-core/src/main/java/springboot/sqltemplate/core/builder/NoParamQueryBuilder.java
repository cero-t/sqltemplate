package springboot.sqltemplate.core.builder;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import springboot.sqltemplate.core.mapper.BeanMapper;
import springboot.sqltemplate.core.template.TemplateEngine;

import java.util.List;

public class NoParamQueryBuilder<T> extends QueryBuilder<T> {
    protected JdbcTemplate jdbcTemplate;
    protected NamedParameterJdbcTemplate namedJdbcTemplate;

    public NoParamQueryBuilder(String fileName, Class<T> clazz, TemplateEngine templateEngine, JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate) {
        super(fileName, clazz, templateEngine);
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    public ArgsQueryBuilder<T> add(Object param) {
        ArgsQueryBuilder<T> builder = new ArgsQueryBuilder<>(fileName, clazz, templateEngine, jdbcTemplate);
        builder.add(param);
        return builder;
    }

    public MapQueryBuilder<T> add(String key, Object value) {
        MapQueryBuilder<T> builder = new MapQueryBuilder<>(fileName, clazz, templateEngine, namedJdbcTemplate);
        builder.add(key, value);
        return builder;
    }

    @Override
    public T forObject() {
        List<T> list = forList();
        return DataAccessUtils.singleResult(list);
    }

    @Override
    public List<T> forList() {
        String sql = get(fileName, null);
        return jdbcTemplate.query(sql, BeanMapper.of(clazz));
    }
}
