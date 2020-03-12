package ninja.cero.sqltemplate.core;

import ninja.cero.sqltemplate.core.mapper.MapperBuilder;
import ninja.cero.sqltemplate.core.parameter.BeanParameter;
import ninja.cero.sqltemplate.core.parameter.MapParameter;
import ninja.cero.sqltemplate.core.parameter.ParamBuilder;
import ninja.cero.sqltemplate.core.stream.StreamResultSetExtractor;
import ninja.cero.sqltemplate.core.template.TemplateEngine;
import ninja.cero.sqltemplate.core.util.TypeUtils;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class SqlTemplate {
    public static final Object[] EMPTY_ARGS = new Object[0];
    protected final JdbcTemplate jdbcTemplate;

    protected final NamedParameterJdbcTemplate namedJdbcTemplate;

    protected TemplateEngine templateEngine;

    protected ParamBuilder paramBuilder;

    protected MapperBuilder mapperBuilder;

    public SqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate) {
        this(jdbcTemplate, namedJdbcTemplate, TemplateEngine.TEXT_FILE, new ParamBuilder(), new MapperBuilder());
    }

    /**
     * Deprecated.
     * Use {@link FreeMarkerSqlTemplate}, {@link PlainTextSqlTemplate} or original class extends {@link SqlTemplate}.
     *
     * @param jdbcTemplate      The JdbcTemplate to use
     * @param namedJdbcTemplate The NamedParameterJdbcTemplate to use
     * @param templateEngine    The tepmlate engine to use
     */
    @Deprecated
    public SqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate, TemplateEngine templateEngine) {
        this(jdbcTemplate, namedJdbcTemplate, templateEngine, new ParamBuilder(), new MapperBuilder());
    }

    public SqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate, ZoneId zoneId) {
        this(jdbcTemplate, namedJdbcTemplate, TemplateEngine.TEXT_FILE, new ParamBuilder(zoneId), new MapperBuilder(zoneId));
    }

    /**
     * Deprecated.
     * Use {@link FreeMarkerSqlTemplate}, {@link PlainTextSqlTemplate} or original class extends {@link SqlTemplate}.
     *
     * @param jdbcTemplate      The JdbcTemplate to use
     * @param namedJdbcTemplate The NamedParameterJdbcTemplate to use
     * @param templateEngine    The tepmlate engine to use
     * @param zoneId            The zoneId for zone aware date type such as ZonedDateTime, OffsetDateTime
     */
    @Deprecated
    public SqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate, TemplateEngine templateEngine, ZoneId zoneId) {
        this(jdbcTemplate, namedJdbcTemplate, templateEngine, new ParamBuilder(zoneId), new MapperBuilder(zoneId));
    }

    public SqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate, TemplateEngine templateEngine, ParamBuilder paramBuilder, MapperBuilder mapperBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.templateEngine = templateEngine;
        this.paramBuilder = paramBuilder;
        this.mapperBuilder = mapperBuilder;
    }

    public <T> T forObject(String fileName, Class<T> clazz) {
        List<T> list = forList(fileName, clazz);
        return DataAccessUtils.singleResult(list);
    }

    public <T> T forObject(String fileName, Class<T> clazz, Object... args) {
        List<T> list = forList(fileName, clazz, args);
        return DataAccessUtils.singleResult(list);
    }

    public <T> T forObject(String fileName, Class<T> clazz, Map<String, Object> params) {
        List<T> list = forList(fileName, clazz, params);
        return DataAccessUtils.singleResult(list);
    }

    public <T> T forObject(String fileName, Class<T> clazz, Object entity) {
        List<T> list = forList(fileName, clazz, entity);
        return DataAccessUtils.singleResult(list);
    }

    public <T> List<T> forList(String fileName, Class<T> clazz) {
        String sql = getTemplate(fileName, EMPTY_ARGS);
        return jdbcTemplate.query(sql, mapperBuilder.mapper(clazz));
    }

    public <T> List<T> forList(String fileName, Class<T> clazz, Object... args) {
        String sql = getTemplate(fileName, args);
        return jdbcTemplate.query(sql, paramBuilder.byArgs(args), mapperBuilder.mapper(clazz));
    }

    public <T> List<T> forList(String fileName, Class<T> clazz, Map<String, Object> params) {
        String sql = getTemplate(fileName, params);
        return namedJdbcTemplate.query(sql, paramBuilder.byMap(params), mapperBuilder.mapper(clazz));
    }

    public <T> List<T> forList(String fileName, Class<T> clazz, Object entity) {
        String sql = getTemplate(fileName, entity);

        if (TypeUtils.isSimpleValueType(entity.getClass())) {
            return jdbcTemplate.query(sql, paramBuilder.byArgs(entity), mapperBuilder.mapper(clazz));
        }

        return namedJdbcTemplate.query(sql, paramBuilder.byBean(entity), mapperBuilder.mapper(clazz));
    }

    /**
     * Executes a query, passing the row objects to {@code handleStream} as a stream.
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * int totalSalary = sqlTemplate.forStream("/path/to/query.sql", Emp.class,
     *         (Stream&lt;Emp&gt; empStream) -&gt; empStream.mapToInt(Emp::getSalary).sum());
     * </pre>
     *
     * @param fileName SQL specifier
     * @param clazz the row object class
     * @param handleStream  the function which is applied to the stream
     * @param <T> the row object type
     * @param <U> the result type
     * @return the result of {@code handleStream}
     * @throws org.springframework.dao.DataAccessException if there is any problem
     */
    public <T, U> U forStream(
            String fileName,
            Class<T> clazz,
            Function<? super Stream<T>, U> handleStream) {
        String sql = getTemplate(fileName, EMPTY_ARGS);
        PreparedStatementSetter pss = paramBuilder.byArgs(new Object[0]);
        RowMapper<T> mapper = mapperBuilder.mapper(clazz);
        return queryStreamWithOrdinalParams(sql, pss, mapper, handleStream);
    }

    /**
     * Executes a query using {@code args} as the parameters,
     * passing the row objects to {@code handleStream} as a stream.
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * int totalSalary = sqlTemplate.forStream("/path/to/query.sql", Emp.class,
     *         new Object[] { 30, "SALESMAN" },
     *         empStream -&gt; empStream.mapToInt(Emp::getSalary).sum());
     * </pre>
     *
     * @param fileName parameterized SQL specifier
     * @param clazz the result object class
     * @param args the parameters
     * @param handleStream the function which is applied to the stream
     * @param <T> the row object type
     * @param <U> the result type
     * @return the result of {@code handleStream}
     * @throws org.springframework.dao.DataAccessException if there is any problem
     */
    public <T, U> U forStream(
            String fileName,
            Class<T> clazz,
            Object[] args,
            Function<? super Stream<T>, U> handleStream) {
        String sql = getTemplate(fileName, args);
        PreparedStatementSetter pss = paramBuilder.byArgs(args);
        RowMapper<T> mapper = mapperBuilder.mapper(clazz);
        return queryStreamWithOrdinalParams(sql, pss, mapper, handleStream);
    }

    /**
     * Executes a query using {@code params} as the named parameters,
     * passing the row objects to {@code handleStream} as a stream.
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * Map&lt;String, Object&gt; params = new HashMap&gt;&lt;();
     * params.put("deptno", 30);
     * params.put("job", "SALESMAN");
     * int totalSalary = sqlTemplate.forStream("/path/to/query.sql", Emp.class, params,
     *         empStream -&gt; empStream.mapToInt(Emp::getSalary).sum());
     * </pre>
     *
     * @param fileName parameterized SQL specifier
     * @param clazz the result object class
     * @param params the named parameters
     * @param handleStream  the function which is applied to the stream
     * @param <T> the row object type
     * @param <U> the result type
     * @return the result of {@code handleStream}
     * @throws org.springframework.dao.DataAccessException if there is any problem
     */
    public <T, U> U forStream(
            String fileName,
            Class<T> clazz,
            Map<String, Object> params,
            Function<? super Stream<T>, U> handleStream) {
        String sql = getTemplate(fileName, params);
        SqlParameterSource sps = paramBuilder.byMap(params);
        RowMapper<T> mapper = mapperBuilder.mapper(clazz);
        return queryStreamWithNamedParams(sql, sps, mapper, handleStream);
    }

    /**
     * Executes a query, passing the row objects to {@code handleStream} as a stream.
     *
     * <p>{@code entity} is used as the single parameter if it is a simple value,
     * or as the container of the named parameters if it is a bean.</p>
     *
     * <p>Example usage with a simple value parameter:</p>
     *
     * <pre>
     * int totalSalary = sqlTemplate.forStream("/path/to/query.sql", Emp.class, "SALESMAN",
     *         empStream -&gt; empStream.mapToInt(Emp::getSalary).sum());
     * </pre>
     *
     * <p>Example usage with a bean parameter:</p>
     *
     * <pre>
     * emp.setDeptno(30);
     * emp.setJob("SALESMAN");
     * int totalSalary = sqlTemplate.forStream("/path/to/query.sql", Emp.class, emp,
     *         empStream -&gt; empStream.mapToInt(Emp::getSalary).sum());
     * </pre>
     *
     * @param fileName parameterized SQL specifier
     * @param clazz the result object class
     * @param entity the single parameter, or the container of the named parameters
     * @param handleStream  the function which is applied to the stream
     * @param <T> the row object type
     * @param <U> the result type
     * @return the result of {@code handleStream}
     * @throws org.springframework.dao.DataAccessException if there is any problem
     */
    public <T, U> U forStream(
            String fileName,
            Class<T> clazz,
            Object entity,
            Function<? super Stream<T>, U> handleStream) {
        String sql = getTemplate(fileName, entity);
        RowMapper<T> mapper = mapperBuilder.mapper(clazz);
        SQLExceptionTranslator excTranslator = jdbcTemplate.getExceptionTranslator();
        if (TypeUtils.isSimpleValueType(entity.getClass())) {
            PreparedStatementSetter pss = paramBuilder.byArgs(entity);
            return queryStreamWithOrdinalParams(sql, pss, mapper, handleStream);
        } else {
            SqlParameterSource sps = paramBuilder.byBean(entity);
            return queryStreamWithNamedParams(sql, sps, mapper, handleStream);
        }
    }

    public Map<String, Object> forMap(String fileName) {
        List<Map<String, Object>> list = forList(fileName);
        return DataAccessUtils.singleResult(list);
    }

    public Map<String, Object> forMap(String fileName, Object... args) {
        List<Map<String, Object>> list = forList(fileName, args);
        return DataAccessUtils.singleResult(list);
    }

    public Map<String, Object> forMap(String fileName, Map<String, Object> params) {
        List<Map<String, Object>> list = forList(fileName, params);
        return DataAccessUtils.singleResult(list);
    }

    public Map<String, Object> forMap(String fileName, Object entity) {
        List<Map<String, Object>> list = forList(fileName, entity);
        return DataAccessUtils.singleResult(list);
    }

    public List<Map<String, Object>> forList(String fileName) {
        String sql = getTemplate(fileName, EMPTY_ARGS);
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> forList(String fileName, Object... args) {
        String sql = getTemplate(fileName, args);
        return jdbcTemplate.query(sql, paramBuilder.byArgs(args), new ColumnMapRowMapper());
    }

    public List<Map<String, Object>> forList(String fileName, Map<String, Object> params) {
        String sql = getTemplate(fileName, params);
        return namedJdbcTemplate.queryForList(sql, paramBuilder.byMap(params));
    }

    public List<Map<String, Object>> forList(String fileName, Object entity) {
        String sql = getTemplate(fileName, entity);

        if (TypeUtils.isSimpleValueType(entity.getClass())) {
            return jdbcTemplate.queryForList(sql, entity);
        }

        return namedJdbcTemplate.queryForList(sql, paramBuilder.byBean(entity));
    }

    /**
     * Executes a query,
     * passing the column maps to {@code handleStream} as a stream.
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * int totalSalary = sqlTemplate.forStream("/path/to/query.sql",
     *         mapStream -&gt; mapStream.mapToInt(map -&gt; (Integer) map.get("salary")).sum());
     * </pre>
     *
     * @param fileName SQL specifier
     * @param handleStream the function which is applied to the stream
     * @param <U> the result type
     * @return the result of {@code handleStream}
     * @throws org.springframework.dao.DataAccessException if there is any problem
     */
    public <U> U forStream(
            String fileName,
            Function<? super Stream<Map<String, Object>>, U> handleStream) {
        String sql = getTemplate(fileName, EMPTY_ARGS);
        PreparedStatementSetter pss = paramBuilder.byArgs(new Object[0]);
        RowMapper<Map<String, Object>> mapper = new ColumnMapRowMapper();
        return queryStreamWithOrdinalParams(sql, pss, mapper, handleStream);
    }

    /**
     * Executes a query using {@code args} as the parameters,
     * passing the column maps to {@code handleStream} as a stream.
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * int totalSalary = sqlTemplate.forStream("/path/to/query.sql",
     *         new Object[] { 30, "SALESMAN" },
     *         mapStream -&gt; mapStream.mapToInt(map -&gt; (Integer) map.get("salary")).sum());
     * </pre>
     *
     * @param fileName parameterized SQL specifier
     * @param args the parameters
     * @param handleStream  the function which is applied to the stream
     * @param <U> the result type
     * @return the result of {@code handleStream}
     * @throws org.springframework.dao.DataAccessException if there is any problem
     */
    public <U> U forStream(
            String fileName,
            Object[] args,
            Function<? super Stream<Map<String, Object>>, U> handleStream) {
        String sql = getTemplate(fileName, args);
        PreparedStatementSetter pss = paramBuilder.byArgs(args);
        RowMapper<Map<String, Object>> mapper = new ColumnMapRowMapper();
        return queryStreamWithOrdinalParams(sql, pss, mapper, handleStream);
    }

    /**
     * Executes a query using {@code params} as the named parameters,
     * passing the column maps to {@code handleStream} as a stream.
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * Map&lt;String, Object&gt; params = new HashMap&gt;&lt;();
     * params.put("deptno", 30);
     * params.put("job", "SALESMAN");
     * int totalSalary = sqlTemplate.forStream("/path/to/query.sql", params,
     *         mapStream -&gt; mapStream.mapToInt(map -&gt; (Integer) map.get("salary")).sum());
     * </pre>
     *
     * @param fileName parameterized SQL specifier
     * @param params the named parameters
     * @param handleStream  the function which is applied to the stream
     * @param <U> the result type
     * @return the result of {@code handleStream}
     * @throws org.springframework.dao.DataAccessException if there is any problem
     */
    public <U> U forStream(
            String fileName,
            Map<String, Object> params,
            Function<? super Stream<Map<String, Object>>, U> handleStream) {
        String sql = getTemplate(fileName, params);
        SqlParameterSource sps = paramBuilder.byMap(params);
        RowMapper<Map<String, Object>> mapper = new ColumnMapRowMapper();
        return queryStreamWithNamedParams(sql, sps, mapper, handleStream);
    }

    /**
     * Executes a query, passing the column maps to {@code handleStream} as a stream.
     *
     * <p>{@code entity} is used as the single parameter if it is a simple value,
     * or as the container of the named parameters if it is a bean.</p>
     *
     * <p>Example usage with a simple value parameter:</p>
     *
     * <pre>
     * int totalSalary = sqlTemplate.forStream("/path/to/query.sql", "SALESMAN",
     *         mapStream -&gt; mapStream.mapToInt(map -&gt; (Integer) map.get("salary")).sum());
     * </pre>
     *
     * <p>Example usage with a bean parameter:</p>
     *
     * <pre>
     * emp.setDeptno(30);
     * emp.setJob("SALESMAN");
     * int totalSalary = sqlTemplate.forStream("/path/to/query.sql", emp,
     *         mapStream -&gt; mapStream.mapToInt(map -&gt; (Integer) map.get("salary")).sum());
     * </pre>
     *
     * @param fileName parameterized SQL specifier
     * @param entity the single parameter or the container of the named parameters
     * @param handleStream  the function which is applied to the stream
     * @param <U> the result type
     * @return the result of {@code handleStream}
     * @throws org.springframework.dao.DataAccessException if there is any problem
     */
    public <U> U forStream(
            String fileName,
            Object entity,
            Function<? super Stream<Map<String, Object>>, U> handleStream) {
        String sql = getTemplate(fileName, entity);
        RowMapper<Map<String, Object>> mapper = new ColumnMapRowMapper();
        if (TypeUtils.isSimpleValueType(entity.getClass())) {
            PreparedStatementSetter pss = paramBuilder.byArgs(entity);
            return queryStreamWithOrdinalParams(sql, pss, mapper, handleStream);
        } else {
            SqlParameterSource sps = paramBuilder.byBean(entity);
            return queryStreamWithNamedParams(sql, sps, mapper, handleStream);
        }
    }

    public int update(String fileName, Object... args) {
        String sql = getTemplate(fileName, args);
        return jdbcTemplate.update(sql, paramBuilder.byArgs(args));
    }

    public int update(String fileName, Map<String, Object> params) {
        String sql = getTemplate(fileName, params);
        return namedJdbcTemplate.update(sql, paramBuilder.byMap(params));
    }

    public int update(String fileName, Object entity) {
        String sql = getTemplate(fileName, entity);

        if (TypeUtils.isSimpleValueType(entity.getClass())) {
            return jdbcTemplate.update(sql, paramBuilder.byArgs(entity));
        }

        return namedJdbcTemplate.update(sql, paramBuilder.byBean(entity));
    }

    public int[] batchUpdate(String... fileNames) {
        return jdbcTemplate.batchUpdate(fileNames);
    }

    public int[] batchUpdate(String fileName, Object[][] batchParams) {
        String sql = getTemplate(fileName, EMPTY_ARGS);
        return jdbcTemplate.batchUpdate(sql, paramBuilder.byBatchArgs(batchParams));
    }

    public int[] batchUpdate(String fileName, Map<String, Object>[] batchParams) {
        String sql = getTemplate(fileName, EMPTY_ARGS);

        MapParameter[] params = new MapParameter[batchParams.length];
        for (int i = 0; i < batchParams.length; i++) {
            params[i] = paramBuilder.byMap(batchParams[i]);
        }

        return namedJdbcTemplate.batchUpdate(sql, params);
    }

    public int[] batchUpdate(String fileName, Object[] batchParams) {
        String sql = getTemplate(fileName, EMPTY_ARGS);
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

    public MapUpdateBuilder update(String fileName) {
        return new MapUpdateBuilder(fileName);
    }

    public <T> MapQueryBuilderForBean<T> query(String fileName, Class<T> clazz) {
        return new MapQueryBuilderForBean<>(fileName, clazz);
    }

    public MapQueryBuilderForMap query(String fileName) {
        return new MapQueryBuilderForMap(fileName);
    }

    protected String getTemplate(String fileName, Object[] args) {
        try {
            return templateEngine.get(fileName, args);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    protected String getTemplate(String fileName, Object param) {
        try {
            return templateEngine.get(fileName, param);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public class MapQueryBuilderForBean<T> {
        protected Map<String, Object> params = new HashMap<>();
        protected String fileName;
        protected Class<T> clazz;

        public MapQueryBuilderForBean(String fileName, Class<T> clazz) {
            this.fileName = fileName;
            this.clazz = clazz;
        }

        public MapQueryBuilderForBean<T> add(String key, Object value) {
            params.put(key, value);
            return this;
        }

        public T forObject() {
            List<T> list = forList();
            return DataAccessUtils.singleResult(list);
        }

        public List<T> forList() {
            String sql = getTemplate(fileName, params);
            return namedJdbcTemplate.query(sql, paramBuilder.byMap(params), mapperBuilder.mapper(clazz));
        }

        /**
         * Executes a query, passing the row objects to {@code handleStream} as a stream.
         *
         * @param handleStream  the function which is applied to the stream
         * @param <U> the result type
         * @return the result of {@code handleStream}
         * @throws org.springframework.dao.DataAccessException if there is any problem
         */
        public <U> U forStream(Function<? super Stream<T>, U> handleStream) {
            String sql = getTemplate(fileName, params);
            return queryStreamWithNamedParams(sql, paramBuilder.byMap(params), mapperBuilder.mapper(clazz), handleStream);
        }
    }

    public class MapQueryBuilderForMap {
        protected Map<String, Object> params = new HashMap<>();
        protected String fileName;

        public MapQueryBuilderForMap(String fileName) {
            this.fileName = fileName;
        }

        public MapQueryBuilderForMap add(String key, Object value) {
            params.put(key, value);
            return this;
        }

        public Map<String, Object> forMap() {
            String sql = getTemplate(fileName, params);
            return namedJdbcTemplate.queryForMap(sql, paramBuilder.byMap(params));
        }

        public List<Map<String, Object>> forList() {
            String sql = getTemplate(fileName, params);
            return namedJdbcTemplate.queryForList(sql, paramBuilder.byMap(params));
        }

        /**
         * Executes a query, passing the column maps to {@code handleStream} as a stream.
         *
         * @param handleStream the function which is applied to the stream
         * @param <U> the result type
         * @return the result of {@code handleStream}
         * @throws org.springframework.dao.DataAccessException if there is any problem
         */
        public <U> U forStream(
                Function<? super Stream<Map<String, Object>>, U> handleStream) {
            String sql = getTemplate(fileName, params);
            return queryStreamWithNamedParams(sql, paramBuilder.byMap(params), new ColumnMapRowMapper(), handleStream);
        }
    }

    public class MapUpdateBuilder {
        protected Map<String, Object> params = new HashMap<>();
        protected String fileName;

        public MapUpdateBuilder(String fileName) {
            this.fileName = fileName;
        }

        public MapUpdateBuilder add(String key, Object value) {
            params.put(key, value);
            return this;
        }

        public int execute() {
            return update(fileName, params);
        }
    }

    /**
     * Executes a quey for stream with ordinal parameters.
     */
    private <T, U> U queryStreamWithOrdinalParams(
            String sql,
            PreparedStatementSetter pss,
            RowMapper<T> mapper,
            Function<? super Stream<T>, U> handleStream) {
        SQLExceptionTranslator excTranslator = jdbcTemplate.getExceptionTranslator();
        ResultSetExtractor<U> extractor
            = new StreamResultSetExtractor(sql, mapper, handleStream, excTranslator);
        return jdbcTemplate.query(sql, pss, extractor);
    }

    /**
     * Executes a query for stream with named parameters.
     */
    private <T, U> U queryStreamWithNamedParams(
            String sql,
            SqlParameterSource sps,
            RowMapper<T> mapper,
            Function<? super Stream<T>, U> handleStream) {
        SQLExceptionTranslator excTranslator = jdbcTemplate.getExceptionTranslator();
        ResultSetExtractor<U> extractor
            = new StreamResultSetExtractor(sql, mapper, handleStream, excTranslator);
        return namedJdbcTemplate.query(sql, sps, extractor);
    }

}
