package springboot.sqltemplate.core.builder;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import springboot.sqltemplate.core.mapper.BeanMapper;
import springboot.sqltemplate.core.parameter.MapParameter;
import springboot.sqltemplate.core.template.TemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapQueryBuilder<T> extends QueryBuilder<T> {
    protected Map<String, Object> params;
    protected NamedParameterJdbcTemplate namedJdbcTemplate;

    public MapQueryBuilder(String fileName, Class<T> clazz, TemplateEngine templateEngine, NamedParameterJdbcTemplate namedJdbcTemplate) {
        super(fileName, clazz, templateEngine);
        this.params = new HashMap<>();
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    public MapQueryBuilder<T> add(String key, Object value) {
        params.put(key, value);
        return this;
    }

    @Override
    public T forObject() {
        List<T> list = forList();
        return DataAccessUtils.singleResult(list);
    }

    @Override
    public List<T> forList() {
        String sql = get(fileName, params);
        return namedJdbcTemplate.query(sql, MapParameter.of(params), BeanMapper.of(clazz));
    }
}
