Bootiful SQL Template
===========

A simple SQL template engine for Spring Boot applications.

## What is Bootiful SQL Template?

Bootiful SQL Template is an O/R mapper designed to be easy to use for people who want to write SQL.
It is implemented as a wrapper around `JdbcTemplate` and `NamedParameterJdbcTemplate` provided by the Spring Framework (Spring JDBC), adding the following features.

- Fluent API
- Use of SQL template files (with FreeMarker syntax support)
- Mapping to/from objects such as POJOs, `java.util.Map` and Java `record`
- Public fields can be used in addition to accessor methods (getter/setter) of POJOs
- Time zone support (Experimental)

## Getting Started

- Add the following block to your Maven `pom.xml`.

```xml
<dependencies>
    ...
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
        <version>4.0.7</version>
    </dependency>
    <dependency>
        <groupId>ninja.cero.bootiful-sqltemplate</groupId>
        <artifactId>bootiful-sqltemplate</artifactId>
        <version>4.0.2</version>
    </dependency>
    <!-- Feel free to replace with any JDBC driver you use -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>2.3.232</version>
    </dependency>
    ...
</dependencies>
```

- Add a bean definition of `ninja.cero.sqltemplate.SqlTemplate` to the configuration class of your Spring Boot application.

```java
@Configuration
public class ApplicationConfig {
	@Bean
	public SqlTemplate sqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		return new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate);
	}
}
```

- Create an SQL file in the classpath.
    - e.g. `src/main/resources/sql/selectAll.sql`

```sql
select * from emp
```

- Add a `SqlTemplate` field to your data access class and inject the instance.
- Access the database using the methods of the `SqlTemplate` class.

```java
@Component
public class SampleProcess {
    protected SqlTemplate sqlTemplate;

    public SampleProcess(SqlTemplate sqlTemplate) {
        this.sqlTemplate = sqlTemplate;
    }

    public void search() {
        List<Emp> emps = sqlTemplate.file("sql/selectAll.sql")
                                    .forList(Emp.class);
        emps.forEach(e -> System.out.println(e.ename));
    }
}
```

## Reference Manual

### 1. Why use Bootiful SQL Template?

Bootiful SQL Template is developed for people like these.

- People who prefer to treat SQL as SQL files rather than as strings
- People who prefer writing concrete SQL rather than abstracting SQL away like JPA
- People who prefer handling simple POJOs rather than annotating entity classes like Spring Data
- People who prefer a slightly more modern API than JdbcTemplate
- People who prefer not to have source code generation or dynamic generation at compile time

If you fit these perfectly, please use Bootiful SQL Template. Conversely, if you do not, you should probably use another product.

Bootiful SQL Template is published as an OSS product. Of course you can use it as is, but it also serves as "an example implementation of a wrapper library for JdbcTemplate." Bootiful SQL Template is only about 1,000 lines of implementation, yet it still complements some of the weaknesses of JdbcTemplate, which is proof that you can build what you want even at that scale.

So, if you find it hard to use because the style or naming of the API does not match your taste, the documentation is unkind, or it is not maintained (sorry!), feel free to implement your own version that suits your taste while referring to the source code.

Please use Bootiful SQL Template casually, with that kind of mindset.

### 2. Setup

#### 2-1. Supported environments

We test with the following versions of Java and the Spring Framework.

- Java 17 or later
- Spring Framework 7.0 or later
- Spring Boot 4.0 or later
- RDBMSs verified to work
    - MySQL
    - PostgreSQL
    - H2Database

> **About older versions (Java 8 / Spring Boot 2.x and 3.x)**
>
> `4.0.0` and later use Java 17, Spring Framework 7.0 and Spring Boot 4.0 as the baseline. If you want to use it with Java 8 or Spring Boot 2.x / 3.x, please refer to the `2.x` branch and version `2.1.x`.

#### 2-2. Adding the dependency

Add `ninja.cero.bootiful-sqltemplate:bootiful-sqltemplate` to the dependencies of your build. When using it with Spring Boot, also add `org.springframework.boot:spring-boot-starter-jdbc`. Add the JDBC driver you actually use.

Maven `pom.xml`
```xml
<dependencies>
    ...
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
        <version>4.0.7</version>
    </dependency>
    <dependency>
        <groupId>ninja.cero.bootiful-sqltemplate</groupId>
        <artifactId>bootiful-sqltemplate</artifactId>
        <version>4.0.2</version>
    </dependency>
    <!-- Feel free to replace with any JDBC driver you use -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>2.3.232</version>
    </dependency>
    ...
</dependencies>
```

Gradle `build.gradle`
```
dependencies {
    ...
    implementation 'org.springframework.boot:spring-boot-starter-jdbc'
    implementation 'ninja.cero.bootiful-sqltemplate:bootiful-sqltemplate:4.0.2'
    implementation 'com.h2database:h2:2.3.232'
    ...
}
```

Now you can use the classes of Bootiful SQL Template.

#### 2-3. Defining the bean

Add `ninja.cero.sqltemplate.SqlTemplate` as a Spring bean definition. Pass the two Spring JDBC objects `org.springframework.jdbc.core.JdbcTemplate` and `org.springframework.jdbc.core.NamedParameterJdbcTemplate` as constructor arguments. For the other constructors, see [6. Other features].

```java
@Configuration
public class ApplicationConfig {
	@Bean
	public SqlTemplate sqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		return new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate);
	}
}
```

Now `SqlTemplate` is managed by the Spring container and ready to be injected.

### 3. Querying - issuing SELECT statements

This section explains the operations for issuing SELECT statements using Bootiful SQL Template.

#### 3-1. Example

```java
// (1)
@Component
public class SampleProcess {
    // (2)
    private SqlTemplate sqlTemplate;

    // (3)
    public SampleProcess(SqlTemplate sqlTemplate) {
        this.sqlTemplate = sqlTemplate;
    }

    public void search() {
        // (4)
        List<Emp> emps = sqlTemplate.file("sql/selectByParam.sql")
                                    .addParam("deptno", 30)
                                    .addParam("job", "SALESMAN")
                                    .forList(Emp.class);
        emps.forEach(e -> System.out.println(e.ename));
    }
}
```

(1) (2) Declare `SqlTemplate` as an instance variable of a class annotated with Spring's `@Component`, so that an instance of the `SqlTemplate` class can be injected.

(3) Receive the `SqlTemplate` instance using constructor injection. You may use the `@Autowired` annotation instead of constructor injection.

(4) Perform the SELECT using the `SqlTemplate` API and receive the result as a `java.util.List`.

The details of the API are explained in the following subsections.

#### 3-2. Using an SQL file

Among the methods that can be called first on the `SqlTemplate` class, `file` and `query` are used to issue SELECT statements. Here we explain the `file` method, which uses an SQL file.

An SQL file is a text file containing an SQL query. Binding variables and issuing dynamic queries using a template engine are also possible, but those are described later. Here we assume a file containing a simple query like the following.

```sql
select
    *
from
    emp
```

Place the SQL file somewhere on the classpath. For a typical Maven-style project, placing it under `src/main/resources` works well. Alternatively, you may place it under `src/main/java` in the same package as the class that accesses the database. There is no particular restriction on the file extension of the SQL file.

- (Reference) Examples of where to place the file
    1. `src/main/resources/sql/selectAll.sql`
    2. `src/main/resources/sql/SomeProcess/selectAll.sql`
    3. `src/main/resources/sql/ninja/cero/sqltemplate/example/SomeProcess/selectAll.sql`
    4. `src/main/java/sql/ninja/cero/sqltemplate/example/SomeProcess/selectAll.sql`

Then specify the path of the SQL file as the argument of the `file` method. The path is written relative to the root of the classpath. When the SQL file is placed at the locations shown above, the arguments of the `file` method are as follows.

- (Reference) Example arguments corresponding to the above
    1. `file("sql/selectAll.sql")`
    2. `file("sql/SomeProcess/selectAll.sql")`
    3. `file("ninja/cero/sqltemplate/example/SomeProcess/selectAll.sql")`
    4. `file("ninja/cero/sqltemplate/example/SomeProcess/selectAll.sql")`

#### 3-3. Specifying SQL as a string

This section explains the `query` method, one of the methods that can be called first on the `SqlTemplate` class. It executes SQL specified as a string.

For example, you can write the following.

```java
List<Emp> emps = sqlTemplate.query("select * from emp")
                            .forList(Emp.class);
```

The `query` method is intended for issuing simple queries that are not worth creating an SQL file for, or for issuing queries assembled dynamically in Java code.

#### 3-4. Specifying search conditions

Both the `file` and `query` methods let you write variables in the query and bind arbitrary values. There are two main ways to write variables and bind them.

##### (1) Using ? for bind variables

One way is to use `?` for bind variables. For example, write a query like the following.

```sql
select
    *
from
    emp
inner join dept
    on emp.deptno = dept.deptno
where
    dept.deptno = ?
    and emp.job = ?
```

In this example two `?` are used as variables.

To bind values to a query written this way, use the `params` method. Pass as many arbitrary values as there are `?` to the `params` method.

```java
List<Emp> emps = sqlTemplate.file("sql/selectByParams.sql")
                            .params(30, "SALESMAN")
                            .forList(Emp.class);
```

With this, the first argument of the `params` method is passed to the first `?`, and the second argument to the second `?`. There is no upper limit on the number of variables.

> **Note on `NULL`:** the SQL type of each `?` value is inferred from the runtime object, but a `NULL` carries no type. On strict databases (e.g. PostgreSQL) a `NULL` in a position where the type cannot be inferred from context — inside a function call or `? IS NULL`, for example — fails with *"could not determine data type of parameter"*. In that case, use a named parameter (`:name`) with a Value Object, whose declared field type supplies the SQL type.

As a result, the query above is bound with values as follows.

```sql
select
    *
from
    emp
inner join dept
    on emp.deptno = dept.deptno
where
    dept.deptno = 30
    and emp.job = 'SALESMAN'
```

Since the query uses a `PreparedStatement`, there is no risk of SQL injection.

Bind variables using `?` are intended for cases with a small number of variables. As the number of variables grows, it becomes harder to tell which value goes to which position, so it is better to use the `:(name)` method described next.

##### (2) Using :(name) for bind variables - listing bind values

The other way is to use `:(name)` for bind variables. For example, write a query like the following.

```sql
select
    *
from
    emp
inner join dept
    on emp.deptno = dept.deptno
where
    dept.deptno = :deptno
    and emp.job = :job
```

In this example two variables, `:deptno` and `:job`, are used.

There are three ways to bind values to a query written this way. One is to specify the bind variable name and value individually, the second is to use a Value Object (POJO) that has fields with the same names as the variables, and the third is to use a `java.util.Map` keyed by the variable names.

For the first approach of specifying the bind variable name and value, use the `addParam` method with the variable name as the first argument and the value to bind as the second argument. You can specify multiple variables by chaining the `addParam` method. The code looks like the following.

```java
List<Emp> emps = sqlTemplate.file("sql/selectByParam.sql")
                            .addParam("deptno", 30)
                            .addParam("job", "SALESMAN")
                            .forList(Emp.class);
```

This binds a value to each variable. The result is equivalent to specifying variables with `?`.

This approach is intended for cases where there are a fair number of bind variables and you want to enumerate them clearly.

##### (3) Using :(name) for bind variables - specifying values with a Value Object

When using a Value Object to specify bind values, first create a Value Object with field names that match the variable names. The Value Object may use accessor methods (getter/setter) or public fields. Here we use public fields as an example because they require fewer lines.

```java
public class SearchCondition {
    public Integer deptno;
    public String job;
}
```

Next, create an instance of this Value Object, assign the values you want to bind, and pass it as the argument of the `param` method.

```java
SearchCondition searchCondition = new SearchCondition();
searchCondition.deptno = 30;
searchCondition.job = "SALESMAN";

List<Emp> emps = sqlTemplate.file("sql/selectByParam.sql")
                            .param(searchCondition)
                            .forList(Emp.class);
```

With this, the field values of the `SearchCondition` instance are bound to the `:(name)` variables in the query. The result is equivalent to specifying variables with `?`.

This approach is intended for cases such as receiving search conditions as a Value Object from outside (a Web API or UI) and executing a query using those values.

##### (4) Using :(name) for bind variables - specifying values with a Map

When using a `java.util.Map` to specify bind values, create a `Map` instance and use the `put` method with the variable name to bind as the first argument and the value as the second. Then pass that `Map` instance as the argument of the `param` method.

```java
Map<String, Object> condition = new HashMap<>();
condition.put("deptno", 30);
condition.put("job", "SALESMAN");

List<Emp> emps = sqlTemplate.file("sql/selectByParam.sql")
                            .param(condition)
                            .forList(Emp.class);
```

With this, the values of the `Map` keys are bound to the `:(key)` variables in the query. The result is equivalent to specifying variables with `?`.

This approach is intended for cases such as executing a query with an already-created `Map` instance.

#### 3-5. Retrieving results - when there are multiple rows

After specifying the query and the parameters, execute the query and obtain the results. The method used to execute the query varies depending on how you want to receive the results. When obtaining multiple rows, you can receive them as a `java.util.List` or a `java.util.stream.Stream`. This section explains how.

##### (1) Receiving as List<Value Object>

To receive multiple results as a `java.util.List`, use the `forList` method. You can specify the `class` of the Value Object as the argument of the `forList` method; in that case the return value is `List<Value Object>`. If you do not specify an argument, the return value is `List<Map<String, Object>>`.

To obtain the results as a `List` of Value Objects, first create a Value Object with field names that match the column names of the query result. The Value Object may use accessor methods (getter/setter) or public fields. Note that even if the column name is in snake case (e.g. `user_name`), the field name may be either camel case (e.g. `userName`) or snake case.

Here we use camel-case public fields as an example.

```java
public class Emp {
    public Integer empno;
    public String ename;
    public String job;
    public Integer mgr;
    public LocalDate hiredate;
    public BigDecimal sal;
    public BigDecimal comm;
    public Integer deptno;
}
```

Next, pass the `class` of this Value Object to the `forList` method.

```java
List<Emp> emps = sqlTemplate.query("select * from emp")
                            .forList(Emp.class);
```

Now you can receive the results as `List<Emp>`.

When handling multiple results, you will basically use this approach.

##### (2) Receiving as List<Map>

To receive the results as `List<Map<String, Object>>`, use the `forList` method without an argument. For example, write the following.

```java
List<Map<String, Object>> emps = sqlTemplate.query("select * from emp")
                            .forList();
```

The returned `Map<String, Object>` holds values keyed by column name. Note that if a column name is in snake case, the `Map` key remains in snake case. Also, if the RDBMS returns column names in all uppercase, the `Map` keys are also all uppercase. Keep these two points in mind when using this method.

You can use this approach when you do not want to bother creating a Value Object to receive the results, but it is not particularly recommended.

##### (3) Processing as a Stream

To process the results as a `java.util.stream.Stream`, use the `forStream` method. There are two ways to use `forStream`.

**(3-a) Passing a `Function` (recommended)**

If you pass a `java.util.function.Function` as an argument, the `Stream` is handed to that function for processing and you receive only the result. The `Stream` is closed automatically by the library, so there is no risk of a resource leak from forgetting to close it. Normally you should use this form.

You can specify the `class` of a Value Object as an argument of the `forStream` method; in that case the second argument `Function` processes a `Stream<Value Object>` to perform conversions and so on. If you do not specify the first-argument type, you pass only a `Function` that processes a `Stream<Map<String, Object>>`.

Here is an example of code that handles a Stream of Value Objects.

```java
Function<Stream<Emp>, Long> summing = stream -> stream.mapToLong(e -> e.sal.longValue()).sum();
Long sum = sqlTemplate.query("select * from emp")
        .forStream(Emp.class, summing);
```

In this example, Stream processing is used to compute the total of the `sal` values of `Emp`.

**(3-b) Receiving a `Stream` directly**

If you call `forStream` without a `Function`, you receive the `Stream` itself as the return value. This is useful when you want to return the results directly to the caller (for example, returning `Stream<Emp>` from a controller method). Specifying the `class` of a Value Object as the first argument returns a `Stream<Value Object>`; omitting it returns a `Stream<Map<String, Object>>`.

**A `Stream` obtained this way must always be closed by the caller.** If you do not close it, the underlying JDBC resources (such as the `Connection`) are not released and will leak. Using a try-with-resources block, as shown below, is the safest approach.

```java
try (Stream<Emp> stream = sqlTemplate.query("select * from emp")
        .forStream(Emp.class)) {
    long sum = stream.mapToLong(e -> e.sal.longValue()).sum();
}
```

Note that this `Stream` does not read the entire result into memory at once; like `JdbcTemplate#queryForStream`, it reads rows lazily one at a time. With Spring Boot, you can set the fetch size via the `spring.jdbc.template.fetch-size` property, which is applied to the auto-configured `JdbcTemplate` and the `NamedParameterJdbcTemplate` derived from it (both of which `SqlTemplate` uses). However, whether the database actually fetches rows incrementally depends on the JDBC driver: many drivers buffer the entire result set on the client side by default, so you typically need to run the query within a transaction and may need driver-specific settings. Refer to your driver's documentation for details.

#### 3-6. Retrieving results - when there are zero or one rows

Query results are expected to be zero or one row especially when searching by primary key or executing aggregate functions. In this case you can retrieve the result as a Value Object, a `java.util.Map`, or a `java.util.Optional`. This section explains how.

##### (1) Receiving as a Value Object

To receive a single result as a Value Object, use the `forObject` method. Specify the `class` of the Value Object as the argument of the `forObject` method. The rules for creating the Value Object are the same as for retrieving multiple rows, so they are omitted here.

Pass the `class` of the Value Object you want to obtain to the `forObject` method.

```java
Emp emp = sqlTemplate.query("select * from emp where empno = ?")
        .params(7369)
        .forObject(Emp.class);
```

Now you can receive the result as `Emp`. If there are zero results, the return value is null. If the query returns two or more rows, an `IncorrectResultSizeDataAccessException` is thrown, so use `forObject` only where at most one row is expected.

##### (2) Receiving as Map<String, Object>

To receive the result as `Map<String, Object>`, use the `forMap` method. For example, write the following.

```java
Map<String, Object> emp = sqlTemplate.query("select * from emp where empno = ?")
        .params(7369)
        .forMap();
```

The returned `Map<String, Object>` holds values keyed by column name. Note that if a column name is in snake case, the `Map` key remains in snake case. Also, if the RDBMS returns column names in all uppercase, the `Map` keys are also all uppercase. Keep these two points in mind when using this method.

You can use this approach when you do not want to bother creating a Value Object to receive the result. If there are zero results, the return value is null. As with `forObject`, two or more rows cause an `IncorrectResultSizeDataAccessException`.

##### (3) Receiving as Optional

To process the result as a `java.util.Optional`, use the `forOptional` method. You can specify the `class` of a Value Object as the argument of the `forOptional` method; in that case the return value is `Optional<Value Object>`. If you do not specify an argument, the return value is `Optional<Map<String, Object>>`.

Here is an example that handles a Value Object.

```java
Optional<Emp> emp = sqlTemplate.query("select * from emp where empno = ?")
        .params(7369)
        .forOptional(Emp.class);
emp.ifPresent(e -> System.out.println(e.ename)); // Do not show
```

By receiving the result as an `Optional`, when there are zero results you get an empty `Optional` instead of null, so you can write null-safe code.

#### 3-7. Using the template engine

The `file` method, which searches using an SQL file, can contain not only simple SQL but also a FreeMarker-format template. Using a template, you can assemble part of the query dynamically.

For the template syntax, refer to the official FreeMarker site (https://freemarker.apache.org/).

As an example, this section explains a query that searches using only the values specified by the parameters. First, create a FreeMarker-format template file like the following. Suppose the file is `src/main/resources/sql/selectByArbitraryParam.sql`.

```sql
select
    *
from
    emp
where
    1 = 1
<#if job??>
    AND job = :job
</#if>
<#if mgr??>
    AND mgr = :mgr
</#if>
<#if deptno??>
    AND deptno = :deptno
</#if>
```

Here, using FreeMarker's `<#if ??>` syntax, the string inside the tags (the search condition) is added only when the value is specified.

Parameters passed to FreeMarker can be given in the same form as the `:(name)` bind variables. That is, the values specified with the `addParam` method, or the Value Object or `Map` values specified with the `param` method, are passed. For example, suppose you pass parameters as follows.

```java
List<Emp> emps1 = sqlTemplate.file("sql/selectByArbitraryParam.sql")
        .addParam("deptno", 30)
        .forList(Emp.class);
```

As a result of passing only `deptno`, the template shown above is interpreted as follows.

```sql
select
    *
from
    emp
where
    1 = 1
    AND deptno = :deptno
```

Because `deptno` was specified, the `AND deptno = :deptno` part remains, while the parts for the `mgr` and `job` conditions are removed.

By using FreeMarker syntax in the template this way, you can flexibly vary the search conditions. The template syntax is powerful, and theoretically you can issue any query; for example, you can specify things that cannot be specified with `:(name)` bind variables, such as table names. However, this also means you can accidentally introduce vulnerabilities such as SQL injection. Please use it with care.

### 4. Updating - issuing INSERT / UPDATE / DELETE statements

This section explains the operations for issuing INSERT / UPDATE / DELETE statements using Bootiful SQL Template. The basic usage is almost the same as for SELECT statements: you can use the same ways of specifying the query (`file` / `query`) and the bind variables (`params` / `param` / `addParam`). The difference from SELECT is that you call the `update` method to get the result.

#### 4-1. Example

```java
@Component
public class SampleProcess {
    private SqlTemplate sqlTemplate;

    public SampleProcess(SqlTemplate sqlTemplate) {
        this.sqlTemplate = sqlTemplate;
    }

    public void update() {
        // (1)
        int count = sqlTemplate.file("sql/updateByParam.sql")
                               .addParam("job", "ANALYST")
                               .addParam("mgr", 7566)
                               .addParam("empno", 7876)
                               .update();
        System.out.println(count + " rows updated");
    }
}
```

(1) Perform the UPDATE using the `SqlTemplate` API and receive the number of updated rows as an `int`. The difference from SELECT is that after specifying the query with `file` or `query` and, if necessary, the bind variables, you finally call the `update` method.

#### 4-2. Using an SQL file / specifying SQL as a string

The way to specify the query is the same as for SELECT statements. Use the `file` method for an SQL file, or the `query` method to specify SQL directly as a string. You can issue INSERT, UPDATE or DELETE statements.

For example, an SQL file containing an INSERT statement (`sql/insertByParam.sql`) looks like the following.

```sql
INSERT
    INTO
        emp
    VALUES
        (:empno, :ename, :job, :mgr, :hiredate, :sal, :comm, :deptno)
```

An UPDATE statement (`sql/updateByParam.sql`) looks like the following.

```sql
UPDATE
        emp
    SET
        job = :job
        ,mgr = :mgr
    WHERE
        empno = :empno
```

A DELETE statement (`sql/deleteByArg.sql`) looks like the following.

```sql
DELETE
    FROM
        emp
    WHERE
        empno = ?
```

As with SELECT statements, the SQL file specified with the `file` method can also use a FreeMarker-format template (see [3-7. Using the template engine]).

#### 4-3. Specifying bind variables

The way to specify bind variables is also the same as for SELECT statements; the following four approaches are available. For details, see [3-4. Specifying search conditions].

- List values with the `params` method using `?`
- Specify values individually with the `addParam` method using `:(name)`
- Pass a Value Object (POJO) to the `param` method using `:(name)`
- Pass a `java.util.Map` to the `param` method using `:(name)`

Here is an example using a Value Object. Prepare a Value Object with field names matching the `:(name)` variables in the query, assign values, and pass it to the `param` method.

```java
Emp emp = new Emp();
emp.empno = 1000;
emp.ename = "TEST";
emp.job = "MANAGER";
emp.mgr = 7839;
emp.hiredate = LocalDate.of(2015, 4, 1);
emp.sal = new BigDecimal(4000);
emp.comm = new BigDecimal(400);
emp.deptno = 10;

int count = sqlTemplate.file("sql/insertByParam.sql")
                       .param(emp)
                       .update();
```

You can also use a Java `record` as the Value Object.

```java
public record EmpRecord(Integer empno, String ename, String job, Integer mgr,
                        LocalDate hiredate, BigDecimal sal, BigDecimal comm, Integer deptno) {
}
```

```java
EmpRecord emp = new EmpRecord(1000, "TEST", "MANAGER", 7839,
        LocalDate.of(2015, 4, 1), new BigDecimal(4000), new BigDecimal(400), 10);

int count = sqlTemplate.file("sql/insertByParam.sql")
                       .param(emp)
                       .update();
```

When using `?`, list the values with the `params` method.

```java
int count = sqlTemplate.file("sql/deleteByArg.sql")
                       .params(7566)
                       .update();
```

#### 4-4. Retrieving the result

For INSERT / UPDATE / DELETE statements, call the `update` method at the end to execute the query. The return value of the `update` method is the number of rows affected by the query (`int`).

```java
int count = sqlTemplate.file("sql/deleteByArgs.sql")
                       .params(30, "SALESMAN")
                       .update();
// If 4 rows with deptno = 30 and job = 'SALESMAN' are deleted, count becomes 4
```

For a query with no bind variables, you can omit `params` and `param` and call `update` directly after `file` or `query`.

```java
int count = sqlTemplate.query("delete from emp").update();
```

### 5. Batch updates

To issue multiple update operations together (a batch update), use the `batchUpdate` method. Batch updates fall broadly into two categories: executing multiple different SQL statements together, and applying multiple sets of parameters to a single SQL statement. In either case, the return value is an `int[]` holding the update count of each operation.

> **Note:** Unlike the `file` method used for SELECT and single updates, the `file` method under `batchUpdate` loads the SQL file as-is. FreeMarker template syntax is **not** processed.

#### 5-1. Executing multiple different SQL statements together

To execute multiple SQL statements with different contents together, call the `queries` method after `batchUpdate` and list the SQL statements you want to execute.

```java
int[] counts = sqlTemplate.batchUpdate()
        .queries("delete from emp",
                 "insert into emp (empno) values (1234)");
```

This approach cannot use bind variables. The listed SQL statements are executed in order as they are, and the update count of each SQL is returned as an `int[]`.

#### 5-2. Applying multiple parameters to a single SQL statement

To apply different parameters repeatedly to the same SQL statement, specify the query with `file` (an SQL file) or `query` (an SQL string) after `batchUpdate`, add sets of parameters with the `addBatch` method, and finally execute with the `execute` method.

The `addBatch` method has multiple ways of specifying parameters, similar to the bind variables of SELECT statements.

##### (1) Using ?

For a query using `?`, list the values as arguments of the `addBatch` method.

```java
int[] counts = sqlTemplate.batchUpdate()
        .query("insert into emp (empno) values (?)")
        .addBatch(111)
        .addBatch(222)
        .execute();
```

> **Note on `NULL`:** with `?` (positional) batch parameters the SQL type of each value is inferred from the runtime object, but a `NULL` carries no type. On strict databases (e.g. PostgreSQL) a `NULL` in a position where the type cannot be inferred from context — inside a function call or `? IS NULL`, for example — fails with *"could not determine data type of parameter"*. If a batched column can be `NULL`, prefer the Value Object form (2) below, which supplies the SQL type from the declared field type.

##### (2) Using :(name) - Value Object

For a query using `:(name)`, you can pass a Value Object with assigned values to the `addBatch` method.

```java
int[] counts = sqlTemplate.batchUpdate().file("sql/insertByParam.sql")
        .addBatch(emp1)
        .addBatch(emp2)
        .execute();
```

##### (3) Using :(name) - Map

You can also pass a `java.util.Map` keyed by variable name to the `addBatch` method.

```java
int[] counts = sqlTemplate.batchUpdate().file("sql/updateByParam.sql")
        .addBatch(arg1)  // Map<String, Object>
        .addBatch(arg2)
        .execute();
```

##### (4) Passing a List at once

Instead of adding Value Objects or `Map`s one by one with `addBatch`, you can specify them all at once by passing a `java.util.List` to the `addBatches` method. The elements of the `List` may be Value Objects or `Map`s.

```java
List<Emp> emps = Arrays.asList(emp1, emp2);

int[] counts = sqlTemplate.batchUpdate().file("sql/insertByParam.sql")
        .addBatches(emps)
        .execute();
```

In all cases, the returned `int[]` holds the update count for each set of parameters added, in the order they were added.

### 6. Other features

Besides the two-argument constructor explained so far, `SqlTemplate` provides several constructors. This section explains the features that use them.

#### 6-1. Handling time zones (Experimental)

> This feature is Experimental. Its behavior may change in the future.

By passing a `java.time.ZoneId` to the `SqlTemplate` constructor, you can perform time-zone-aware date/time conversion.

```java
@Bean
public SqlTemplate sqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    return new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate, ZoneId.of("GMT"));
}
```

The `ZoneId` specified here is used for conversion when writing to and reading from the database for zone-aware JSR-310 types (`OffsetDateTime` / `ZonedDateTime` / `OffsetTime`). If you do not specify a `ZoneId`, the system default time zone (`ZoneId.systemDefault()`) is used.

For example, consider storing a `ZonedDateTime` of `2001-01-28T12:35:02.789+09:00[Asia/Tokyo]` in an environment whose system default time zone is `Asia/Tokyo`. If you use a `SqlTemplate` configured with `ZoneId.of("GMT")`, this value is converted to GMT and stored, and the read-back result is `2001-01-28T03:35:02.789Z[GMT]` (the same instant expressed in GMT).

Note that `java.time.Instant` is also supported, but because it represents an absolute point on the timeline (UTC), it is converted directly through the epoch (`Timestamp#from` / `Timestamp#toInstant`) and is **not** affected by the configured `ZoneId`. In the same environment as above, an `Instant` is stored and read back as the same instant whether the `SqlTemplate` uses `Asia/Tokyo` or `GMT`.

#### 6-2. Mapping enum values

`enum` types are supported for both bind variables and query results. An enum is mapped to and from a string column (the `CHAR` / `VARCHAR` family) by its name (`Enum#name()`), independently of the JDBC driver's own enum handling.

Declare an enum and use it as a field (or record component) type — for example, giving the `job` column of the `Emp` shown earlier an enum type:

```java
public enum Job {
    ANALYST, CLERK, MANAGER, PRESIDENT, SALESMAN
}
```

When an enum is passed as a bind variable it is stored as its `name()`; when a string column is read into an enum field it is converted back to the matching constant. This applies to every parameter style (`params` / `param` / `addParam`, Value Object, `Map`, and batch) and every result type (record / public field / accessor / single column).

```java
// Write: Job.SALESMAN is bound as the string "SALESMAN"
List<Emp> emps = sqlTemplate.file("sql/selectByParam.sql")
                            .addParam("deptno", 30)
                            .addParam("job", Job.SALESMAN)
                            .forList(Emp.class);

// Read: the "SALESMAN" column value is mapped back to Job.SALESMAN
Job job = sqlTemplate.query("select job from emp where empno = ?")
                     .params(7499)
                     .forObject(Job.class);
System.out.println(job); // SALESMAN
```

If the stored string does not match any constant, an `IllegalArgumentException` ("No enum constant ...") is thrown. Only string columns are supported (matched by the enum's `name()`); mapping to a numeric column by `ordinal()` is not.

#### 6-3. Customizing parameter binding and mapping

By passing a `ParamBuilder` and a `MapperBuilder` to the `SqlTemplate` constructor, you can customize the conversion of bind variable values and the mapping of query results to objects.

```java
@Bean
public SqlTemplate sqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    return new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate, new ParamBuilder(), new MapperBuilder());
}
```

`ParamBuilder` is responsible for converting the parameters to bind (the values specified with `params` / `param` / `addParam`) into the form handled by Spring JDBC. `MapperBuilder` is responsible for generating the `RowMapper` that maps query results to the Value Object specified with `forObject` / `forList` and so on. If you want to perform conversion or mapping that differs from the default, you can pass your own implementations that extend these classes.

Note that the constructor that takes a `ZoneId`, explained in [6-1. Handling time zones], internally generates a `ParamBuilder` and a `MapperBuilder` that hold the specified `ZoneId`.
