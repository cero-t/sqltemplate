package springboot.sqltemplate.core.builder;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import springboot.sqltemplate.core.mapper.BeanMapper;
import springboot.sqltemplate.core.parameter.ArgsParameter;
import springboot.sqltemplate.core.template.TemplateEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArgsQueryBuilder<T> extends QueryBuilder<T> {
    protected List<Object> params;
    protected JdbcTemplate jdbcTemplate;

    public ArgsQueryBuilder(String fileName, Class<T> clazz, TemplateEngine templateEngine, JdbcTemplate jdbcTemplate) {
        super(fileName, clazz, templateEngine);
        this.params = new ArrayList<>();
        this.jdbcTemplate = jdbcTemplate;
    }

    public ArgsQueryBuilder<T> add(Object param) {
        params.add(param);
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
        Object[] args = params.toArray(new Object[params.size()]);
        return jdbcTemplate.query(sql, ArgsParameter.of(args), BeanMapper.of(clazz));
    }
}
