package ninja.cero.sqltemplate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Reproduction for issue #14 on PostgreSQL: the positional batch sources bind every column as
 * {@code TYPE_UNKNOWN}. Non-null values are fine (the driver infers the type from the value) and H2
 * tolerates even untyped NULLs, so the H2 suite misses it; but a NULL in a type-ambiguous position
 * (the common {@code ? IS NULL} idiom) fails on PostgreSQL with "could not determine data type of
 * parameter". Runs only when Docker is available (Testcontainers); skipped otherwise.
 */
public class Issue14BatchArgsTypeReproIT {

    static PostgreSQLContainer<?> postgres;
    static JdbcTemplate jdbcTemplate;
    static NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @BeforeAll
    static void setUp() {
        assumeTrue(DockerClientFactory.instance().isDockerAvailable(), "Docker not available");

        postgres = new PostgreSQLContainer<>("postgres:15-alpine");
        postgres.start();

        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        dataSource.setDriverClassName("org.postgresql.Driver");

        jdbcTemplate = new JdbcTemplate(dataSource);
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

        jdbcTemplate.execute("create table emp (empno int primary key, mgr int)");
        jdbcTemplate.update("insert into emp (empno, mgr) values (7369, 7902)");
    }

    @AfterAll
    static void tearDown() {
        if (postgres != null) {
            postgres.stop();
        }
    }

    SqlTemplate sqlTemplate() {
        return new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate);
    }

    // The problematic parameter is the last "? IS NULL" - a nullable-filter guard.
    static final String POSITIONAL_SQL = "update emp set mgr = ? where empno = ? and ? is null";
    static final String NAMED_SQL = "update emp set mgr = :mgr where empno = :empno and :flag is null";

    // Baseline: a NON-null guard gives the driver a type, so the position itself is fine.
    @Test
    void positionalBatch_nonNullGuard_ok() {
        assertDoesNotThrow(() -> sqlTemplate().batchUpdate()
                .query(POSITIONAL_SQL)
                .addBatch(100, 7369, 0)   // guard = 0 (int) -> "0 is null" = false, no row, but no error
                .execute());
    }

    // The bug: the guard NULL is bound as TYPE_UNKNOWN, so PostgreSQL can't infer $3 in "$3 IS NULL".
    @Test
    void positionalBatch_nullGuard_failsOnPostgres() {
        Exception ex = assertThrows(Exception.class, () -> sqlTemplate().batchUpdate()
                .query(POSITIONAL_SQL)
                .addBatch(100, 7369, null)   // guard = null, bound WITHOUT a SQL type
                .execute());

        String msg = rootMessage(ex);
        assertTrue(msg.contains("could not determine data type"),
                "expected PostgreSQL type-inference failure, but was: " + msg);
    }

    // Fix direction: the bean path types the null from the declared Integer field, so PG accepts it.
    @Test
    void namedBeanBatch_sameNullGuard_ok() {
        int[] counts = sqlTemplate().batchUpdate()
                .query(NAMED_SQL)
                .addBatch(new GuardParam(100, 7369, null))
                .execute();
        assertArrayEquals(new int[]{1}, counts);   // "null is null" = true -> row updated
    }

    /** Public-field bean so BeanParameter reads the declared {@code Integer} types (incl. the null flag). */
    public static class GuardParam {
        public Integer mgr;
        public Integer empno;
        public Integer flag;

        public GuardParam(Integer mgr, Integer empno, Integer flag) {
            this.mgr = mgr;
            this.empno = empno;
            this.flag = flag;
        }
    }

    private static String rootMessage(Throwable t) {
        Throwable root = t;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        return String.valueOf(root.getMessage());
    }
}
