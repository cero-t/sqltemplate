package springboot.sqltemplate.example;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import springboot.sqltemplate.core.mapper.BeanMapper;
import springboot.sqltemplate.core.parameter.ArgsParameter;
import springboot.sqltemplate.core.parameter.BeanParameter;
import springboot.sqltemplate.core.parameter.MapParameter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SqlTemplate {
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected NamedParameterJdbcTemplate namedJdbcTemplate;

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

    public <T> List<T> forList(String fileName, Class<T> clazz, Object... args) {
        String sql = get(fileName, args);
        return jdbcTemplate.query(sql, ArgsParameter.of(args), BeanMapper.of(clazz));
    }

    public <T> List<T> forList(String fileName, Class<T> clazz, Map<String, Object> params) {
        String sql = get(fileName, params);
        return namedJdbcTemplate.query(sql, MapParameter.of(params), BeanMapper.of(clazz));
    }

    public <T> List<T> forList(String fileName, Class<T> clazz, Object entity) {
        String sql = get(fileName, entity);

        if (BeanUtils.isSimpleValueType(entity.getClass())) {
            return jdbcTemplate.query(sql, ArgsParameter.of(entity), BeanMapper.of(clazz));
        }

        return namedJdbcTemplate.query(sql, BeanParameter.of(entity), BeanMapper.of(clazz));
    }

    public int update(String fileName, Map<String, Object> params) {
        String sql = get(fileName, params);
        return namedJdbcTemplate.update(sql, MapParameter.of(params));
    }

    public int update(String fileName, Object entity) {
        String sql = get(fileName, entity);

        if (BeanUtils.isSimpleValueType(entity.getClass())) {
            return jdbcTemplate.update(sql, ArgsParameter.of(entity));
        }

        return namedJdbcTemplate.update(sql, BeanParameter.of(entity));
    }

    public int update(String fileName, Object... args) {
        String sql = get(fileName, args);
        return jdbcTemplate.update(sql, ArgsParameter.of(args));
    }

    protected String get(String fileName, Object... args) {
        try {
            return Files.readAllLines(Paths.get(getClass().getResource("/" + fileName).getFile())).stream()
                    .collect(Collectors.joining("\n"));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
