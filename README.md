Bootiful SQL Template
===========

A simple SQL template engine for Spring Boot Application.

## What is Bootifule SQL Template?
Bootiful SQL Template is a simple wrapper of `org.springframework.jdbc.core.JdbcTemplate` and `org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate` which offers following extension.

* supports SQL file (plain SQL file or FreeMarker template file)
* supports simple arguments such as, value object, `java.util.Map`, and series of simple objects
* supports public fields of entity classes besides private fields with accessor methods
* supports JSR-310 time types such as `LocalDate`, `LocalTime`, `LocalDateTime`, `ZonedDateTime` and `OffsetDateTime`.

## Getting started
* Create application class or configuration class of your spring boot applications as follows.

```
@Configuration
public class SqlTemplateConfiguration {
	@Bean
	SqlTemplate sqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		return new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate);
	}
}
```

* Create SQL file in the classpath folder. (cf. src/main/resources/sql/selectAll.sql)

```
select * from emp
```

* Create data access class having SqlTemplate component as `@autowired` property. Then data access method can call forObject / forList / update / query method of SqlTemplate class.

```
public class SampleProcess {
    @Autowired
    protected SqlTemplate template;

    public void process() {
        List<Emp> emps = template.forList("sql/selectAll.sql", Emp.class);
        emps.forEach(e -> System.out.println(e.ename)); // SMITH ... MILLER
    }
}
```
## Functions

T.B.D.
