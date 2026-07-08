Bootiful SQL Template
===========

A simple SQL template engine for Spring Boot applications.

## Bootiful SQL Templateとは何か？

Bootiful SQL Templateは、SQLを書きたい人にとって使いやすいことを目指したO/Rマッパーです。  
Spring Framework（Spring JDBC）が提供する `JdbcTemplate` と `NamedParameterJdbcTemplate` のラッパーとして実装しており、次の機能を追加しています。

- Fluent API
- SQLテンプレートファイルの利用 (FreeMarkerの書式をサポート)
- POJOや `java.util.Map`、Javaの `record` などのオブジェクトに対するマッピング
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
        <version>4.0.7</version>
    </dependency>
    <dependency>
        <groupId>ninja.cero.bootiful-sqltemplate</groupId>
        <artifactId>bootiful-sqltemplate</artifactId>
        <version>4.0.0</version>
    </dependency>
    <!-- 任意のJDBCドライバと差し替えて構いません -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>2.3.232</version>
    </dependency>
    ...
</dependencies>
```

- Spring BootアプリケーションのConfigurationクラスに `ninja.cero.sqltemplate.SqlTemplate` のBean定義を追加します。

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

- Java 17 以降
- Spring Framework 7.0 以降
- Spring Boot 4.0 以降
- 動作確認済みのRDBMS
    - MySQL
    - PostgreSQL
    - H2Database

> **旧バージョン (Java 8 / Spring Boot 2・3 系) について**
>
> `4.0.0` 以降は Java 17・Spring Framework 7.0・Spring Boot 4.0 をベースラインとしています。Java 8 や Spring Boot 2・3 系で利用したい場合は、`2.x` ブランチおよびバージョン `2.1.x` を参照してください。

#### 2-2. 依存モジュールの追加

ビルド環境のdependenciesに `ninja.cero.bootiful-sqltemplate:bootiful-sqltemplate` を追加します。また、Spring Bootで利用する場合は `org.springframework.boot:spring-boot-starter-jdbc` を追加します。JDBCドライバは実際に利用するものを追加してください。

Mavenの `pom.xml`
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
        <version>4.0.0</version>
    </dependency>
    <!-- 任意のJDBCドライバと差し替えて構いません -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>2.3.232</version>
    </dependency>
    ...
</dependencies>
```

Gradleの `build.gradle`
```
dependencies {
    ...
    implementation 'org.springframework.boot:spring-boot-starter-jdbc'
    implementation 'ninja.cero.bootiful-sqltemplate:bootiful-sqltemplate:4.0.0'
    implementation 'com.h2database:h2:2.3.232'
    ...
}
```

これでBootiful SQL Templateのクラス群が利用できるようになります。

#### 2-3. Bean定義の追加

SpringのBean定義として `ninja.cero.sqltemplate.SqlTemplate` を追加します。コンストラクタの引数にはSpring JDBCの `org.springframework.jdbc.core.JdbcTemplate` と `org.springframework.jdbc.core.NamedParameterJdbcTemplate` の2つを渡します。それ以外のコンストラクタについては [6. その他の機能] を参照してください。

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
    and emp.job = 'SALESMAN'
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

検索結果を `java.util.stream.Stream` として処理したい場合 `forStream` メソッドを利用します。`forStream` には2つの使い方があります。

**(3-a) `Function` を渡す方法（推奨）**

`java.util.function.Function` を引数として渡すと、その関数に `Stream` を渡して処理し、結果だけを受け取ります。`Stream` はライブラリ側で自動的に `close` されるため、閉じ忘れによるリソースリークの心配がありません。通常はこちらを使ってください。

`forStream` メソッドの引数にValue Objectの `class` を指定することができ、指定した場合には第二引数に渡す `Function` は `Stream<Value Object>` を処理して変換などの処理を行う関数になります。第一引数の型を指定しない場合は `Stream<Map<String, Object>>` を処理する `Function` のみを渡します。

ここでは例として、Value Objectを扱うStreamのコードを示します。

```java
Function<Stream<Emp>, Long> summing = stream -> stream.mapToLong(e -> e.sal.longValue()).sum();
Long sum = sqlTemplate.query("select * from emp")
        .forStream(Emp.class, summing);
```

この例ではStreamの処理を利用してEmpのsalの合計値を計算しました。

**(3-b) `Stream` を直接受け取る方法**

`Function` を渡さずに `forStream` を呼ぶと、`Stream` をそのまま戻り値として受け取れます。取得結果をそのまま呼び出し元へ返したい場合（たとえばコントローラのメソッドから `Stream<Emp>` を返す場合）などに利用できます。第一引数にValue Objectの `class` を指定すると `Stream<Value Object>` が、指定しない場合は `Stream<Map<String, Object>>` が返ります。

**この方法で受け取った `Stream` は、必ず呼び出し元で `close` してください。** `close` しないと、内部の JDBC リソース（`Connection` など）が解放されずリークします。次のように try-with-resources を使うのが確実です。

```java
try (Stream<Emp> stream = sqlTemplate.query("select * from emp")
        .forStream(Emp.class)) {
    long sum = stream.mapToLong(e -> e.sal.longValue()).sum();
}
```

なお、この `Stream` は結果を一度にすべてメモリへ読み込むのではなく、`JdbcTemplate#queryForStream` と同じく1行ずつ遅延的に読み出します。Spring Boot を使っている場合、フェッチ件数は `spring.jdbc.template.fetch-size` プロパティで設定でき、これは自動構成された `JdbcTemplate` と、そこから派生する `NamedParameterJdbcTemplate`（どちらも `SqlTemplate` が利用します）の両方に適用されます。ただし、実際にデータベースから少しずつフェッチできるかは利用する JDBC ドライバの挙動に依存します。多くのドライバはデフォルトで結果セット全体をクライアント側にバッファリングするため、通常はトランザクション内でクエリを実行する必要があり、さらにドライバ固有の設定が必要になる場合もあります（詳細は利用するドライバのドキュメントを参照してください）。

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

### 4. 更新する - INSERT / UPDATE / DELETE文の発行

Bootiful SQL Templateを利用して、INSERT / UPDATE / DELETE文を発行するための操作について説明します。基本的な使い方はSELECT文の場合とほとんど同じで、クエリの指定方法（`file` / `query`）やバインド変数の指定方法（`params` / `param` / `addParam`）はそのまま利用できます。SELECT文と異なるのは、結果を取り出すために `update` メソッドを呼び出す点です。

#### 4-1. 使い方の例

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
        System.out.println(count + "件更新しました");
    }
}
```

(1) `SqlTemplate` のAPIを用いてUPDATEの処理を行い、更新された件数を `int` として受け取っています。`file` や `query` でクエリを指定し、必要に応じてバインド変数を指定した後、最後に `update` メソッドを呼び出す点がSELECTとの違いです。

#### 4-2. SQLファイルを使う / SQLを文字列で指定する

クエリの指定方法はSELECT文の場合と同じです。SQLファイルを使う場合は `file` メソッドに、SQLを文字列で直接指定する場合は `query` メソッドにクエリを渡します。INSERT / UPDATE / DELETEのいずれの文も発行できます。

たとえばINSERT文を記述したSQLファイル (`sql/insertByParam.sql`) は次のようになります。

```sql
INSERT
    INTO
        emp
    VALUES
        (:empno, :ename, :job, :mgr, :hiredate, :sal, :comm, :deptno)
```

UPDATE文 (`sql/updateByParam.sql`) は次のようになります。

```sql
UPDATE
        emp
    SET
        job = :job
        ,mgr = :mgr
    WHERE
        empno = :empno
```

DELETE文 (`sql/deleteByArg.sql`) は次のようになります。

```sql
DELETE
    FROM
        emp
    WHERE
        empno = ?
```

`file` メソッドで指定するSQLファイルでは、SELECT文の場合と同様にFreeMarker形式のテンプレートも利用できます（[3-7. テンプレートエンジンを使う] を参照）。

#### 4-3. バインド変数の指定

バインド変数の指定方法もSELECT文の場合と同じで、次の4つの方法が利用できます。詳細は [3-4. 検索条件の指定] を参照してください。

- `?` を用いて `params` メソッドで値を列挙する
- `:(変数名)` を用いて `addParam` メソッドで個別に指定する
- `:(変数名)` を用いて `param` メソッドにValue Object (POJO) を渡す
- `:(変数名)` を用いて `param` メソッドに `java.util.Map` を渡す

Value Objectを用いる例を示します。クエリの `:(変数名)` と同じフィールド名を持つValue Objectを用意し、値を代入して `param` メソッドに渡します。

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

なお、Value ObjectにはJavaの `record` を利用することもできます。

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

`?` を用いる場合は `params` メソッドで値を列挙します。

```java
int count = sqlTemplate.file("sql/deleteByArg.sql")
                       .params(7566)
                       .update();
```

#### 4-4. 結果の取り出し

INSERT / UPDATE / DELETE文では、最後に `update` メソッドを呼び出すことでクエリを実行します。`update` メソッドの戻り値は、そのクエリによって影響を受けた行数（`int`）です。

```java
int count = sqlTemplate.file("sql/deleteByArgs.sql")
                       .params(30, "SALESMAN")
                       .update();
// deptno = 30 かつ job = 'SALESMAN' の行が4件削除された場合、count は 4 になる
```

バインド変数を持たないクエリの場合は `params` や `param` を省略して、`file` または `query` の直後に `update` を呼び出すこともできます。

```java
int count = sqlTemplate.query("delete from emp").update();
```

### 5. バッチ更新する

複数の更新処理をまとめて発行（バッチ更新）したい場合は `batchUpdate` メソッドを利用します。バッチ更新には大きく分けて、複数の異なるSQLをまとめて実行する方法と、1つのSQLに対して複数のパラメータを適用して実行する方法の2種類があります。いずれの場合も、戻り値は各更新の件数を格納した `int[]` です。

#### 5-1. 複数の異なるSQLをまとめて実行する

内容の異なる複数のSQLをまとめて実行したい場合は、`batchUpdate` に続けて `queries` メソッドを呼び出し、実行したいSQLを列挙します。

```java
int[] counts = sqlTemplate.batchUpdate()
        .queries("delete from emp",
                 "insert into emp (empno) values (1234)");
```

この方法ではバインド変数は利用できません。列挙したSQLがそのまま順番に実行され、各SQLの更新件数が `int[]` として返されます。

#### 5-2. 1つのSQLに複数のパラメータを適用する

同じSQLに対して異なるパラメータを繰り返し適用したい場合は、`batchUpdate` に続けて `file`（SQLファイル）または `query`（SQL文字列）でクエリを指定し、`addBatch` メソッドでパラメータの組を追加していき、最後に `execute` メソッドで実行します。

`addBatch` メソッドは、SELECT文のバインド変数と同様に複数の指定方法を持ちます。

##### (1) ? を用いる場合

`?` を用いたクエリには、`addBatch` メソッドの引数に値を列挙します。

```java
int[] counts = sqlTemplate.batchUpdate()
        .query("insert into emp (empno) values (?)")
        .addBatch(111)
        .addBatch(222)
        .execute();
```

##### (2) :(変数名) を用いる場合 - Value Object

`:(変数名)` を用いたクエリには、値を代入したValue Objectを `addBatch` メソッドに渡すことができます。

```java
int[] counts = sqlTemplate.batchUpdate().file("sql/insertByParam.sql")
        .addBatch(emp1)
        .addBatch(emp2)
        .execute();
```

##### (3) :(変数名) を用いる場合 - Map

変数名をキーにした `java.util.Map` を `addBatch` メソッドに渡すこともできます。

```java
int[] counts = sqlTemplate.batchUpdate().file("sql/updateByParam.sql")
        .addBatch(arg1)  // Map<String, Object>
        .addBatch(arg2)
        .execute();
```

##### (4) List でまとめて渡す

Value Objectや `Map` を1件ずつ `addBatch` で追加する代わりに、`addBatches` メソッドに `java.util.List` を渡すことで一括で指定することもできます。`List` の要素はValue Objectでも `Map` でも構いません。

```java
List<Emp> emps = Arrays.asList(emp1, emp2);

int[] counts = sqlTemplate.batchUpdate().file("sql/insertByParam.sql")
        .addBatches(emps)
        .execute();
```

いずれの場合も、戻り値の `int[]` には、追加したパラメータの組ごとの更新件数が、追加した順番で格納されます。

### 6. その他の機能

`SqlTemplate` には、これまでに説明した2引数のコンストラクタ以外に、いくつかのコンストラクタが用意されています。ここではそれらを用いた機能について説明します。

#### 6-1. タイムゾーンを扱う (Experimental)

> この機能は Experimental（実験的）です。挙動が将来変更される可能性があります。

`SqlTemplate` のコンストラクタに `java.time.ZoneId` を渡すことで、タイムゾーンを考慮した日時の変換を行えます。

```java
@Bean
public SqlTemplate sqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    return new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate, ZoneId.of("GMT"));
}
```

ここで指定した `ZoneId` は、タイムゾーンを持つJSR-310の型（`OffsetDateTime` / `ZonedDateTime` / `OffsetTime`）をデータベースへ書き込む際・読み出す際の変換に用いられます。`ZoneId` を指定しない場合は、システムのデフォルトタイムゾーン (`ZoneId.systemDefault()`) が用いられます。

たとえばシステムのデフォルトタイムゾーンが `Asia/Tokyo` の環境で、`ZonedDateTime` として `2001-01-28T12:35:02.789+09:00[Asia/Tokyo]` を保存する場合を考えます。`ZoneId.of("GMT")` を指定した `SqlTemplate` を用いると、この値はGMTに変換されて保存され、読み出した結果は `2001-01-28T03:35:02.789Z[GMT]`（同じ時刻をGMTで表したもの）となります。

なお `java.time.Instant` もサポートしていますが、`Instant` はタイムライン上の絶対時刻（UTC）を表すため、エポックを介して直接変換され（`Timestamp#from` / `Timestamp#toInstant`）、指定した `ZoneId` の影響を受けません。上と同じ環境でも、`Instant` は `SqlTemplate` が `Asia/Tokyo` でも `GMT` でも同じ時刻として保存・読み出しされます。

#### 6-2. Enumをマッピングする

`enum` 型は、バインド変数と検索結果の両方でサポートしています。enumは文字列カラム（`CHAR` / `VARCHAR` 系）との間で、その名前（`Enum#name()`）によって相互に変換されます。JDBCドライバ自身のenum対応には依存しません。

enumを定義し、フィールド（またはrecordコンポーネント）の型として使います。たとえば、先ほどの `Emp` の `job` カラムをenum型にする場合は次のようになります。

```java
public enum Job {
    ANALYST, CLERK, MANAGER, PRESIDENT, SALESMAN
}
```

enumをバインド変数として渡すと `name()` の文字列として保存され、文字列カラムをenumのフィールドへ読み込むと対応する定数に変換されます。これはすべてのパラメータ指定方法（`params` / `param` / `addParam`、Value Object、`Map`、バッチ）と、すべての結果の型（record / public フィールド / アクセサ / 単一カラム）に適用されます。

```java
// 書き込み: Job.SALESMAN は文字列 "SALESMAN" としてバインドされる
List<Emp> emps = sqlTemplate.file("sql/selectByParam.sql")
                            .addParam("deptno", 30)
                            .addParam("job", Job.SALESMAN)
                            .forList(Emp.class);

// 読み込み: "SALESMAN" というカラム値は Job.SALESMAN に変換される
Job job = sqlTemplate.query("select job from emp where empno = ?")
                     .params(7499)
                     .forObject(Job.class);
System.out.println(job); // SALESMAN
```

保存されている文字列がどの定数にも一致しない場合は `IllegalArgumentException`（"No enum constant ..."）がスローされます。サポートするのは文字列カラム（enumの `name()` による対応）のみで、`ordinal()` を用いた数値カラムへのマッピングには対応していません。

#### 6-3. パラメータのバインドやマッピングをカスタマイズする

`SqlTemplate` のコンストラクタに `ParamBuilder` と `MapperBuilder` を渡すことで、バインド変数の値の変換や、検索結果のオブジェクトへのマッピングの挙動をカスタマイズできます。

```java
@Bean
public SqlTemplate sqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    return new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate, new ParamBuilder(), new MapperBuilder());
}
```

`ParamBuilder` は、バインドするパラメータ（`params` / `param` / `addParam` で指定した値）を Spring JDBC が扱う形へ変換する処理を担います。`MapperBuilder` は、検索結果を `forObject` / `forList` などで指定したValue Objectへマッピングする `RowMapper` を生成する処理を担います。標準とは異なる変換やマッピングを行いたい場合は、これらのクラスを継承した独自の実装を渡すことができます。

なお、[6-1. タイムゾーンを扱う] で説明した `ZoneId` を渡すコンストラクタは、内部的には、指定された `ZoneId` を持つ `ParamBuilder` と `MapperBuilder` を生成しています。
