Bootiful SQL Template
===========

A simple SQL template engine for Spring Boot applications.

## What is Bootiful SQL Template?
Bootiful SQL Template is a simple wrapper of `org.springframework.jdbc.core.JdbcTemplate` and `org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate` which offers following extension.

* supports SQL files (plain SQL files or FreeMarker template files)
* add API using plain Java objects such as value object, `java.util.Map` and series of simple objects
* supports public fields of entity classes besides private fields with accessor methods
* supports JSR-310 time types such as `LocalDate`, `LocalTime`, `LocalDateTime`, `ZonedDateTime` and `OffsetDateTime`.
* supports time zones

## Getting started
* Add dependency in your `pom.xml` or other build tool's configuration file.

```xml
<dependencies>
    <dependency>
        <groupId>ninja.cero.bootiful-sqltemplate</groupId>
        <artifactId>bootiful-sqltemplate-core</artifactId>
        <version>1.0.3</version>
    </dependency>
    ...
</dependencies>
```

* Create application class or configuration class of your Spring Boot application as follows.

```java
@Configuration
public class SqlTemplateConfiguration {
	@Bean
	public SqlTemplate sqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		return new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate);
	}
}
```

* Create an SQL file in the classpath. (cf. src/main/resources/sql/selectAll.sql)

```sql
select * from emp
```

* Create a data access class having SqlTemplate bean as an `@Autowired` property. Then the data access method can call the forObject / forList / update / query methods of the SqlTemplate class.

```java
@Service
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
