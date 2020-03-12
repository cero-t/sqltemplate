package ninja.cero.sqltemplate.core;

import ninja.cero.sqltemplate.core.mapper.MapperBuilder;
import ninja.cero.sqltemplate.core.parameter.ParamBuilder;
import ninja.cero.sqltemplate.core.template.TemplateEngine;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.time.ZoneId;

public class PlainTextSqlTemplate extends SqlTemplate {
    public PlainTextSqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate) {
        super(jdbcTemplate, namedJdbcTemplate, TemplateEngine.PLAIN_TEXT, new ParamBuilder(), new MapperBuilder());
    }

    public PlainTextSqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate, ZoneId zoneId) {
        super(jdbcTemplate, namedJdbcTemplate, TemplateEngine.PLAIN_TEXT, new ParamBuilder(zoneId), new MapperBuilder(zoneId));
    }

    public PlainTextSqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate, ParamBuilder paramBuilder, MapperBuilder mapperBuilder) {
        super(jdbcTemplate, namedJdbcTemplate, TemplateEngine.PLAIN_TEXT, paramBuilder, mapperBuilder);
    }
}
