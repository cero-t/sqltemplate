package ninja.cero.sqltemplate;

import ninja.cero.sqltemplate.test.MysqlTestConfig;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Runs {@link EnumMappingTestBase} against MySQL (failsafe / {@code mvn verify}).
 * Disabled automatically when Docker is not available.
 */
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(classes = MysqlTestConfig.class)
public class EnumMappingMysqlIT extends EnumMappingTestBase {
}
