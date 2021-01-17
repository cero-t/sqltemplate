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

public class BatchArgsBuilder {
    protected JdbcTemplate jdbcTemplate;
    protected NamedParameterJdbcTemplate namedJdbcTemplate;
    protected ParamBuilder paramBuilder;
    protected MapperBuilder mapperBuilder;
    protected TemplateEngine templateEngine;
    protected String template;

    public BatchArgsBuilder(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate, ParamBuilder paramBuilder, MapperBuilder mapperBuilder, TemplateEngine templateEngine, String template) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.paramBuilder = paramBuilder;
        this.mapperBuilder = mapperBuilder;
        this.templateEngine = templateEngine;
        this.template = template;
    }

    public BatchArrayArgsBuilder addParams(Object... args) {
        return new BatchArrayArgsBuilder(args);
    }

    public BatchEntityArgsBuilder addParam(Object entity) {
        return new BatchEntityArgsBuilder(entity);
    }

    public BatchMapArgsBuilder addParam(Map<String, Object> map) {
        return new BatchMapArgsBuilder(map);
    }

    public int[] execute() {
        String sql = templateEngine.get(template);
        return jdbcTemplate.batchUpdate(sql);
    }

    public class BatchArrayArgsBuilder {
        private List<Object[]> arrayArgs = new ArrayList<>();

        public BatchArrayArgsBuilder(Object[] args) {
            arrayArgs.add(args);
        }

        public BatchArrayArgsBuilder addArgs(Object... args) {
            arrayArgs.add(args);
            return this;
        }

        public int[] execute() {
            String sql = templateEngine.get(template);
            Object[][] args = arrayArgs.toArray(new Object[arrayArgs.size()][]);
            return jdbcTemplate.batchUpdate(sql, paramBuilder.byBatchArgs(args));
        }
    }

    public class BatchEntityArgsBuilder {
        private List<Object> entityArgs = new ArrayList<>();

        public BatchEntityArgsBuilder(Object entity) {
            entityArgs.add(entity);
        }

        public BatchEntityArgsBuilder addArgs(Object entity) {
            entityArgs.add(entity);
            return this;
        }

        public int[] execute() {
            String sql = templateEngine.get(template);
            if (TypeUtils.isSimpleValueType(entityArgs.get(0).getClass())) {
                Object[] args = entityArgs.toArray(new Object[entityArgs.size()]);
                return jdbcTemplate.batchUpdate(sql, paramBuilder.byBatchArgs(args));
            }

            BeanParameter[] beanParameters = entityArgs.stream()
                    .map(paramBuilder::byBean)
                    .toArray(BeanParameter[]::new);
            return namedJdbcTemplate.batchUpdate(sql, beanParameters);
        }
    }

    public class BatchMapArgsBuilder {
        private List<Map<String, Object>> mapArgs = new ArrayList<>();

        public BatchMapArgsBuilder(Map<String, Object> params) {
            mapArgs.add(params);
        }

        public BatchMapArgsBuilder addArgs(Map<String, Object> params) {
            mapArgs.add(params);
            return this;
        }

        public int[] execute() {
            MapParameter[] mapParameters = mapArgs.stream()
                    .map(paramBuilder::byMap)
                    .toArray(MapParameter[]::new);

            String sql = templateEngine.get(template);
            return namedJdbcTemplate.batchUpdate(sql, mapParameters);
        }
    }
}
