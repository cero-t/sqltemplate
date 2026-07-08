package ninja.cero.sqltemplate;

import ninja.cero.sqltemplate.test.PostgresTestConfig;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Runs {@link SqlTemplateTestBase} against PostgreSQL (failsafe / {@code mvn verify}).
 * Disabled automatically when Docker is not available.
 */
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(classes = PostgresTestConfig.class)
public class SqlTemplatePostgresIT extends SqlTemplateTestBase {
}
