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
- タイムゾーンをサポート (Experimental)

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

(1) (2) `SqlTemplate` クラスのインスタンスをインジェクトできるよう、Springの `@Component` アノテーションをつけたクラスのインスタンス変数として `SqlTemplate` を宣言します。

(3) コンストラクタインジェクションを用いて `SqlTemplate` のインスタンスを受け取ります。コンストラクタインジェクションの代わりに `@Autowired` アノテーションを使っても構いません。

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

SQLファイルはクラスパスの通った場所に置いてください。一般的なMaven形式のプロジェクトであれば `src/main/resouces` の下に置くと良いでしょう。あるいは `src/main/java` の下でデータベースへのアクセス処理を記述するクラスと同じパッケージに配置しても良いでしょう。SQLファイルの拡張子についても特に規定はありません。

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

`SqlTemplate` クラスで最初に呼び出せるメソッドの一つである `query` について説明します。これはSQLを文字列で指定して実行するためのメソッドです。

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

このように記述したクエリに変数をバインドするために `params` メソッドを利用します。`params` メソッドの引数に `?` と同じ数だけ任意の値を渡します。

```java
List<Emp> emps = sqlTemplate.file("sql/selectByParams.sql")
                            .params(30, "SALESMAN")
                            .forList(Emp.class);
```

このように指定することで、1番目の `?` には `params` メソッドの第一引数が、2番目の `?` には `params` メソッドの第二引数が渡されます。変数の数に上限はありません。

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

一つ目のバインド変数名と値を指定する方法では `addParam` メソッドを利用し、第一引数に変数名、第二引数にバインドする値を指定します。`addParam` メソッドをチェーンさせることで複数の変数を指定することができます。次のようなコードになります。

```java
List<Emp> emps = sqlTemplate.file("sql/selectByParam.sql")
                            .addParam("deptno", 30)
                            .addParam("job", "SALESMAN")
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

次にこのValue Objectのインスタンスを作成し、バインドしたい値を代入したものを `param` メソッドの引数に渡します。

```java
SearchCondition searchCondition = new SearchCondition();
searchCondition.deptno = 30;
searchCondition.job = "SALESMAN";

List<Emp> emps = sqlTemplate.file("sql/selectByParam.sql")
                            .param(searchCondition)
                            .forList(Emp.class);
```

このように指定することで `SearchCondigion` インスタンスのフィールド値の値が、クエリの `:(変数名)` にバインドされます。バインドした結果は `?` で変数を指定した場合と同等です。

この方法は、Web APIやUIなど外部から検索条件をValue Objectとして受け取り、その値を用いてクエリを実行するような場合に利用することを想定しています。

##### (4) バインド変数に :(変数名) を用いる - Mapでバインド値を指定する

バインド変数の値指定に `java.util.Map` を利用する場合、`Map` のインスタンスを作成し、`put` メソッドを用いてバインドしたい変数名を第一引数に、値を第二引数にして指定します。その `Map` のインスタンスを `param` メソッドの引数に渡します。

```java
Map<String, Object> condition = new HashMap<>();
condition.put("deptno", 30);
condition.put("job", "SALESMAN");

List<Emp> emps = sqlTemplate.file("sql/selectByParam.sql")
                            .param(condition)
                            .forList(Emp.class);
```

このように指定することで `Map` の `key` の値が、クエリの `:(key名)` にバインドされます。バインドした結果は `?` で変数を指定した場合と同等です。

この方法は、既に作成した `Map` のインスタンスを受け取ってクエリを実行するような場合に利用することを想定しています。

#### 3-5. 結果の取り出し - 結果が複数件の場合

クエリの指定とパラメータの指定をした後、クエリを実行して検索結果を取得します。クエリを実行するためのメソッドは、結果をどのように受け取るかによって変わります。複数件の結果を取得する場合は、`java.util.List` か `java.util.stream.Stream` として取得することができます。その取得方法について説明します。

##### (1) List<Value Object> として受け取る

複数の検索結果を `java.util.List` として受け取りたい場合 `forList` メソッドを利用します。`forList` メソッドの引数にはValue Objectの `class` を指定することができ、指定した場合はメソッドの戻り値が `List<Value Object>` となります。引数を指定しない場合、戻り値は `List<Map<String, Object>>` となります。

Value Objectの `List` として取得したい場合、まずはクエリ結果のカラム名と同じフィールド名を持つValue Objectを作成します。Value Objectはアクセサメソッド（getter/setter）を用いて作成しても良いですし、publicフィールドを用いても構いません。なお、カラム名がスネークケース（たとえば `user_name` など）であったとしても、フィールド名はキャメールケース（たとえば `userName` など）でもスネークケースでもも構いません。

ここではキャメルケースのpublicフィールドを例にします。

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

次にこのValue Objectの `class` を `forList` メソッドの戻り値に渡します。

```java
List<Emp> emps = sqlTemplate.query("select * from emp")
                            .forList(Emp.class);
```

これで、検索結果を `List<Emp>` として受け取ることができます。

複数件の検索結果を扱いたい場合には、基本的にはこの方法を利用することとなります。

##### (2) List<Map> として受け取る

検索結果を `List<Map<String, Object>>` として受け取りたい場合 `forList` メソッドを引数なしで利用します。たとえば次のように記述します。

```java
List<Map<String, Object>> emps = sqlTemplate.query("select * from emp")
                            .forList();
```

戻り値の `Map<String, Object>` にはカラム名をキーとした値が格納されます。ここで、カラム名がスネークケースの場合は `Map` のキーもスネークケースのままになります。また、RDBMSがカラム名をすべて大文字で返す場合には、`Map` のキーもすべて大文字となります。このメソッドを用いる場合には、その2点に注意してください。

検索結果を取得するためにValue Objectをわざわざ作りたくない場合に、この方法を利用することができますが、あまりお勧めはしません。

##### (3) Stream として処理する

検索結果を `java.util.stream.Stream` として処理したい場合 `forStream` メソッドを利用します。この `forStream` メソッドは `Stream` を戻り値として返すのではなく `java.util.function.Function` を引数として受け取って処理をするメソッドです。`Stream` を `close` し忘れることを防ぐためにこのようなAPIの構成にしています（ただし将来的に変更する可能性があります）

`forStream` メソッドの引数にValue Objectの `class` を指定することができ、指定した場合には第二引数に渡す `Function` は `Stream<Value Object>` を処理して変換などの処理を行う関数になります。第一引数の型を指定しない場合は `Stream<Map<String, Object>>` を処理する `Function` のみを渡します。

ここでは例として、Value Objectを扱うStreamのコードを示します。

```java
Function<Stream<Emp>, Long> summing = stream -> stream.mapToLong(e -> e.sal.longValue()).sum();
Long sum = sqlTemplate.query("select * from emp")
        .forStream(Emp.class, summing);
```

この例ではStreamの処理を利用してEmpのsalの合計値を計算しました。

#### 3-6. 結果の取り出し - 結果が0件か1件の場合

クエリの実行結果は、特に主キーによる検索や集計関数を実行した場合に0件か1件になることが想定されます。この場合は Value Objectか `java.util.Map` か `java.util.Optional`として結果を取り出すことができます。その取得方法について説明します。

##### (1) Value Objectとして受け取る

1件の検索結果をValue Objectとして受け取りたい場合 `forObject` メソッドを利用します。`forObject` メソッドの引数にはValue Objectの `class` を指定します。また作成するValue Objectの作成ルールは複数件の取得の時と同様であるため説明を省略します。

取得したいValue Objectの `class` を `forObject` メソッドの戻り値に渡します。

```java
Emp emp = sqlTemplate.query("select * from emp where empno = ?")
        .params(7369)
        .forObject(Emp.class);
```

これで、検索結果を `Emp` として受け取ることができます。検索結果が0件の場合は戻り値がnullとなります。

##### (2) Map<String, Object> として受け取る

検索結果を `Map<String, Object>` として受け取りたい場合 `forMap` メソッドを利用します。たとえば次のように記述します。

```java
Map<String, Object> emp = sqlTemplate.query("select * from emp where empno = ?")
        .params(7369)
        .forMap();
```

戻り値の `Map<String, Object>` にはカラム名をキーとした値が格納されます。ここで、カラム名がスネークケースの場合は `Map` のキーもスネークケースのままになります。また、RDBMSがカラム名をすべて大文字で返す場合には、`Map` のキーもすべて大文字となります。このメソッドを用いる場合には、その2点に注意してください。

検索結果を取得するためにValue Objectをわざわざ作りたくない場合に、この方法を利用することができます。検索結果が0件の場合は戻り値がnullとなります。

##### (3) Optional として受け取る

検索結果を `java.util.Optional` として処理したい場合 `forOptional` メソッドを利用します。`forOptional` メソッドの引数にはValue Objectの `class` を指定することができ、指定した場合はメソッドの戻り値が `Optional<Value Object>` となります。引数を指定しない場合、戻り値は `Optional<Map<String, Object>>` となります。

ここでは例として、Value Objectを扱うStreamのコードを示します。

```java
Optional<Emp> emp = sqlTemplate.query("select * from emp where empno = ?")
        .params(7369)
        .forOptional(Emp.class);
emp.ifPresent(e -> System.out.println(e.ename)); // Do not show
```

結果を `Optional` として受け取ることで、検索結果が0件の場合はnullではなく空の `Optional` となるため、null安全な処理を書けるようになります。

#### 3-7. テンプレートエンジンを使う

SQLファイルを用いて検索するための `file` メソッドは、シンプルなSQLを記述するだけでなく、FreeMarker形式のテンプレートを記述することもできます。このテンプレートを用いることでクエリの一部を動的に組み立てることができます。

テンプレートの文法についてはFreeMarkerの公式サイト (https://freemarker.apache.org/) を参照してください。

ここでは例として、パラメータで指定された値だけを用いて検索を行うようなクエリについて説明します。まずは次のようなFreeMarker形式のテンプレートファイルを作成します。ファイルは仮に `src/main/resources/sql/selectByArbitraryParam.sql` とします。

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

ここではFreeMarkerの `<#if ??>` の文法を用いて、値が指定された場合のみタグの中の文字列（検索条件）を追加するように記述しています。

FreeMarkerに渡すパラメータは、バインド変数の `:(変数名)` と同じ形で渡すことができます。つまり `addParam` メソッドで指定した値や `param` メソッドで指定したValue Objectや `Map` の値が渡されます。たとえば次のようにパラメータを渡したとしましょう。

```java
List<Emp> emps1 = sqlTemplate.file("sql/selectByArbitraryParam.sql")
        .addParam("deptno", 30)
        .forList(Emp.class);
```

これで `deptno` のみが渡された結果、上に示したテンプレートは次のように解釈されます。

```sql
select
    *
from
    emp
where
    1 = 1
    AND deptno = :deptno
```

`deptno` が指定されたため `AND deptno = :deptno` という部分は残り、一方で `mgr` と `job` に関する条件の部分は除去されました。

このようにしてテンプレートにFreeMarkerの文法を用いることで、検索条件を柔軟に変化させることができます。テンプレートの文法は強力であり、理論的にはどのようなクエリを発行することもでき、たとえば `:(変数名)` のバインド変数では指定できないテーブル名なども、テンプレートの文法を使えば指定することができます。ただしその分、うっかりSQLインジェクションに対する脆弱性などを埋め込んでしまうこともあります。その点には注意して利用してください。

（以下、作成中）

### 4. 更新する - INSERT / UPDATE / DELETE文の発行

#### 4-1. 使い方の例

#### 4-2. SQLファイルを使う / SQLを文字列で指定する

#### 4-3. 検索条件の指定

#### 4-4. 結果の取り出し

### 5. バッチ更新する

### 6. その他の機能

#### 6-1. タイムゾーンを扱う
