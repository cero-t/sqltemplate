Bootiful SQL Template
===========

A simple SQL template engine for Spring Boot applications.

## Bootiful SQL Templateとは何か？

Bootiful SQL Templateは、SQLを書きたい人にとって使いやすいことを目指したO/Rマッパーです。  
Spring Framework（Spring JDBC）が提供する `JdbcTemplate` と `NamedParameterJdbcTemplate` のラッパーとして実装しており、次の機能を追加しています。

- Fluent API
- SQLテンプレートファイルの利用 (FreeMarkerの書式をサポート)
- POJOや `java.util.Map` などのオブジェクトに対するマッピング
- POJOのアクセサメソッド（getter/setter）に加えてpublicフィールドが利用可能
- JSR-310 Date and Time APIの `LocalDateTime` や `ZonedDateTime` `OffsetDateTime` などを利用可能
- タイムゾーンをサポート

## Getting Started

- Mavenの `pom.xml` に次のブロックを追加します。

```xml
<dependencies>
    ...
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
        <version>2.0.0.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>ninja.cero.bootiful-sqltemplate</groupId>
        <artifactId>bootiful-sqltemplate</artifactId>
        <version>2.0.0</version>
    </dependency>
    <!-- 任意のJDBCドライバと差し替えて構いません -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>1.4.200</version>
    </dependency>
    ...
</dependencies>
```

- Spring BootアプリケーションのConfigurationクラスに `ninja.cero.sqltempalte.SqlTemplate` のBean定義を追加します。

```java
@Configuration
public class ApplicationConfig {
	@Bean
	public SqlTemplate sqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		return new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate);
	}
}
```

- SQLファイルをクラスパス内に作成します
    - 例) `src/main/resources/sql/selectAll.sql`

```sql
select * from emp
```

- データアクセスを行うクラスに `SqlTemplate` のフィールドを追加し、インスタンスをインジェクションします。
- `SqlTemplate` クラスのメソッドを利用して、データアクセスを行います。

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

## リファレンスマニュアル

### 1. なぜBootiful SQL Templateを使うのか？

Bootiful SQL Templateは、こんな人のために開発しています。

- SQLをStringで扱うよりも、SQLファイルとして扱うほうが好きな人
- JPAのようにSQLを抽象化するよりも、具体的なSQLを書くほうが好きな人
- Spring DataのようにEntityクラスにアノテーションをつけるより、シンプルなPOJOを扱うほうが好きな人
- JdbcTemplateよりも、もう少しモダンなAPIのほうが好きな人
- ソースコードの自動生成や、コンパイル時の動的生成などはない方が好きな人

これらにぴったり当てはまる人には、ぜひBootiful SQL Templateを使って欲しいですし、逆に、当てはまらないのであれば別のプロダクトを使うべきだと思います。

このBootiful SQL TemplateはOSSプロダクトとして公開していますが、そのまま使えるのはもちろんのこと、その一方で「JdbcTemplateのラッパーライブラリの実装例」という側面もあります。Bootiful SQL Templateは1000行くらいの実装規模しかありませんが、それでもJdbcTemplateの弱点をいくつか補うような実装をしており、それくらいの規模でも、十分に欲しいものが作れるという証左となっています。

そのため、もしBootiful SQL Templateの提供するAPIのスタイルや名前などが好みに合わないとか、ドキュメントが不親切だとか、メンテナンスされていないとか（ごめんなさい！）、そういう理由で使いづらいと感じた場合には、ソースコードを参考にしながら自分の好みに合うよう実装していただければと思っています。

それくらいの気持ちで、気軽にBootiful SQL Templateを使ってみてください。

### 2. セットアップ

#### 2-1. サポートする環境

次のバージョンのJava、Spring Frameworkでのテストを行っています。

- Java 1.8.0 以降
- Spring Framework 5.0.0 以降
- Spring Boot 2.0 以降
- 動作確認済みのRDBMS
    - MySQL
    - PostgreSQL
    - H2Database

#### 2-2. 依存モジュールの追加

ビルド環境のdependenciesに `ninja.cero.bootiful-sqltemplate:bootiful-sqltemplate` を追加します。また、Spring Bootで利用する場合は `org.springframework.boot:spring-boot-starter-jdbc` を追加します。JDBCドライバは実際に利用するものを追加してください。

Mavenの `pom.xml`
```xml
<dependencies>
    ...
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
        <version>2.0.0.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>ninja.cero.bootiful-sqltemplate</groupId>
        <artifactId>bootiful-sqltemplate</artifactId>
        <version>2.0.0</version>
    </dependency>
    <!-- 任意のJDBCドライバと差し替えて構いません -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>1.4.200</version>
    </dependency>
    ...
</dependencies>
```

Gradleの `build.gradle`
```
dependencies {
    ...
    implementation 'org.springframework.boot:spring-boot-starter-jdbc'
    implementation 'ninja.cero.bootiful-sqltemplate:bootiful-sqltemplate:2.0.0'
    implementation 'com.h2database:h2:1.4.200'
    ...
}
```

これでBootiful SQL Templateのクラス群が利用できるようになります。

#### 2-3. Bean定義の追加

SpringのBean定義として `ninja.cero.sqltempalte.SqlTemplate` を追加します。コンストラクタの引数にはSpring JDBCの `org.springframework.jdbc.core.JdbcTemplate` と `org.springframework.jdbc.core.NamedParameterJdbcTemplate` の2つを渡します。それ以外のコンストラクタについては [6. その他の機能] を参照してください。

```java
@Configuration
public class ApplicationConfig {
	@Bean
	public SqlTemplate sqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		return new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate);
	}
}
```

これで `SqlTemplate` がSpringのコンテナに管理され、インジェクトできる状態になります。

### 3. 検索する - SELECT文の発行

Bootiful SQL Templateを利用して、SELECT文を発行するための操作について説明します。

#### 3-1. 使い方の例

```java
// (1)
@Component
public class SampleProcess {
    // (2)
    protected SqlTemplate sqlTemplate;

    // (3)
    public SampleProcess(SqlTemplate sqlTemplate) {
        this.sqlTemplate = sqlTemplate;
    }

    public void search() {
        // (4)
        List<Emp> emps = sqlTemplate.file("sql/selectByParam.sql")
                                    .add("deptno", 30)
                                    .add("job", "SALESMAN")
                                    .forList(Emp.class);
        emps.forEach(e -> System.out.println(e.ename));
    }
}
```

(1) (2) `SqlTemplate` クラスのインスタンスをインジェクトできるよう、Springの `@Component` アノテーションをつけたクラスのインスタンス変数として `SqlTemplate` を宣言します。

(3) コンストラクタインジェクションを用いて `SqlTemplate` のインスタンスを受け取ります。好みや組織のルール次第で、コンストラクタインジェクションの代わりに `@Autowired` アノテーションを使っても構いません。

(4) `SqlTemplate` のAPIを用いてSELECTの処理を行い、結果を `java.util.List` として受け取っています。

APIの詳細は次項以降で説明します。

#### 3-2. SQLファイルを使う

`SqlTemplate` クラスで最初に呼び出せるメソッドのうち `file` と `query` の2つがSELECT文の発行に用いるメソッドです。ここではSQLファイルを使うための `file` メソッドについて説明します。

SQLファイルは、SQLクエリを記述したテキストファイルです。変数のバインドや、テンプレートエンジンを利用した動的なクエリの発行なども可能ですが、それらについては後述します。ここでは次のようなシンプルなクエリが記載されたファイルを想定します。

```sql
select
    *
from
    emp
```

SQLファイルはクラスパスの通った場所に置いてください。一般的なMaven形式のプロジェクトであれば `src/main/resouces` の下に置くと良いでしょう。好みによっては `src/main/java` の下でデータベースへのアクセス処理を記述するクラスと同じパッケージに配置しても良いでしょう。SQLファイルの拡張子についても特に規定はありません。

- （参考）ファイルを置く場所の例
    1. `src/main/resources/sql/selectAll.sql`
    2. `src/main/resources/sql/SomeProcess/selectAll.sql`
    3. `src/main/resources/sql/ninja/cero/sqltemplate/example/SomeProcess/selectAll.sql`
    4. `src/main/java/sql/ninja/cero/sqltemplate/example/SomeProcess/selectAll.sql`

そして `file` メソッドの引数に、SQLファイルのパスを指定します。ファイルのパスはクラスパスのルートからの相対パスを記述します。上の例で示した場所にSQLファイルを置いた場合、`file` メソッドの引数はそれぞれ次のようになります。

- （参考）上の例に対応する引数の例
    1. `file("sql/selectAll.sql")`
    2. `file("sql/SomeProcess/selectAll.sql")`
    3. `file("ninja/cero/sqltemplate/example/SomeProcess/selectAll.sql")`
    4. `file("ninja/cero/sqltemplate/example/SomeProcess/selectAll.sql")`

#### 3-3. SQLを文字列で指定する

SQLを文字列で指定するために `query` メソッドを用いることができます。

たとえば次のような記述ができます。

```java
List<Emp> emps = sqlTemplate.query("select * from emp")
                            .forList(Emp.class);
```

この `query` メソッドは、SQLファイルを作るほどでない簡単なクエリを発行する場合や、Javaのコードの中で動的に組み立てたクエリを発行する場合に利用することを想定しています。

#### 3-4. 検索条件の指定

`file` メソッドと `query` メソッドのいずれも、クエリの中に変数を記述して任意の値をバインドさせることができます。変数の記述方法とバインド方法は、大きく分けて2種類あります。

##### (1) バインド変数に ? を用いる

一つはバインド変数に `?` を用いる方法です。たとえば次のようなクエリを記述します。

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

この例では2つの `?` を変数として利用しています。

このように記述したクエリに変数をバインドするために `args` メソッドを利用します。`args` メソッドの引数に `?` と同じ数だけ任意の値を渡します。

```java
List<Emp> emps = sqlTemplate.file("sql/selectByArgs.sql")
                            .args(30, "SALESMAN")
                            .forList(Emp.class);
```

このように指定することで、1番目の `?` には `args` メソッドの第一引数が、2番目の `?` には `args` メソッドの第二引数が渡されます。変数の数に上限はありません。

この記述をした結果、上に書いたクエリは次のように値がバインドされます。

```sql
select
    *
from
    emp
inner join dept
    on emp.deptno = dept.deptno
where
    dept.deptno = 30
    and emp.job = 'SALESEMAN'
```

なお、クエリは `PreparedStatement` が利用されるため、SQLインジェクションの心配はありません。

この `?` を用いたバインド変数は、変数の数が少ない場合の利用を想定しています。変数の数が多くなってくると、何番目にどの値が渡されるのか分かりづらくなってしまうため、次に説明する `:(変数名)` を用いた方法を利用した方が良いでしょう。

##### (2) バインド変数に :(変数名) を用いる - バインド値を列挙する

もう一つの方法は、バインド変数に `:(変数名)` を用いるものです。たとえば次のようなクエリを記述します。

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

この例では `:deptno` と `:job` という2つの変数を利用しています。

このように記述したクエリに変数をバインドするためには、3つの方法があります。1つは個別にバインド変数名とバインドする値を指定する方法、2つ目は変数名と同じフィールドを持つ Value Object (POJO) を利用する方法、3つ目が変数名をキーにした `java.util.Map` を利用する方法です。

バインド変数名と値を指定する方法では `addArg` メソッドを利用し、第一引数に変数名、第二引数にバインドする値を指定します。`addArg` メソッドをチェーンさせることで複数の変数を指定することができます。次のようなコードになります。

```java
List<Emp> emps = sqlTemplate.file("sql/selectByParam.sql")
                            .addArg("deptno", 30)
                            .addArg("job", "SALESMAN")
                            .forList(Emp.class);
```

これでそれぞれの変数に値がバインドされます。バインドした結果は `?` で変数を指定した場合と同等です。

この方法は、バインド変数の数がある程度多く、見通しよく列挙したい場合に利用することを想定しています。

##### (3) バインド変数に :(変数名) を用いる - Value Objectでバインド値を指定する

バインド変数の値指定にValue Objectを利用する場合、まずは変数名と同じフィールド名を持つValue Objectを作成します。Value Objectはアクセサメソッド（getter/setter）を用いて作成しても良いですし、publicフィールドを用いても構いません。ここでは行数の記述が少なくて済むpublicフィールドを例にします。

```java
public class SearchCondition {
    public Integer deptno;
    public String job;
}
```

次にこのValue Objectのインスタンスを作成し、バインドしたい値を代入したものを `args` メソッドの引数に渡します。

```java
SearchCondition searchCondition = new SearchCondition();
searchCondition.deptno = 30;
searchCondition.job = "SALESMAN";

List<Emp> emps = sqlTemplate.file("sql/selectByParam.sql")
                            .args(searchCondition)
                            .forList(Emp.class);
```

このように指定することで `SearchCondigion` インスタンスのフィールド値の値が、クエリの `:(変数名)` にバインドされます。バインドした結果は `?` で変数を指定した場合と同等です。

この方法は、Web APIやUIなど外部から検索条件をValue Objectとして受け取り、その値を用いてクエリを実行するような場合に利用することを想定しています。

##### (4) バインド変数に :(変数名) を用いる - Mapでバインド値を指定する

バインド変数の値指定に `java.util.Map` を利用する場合、`Map` のインスタンスを作成し、`put` メソッドを用いてバインドしたい変数名を第一引数に、値を第二引数にして指定します。その `Map` のインスタンスを `args` メソッドの引数に渡します。

```java
Map<String, Object> condition = new HashMap<>();
condition.put("deptno", 30);
condition.put("job", "SALESMAN");

List<Emp> emps = sqlTemplate.file("sql/selectByParam.sql")
                            .args(condition)
                            .forList(Emp.class);
```

このように指定することで `Map` の `key` の値が、クエリの `:(key名)` にバインドされます。バインドした結果は `?` で変数を指定した場合と同等です。

この方法は、既に作成した `Map` のインスタンスを受け取ってクエリを実行するような場合に利用することを想定しています。

#### 3-5. 結果の取り出し - 結果が複数件の場合

`file` メソッドか `query` メソッドでクエリを指定し、必要に応じて `args` メソッドや `addArg` メソッドで変数のバインドを行った後、クエリを実行して検索結果を取得します。クエリを実行するためのメソッドは、結果をどのように受け取るかによって変わります。ここでは結果が複数件あることが想定できる場合のメソッドについて説明します。

##### (1) List<Value Object> として受け取る

##### (2) List<Map> として受け取る

##### (3) Stream として処理する


#### 3-6. 結果の取り出し - 結果が0件か1件の場合



#### 3-7. テンプレートエンジンを使う





### 4. 更新する - INSERT / UPDATE / DELETE文の発行

#### 4-1. 使い方の例

#### 4-2. SQLファイルを使う / SQLを文字列で指定する

#### 4-3. 検索条件の指定

#### 4-4. 結果の取り出し

### 5. バッチ更新する

### 6. その他の機能

#### 6-1. タイムゾーンを扱う
