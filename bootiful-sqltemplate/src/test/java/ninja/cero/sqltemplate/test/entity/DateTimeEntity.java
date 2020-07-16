package ninja.cero.sqltemplate.test.entity;

import java.sql.Timestamp;
import java.time.*;

public class DateTimeEntity {
    public java.util.Date utilDate;
    public java.sql.Date sqlDate;
    public java.sql.Time sqlTime;
    public Timestamp sqlTimestamp;

    public LocalDateTime localDateTime;
    public LocalDate localDate;
    public LocalTime localTime;

    public ZonedDateTime zonedDateTime;

    public OffsetDateTime offsetDateTime;
    public OffsetTime offsetTime;
}
