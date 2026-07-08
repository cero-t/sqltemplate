package ninja.cero.sqltemplate.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;

/**
 * MySQL configuration backed by a Testcontainers singleton container (failsafe / {@code *IT}).
 * The container starts once per JVM and is torn down by Testcontainers (Ryuk) at exit.
 */
@Configuration
public class MysqlTestConfig extends AbstractDbTestConfig {
    static final MySQLContainer<?> CONTAINER = new MySQLContainer<>("mysql:8.0");

    static {
        CONTAINER.start();
    }

    @Bean
    DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                CONTAINER.getJdbcUrl(), CONTAINER.getUsername(), CONTAINER.getPassword());
        dataSource.setDriverClassName(CONTAINER.getDriverClassName());

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
                new ClassPathResource("schema.sql"), new ClassPathResource("data.sql"));
        DatabasePopulatorUtils.execute(populator, dataSource);
        return dataSource;
    }
}
