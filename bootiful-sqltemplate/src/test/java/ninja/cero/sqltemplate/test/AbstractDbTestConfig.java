package ninja.cero.sqltemplate.test;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Common Spring beans for the DB-integration tests. Each concrete subclass supplies the
 * {@link DataSource} for one database (H2 / MySQL / PostgreSQL); everything downstream
 * ({@code transactionManager} for {@code @Transactional} rollback, {@code JdbcTemplate},
 * {@code NamedParameterJdbcTemplate}) is shared.
 */
public abstract class AbstractDbTestConfig {
    @Bean
    DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
