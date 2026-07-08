package ninja.cero.sqltemplate.core.util;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The zone offset applied when converting an {@link OffsetDateTime} must be taken at the value's
 * own instant, not at {@code Instant.now()}. America/New_York is EDT (-04:00) in summer and EST
 * (-05:00) in winter; asserting both a summer and a winter value makes this deterministic
 * regardless of when the test runs, since {@code now()} can match at most one of the two offsets.
 */
class JdbcValueUtilsTest {
    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");

    @Test
    void convertIfNecessary_offsetDateTimeInSummer_usesOffsetAtValueInstant() {
        // 2001-07-01T12:00Z -> New York EDT (-04:00) -> local 08:00
        Timestamp result = (Timestamp) JdbcValueUtils.convertIfNecessary(
                OffsetDateTime.of(2001, 7, 1, 12, 0, 0, 0, ZoneOffset.UTC), NEW_YORK);
        assertEquals(Timestamp.valueOf(LocalDateTime.of(2001, 7, 1, 8, 0)), result);
    }

    @Test
    void convertIfNecessary_offsetDateTimeInWinter_usesOffsetAtValueInstant() {
        // 2001-01-01T12:00Z -> New York EST (-05:00) -> local 07:00
        Timestamp result = (Timestamp) JdbcValueUtils.convertIfNecessary(
                OffsetDateTime.of(2001, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC), NEW_YORK);
        assertEquals(Timestamp.valueOf(LocalDateTime.of(2001, 1, 1, 7, 0)), result);
    }
}
