package ninja.cero.sqltemplate.example;

import ninja.cero.sqltemplate.SqlTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@SpringBootApplication
public class SampleApplication {

    @Bean
    SqlTemplate sqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate);
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SampleApplication.class, args);
        SampleProcess sampleProcess = context.getBean(SampleProcess.class);
        sampleProcess.process();
    }
}
