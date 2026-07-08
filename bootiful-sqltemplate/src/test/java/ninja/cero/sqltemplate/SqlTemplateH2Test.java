package ninja.cero.sqltemplate;

import ninja.cero.sqltemplate.test.H2TestConfig;
import org.springframework.test.context.ContextConfiguration;

/**
 * Runs {@link SqlTemplateTestBase} against H2 (surefire / {@code mvn test}, no Docker).
 */
@ContextConfiguration(classes = H2TestConfig.class)
public class SqlTemplateH2Test extends SqlTemplateTestBase {
}
