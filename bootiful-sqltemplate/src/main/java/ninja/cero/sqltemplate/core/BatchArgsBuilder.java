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

    public BatchArrayArgsBuilder addBatch(Object... batchArgs) {
        return new BatchArrayArgsBuilder(batchArgs);
    }

    public BatchEntityArgsBuilder addBatch(Object batchEntity) {
        return new BatchEntityArgsBuilder(batchEntity);
    }

    public BatchMapArgsBuilder addBatch(Map<String, Object> batchParam) {
        return new BatchMapArgsBuilder(batchParam);
    }

    public int[] execute() {
        String sql = templateEngine.get(template);
        return jdbcTemplate.batchUpdate(sql);
    }

    public class BatchArrayArgsBuilder {
        private List<Object[]> batchArgList = new ArrayList<>();

        public BatchArrayArgsBuilder(Object[] batchArgs) {
            batchArgList.add(batchArgs);
        }

        public BatchArrayArgsBuilder addBatch(Object... batchArgs) {
            batchArgList.add(batchArgs);
            return this;
        }

        public int[] execute() {
            String sql = templateEngine.get(template);
            Object[][] args = batchArgList.toArray(new Object[batchArgList.size()][]);
            return jdbcTemplate.batchUpdate(sql, paramBuilder.byBatchArgs(args));
        }
    }

    public class BatchEntityArgsBuilder {
        private List<Object> batchEntities = new ArrayList<>();

        public BatchEntityArgsBuilder(Object batchEntity) {
            batchEntities.add(batchEntity);
        }

        public BatchEntityArgsBuilder addBatch(Object batchEntity) {
            batchEntities.add(batchEntity);
            return this;
        }

        public int[] execute() {
            String sql = templateEngine.get(template);
            if (TypeUtils.isSimpleValueType(batchEntities.get(0).getClass())) {
                Object[] args = batchEntities.toArray(new Object[batchEntities.size()]);
                return jdbcTemplate.batchUpdate(sql, paramBuilder.byBatchArgs(args));
            }

            BeanParameter[] beanParameters = batchEntities.stream()
                    .map(paramBuilder::byBean)
                    .toArray(BeanParameter[]::new);
            return namedJdbcTemplate.batchUpdate(sql, beanParameters);
        }
    }

    public class BatchMapArgsBuilder {
        private List<Map<String, Object>> batchParams = new ArrayList<>();

        public BatchMapArgsBuilder(Map<String, Object> batchParam) {
            batchParams.add(batchParam);
        }

        public BatchMapArgsBuilder addBatch(Map<String, Object> batchParam) {
            batchParams.add(batchParam);
            return this;
        }

        public int[] execute() {
            MapParameter[] mapParameters = batchParams.stream()
                    .map(paramBuilder::byMap)
                    .toArray(MapParameter[]::new);

            String sql = templateEngine.get(template);
            return namedJdbcTemplate.batchUpdate(sql, mapParameters);
        }
    }
}
