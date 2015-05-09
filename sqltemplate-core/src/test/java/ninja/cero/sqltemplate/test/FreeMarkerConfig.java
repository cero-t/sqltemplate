package ninja.cero.sqltemplate.test;

import ninja.cero.sqltemplate.core.SqlTemplate;
import ninja.cero.sqltemplate.core.template.FreeMarker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@SpringBootApplication
public class FreeMarkerConfig {
    public static void main(String[] args) {
        SpringApplication.run(FreeMarkerConfig.class, args);
    }

    @Bean
    SqlTemplate sqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate, new FreeMarker());
    }
}
