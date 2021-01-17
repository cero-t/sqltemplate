package ninja.cero.sqltemplate.example;

import ninja.cero.sqltemplate.SqlTemplate;
import ninja.cero.sqltemplate.example.entity.Emp;
import ninja.cero.sqltemplate.example.entity.SearchCondition;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Component
public class SampleProcess {
    private SqlTemplate sqlTemplate;

    public SampleProcess(SqlTemplate sqlTemplate) {
        this.sqlTemplate = sqlTemplate;
    }

    public void gettingStarted() {
        List<Emp> emps = sqlTemplate.file("sql/selectAll.sql")
                .forList(Emp.class);
        emps.forEach(e -> System.out.println(e.ename)); // SMITH ... MILLER
    }

    public void selectByQuery() {
        List<Emp> emps = sqlTemplate.query("select * from emp")
                .forList(Emp.class);
        emps.forEach(e -> System.out.println(e.ename)); // SMITH ... MILLER
    }

    public void selectByFile() {
        List<Emp> emps = sqlTemplate.file("sql/selectByParams.sql")
                .params(30, "SALESMAN")
                .forList(Emp.class);
        emps.forEach(e -> System.out.println(e.ename)); // ALLEN, WARD, MARTIN, TURNER
    }

    public void selectWithParam() {
        List<Emp> emps = sqlTemplate.file("sql/selectByParam.sql")
                .addParam("deptno", 30)
                .addParam("job", "SALESMAN")
                .forList(Emp.class);
        emps.forEach(e -> System.out.println(e.ename)); // ALLEN, WARD, MARTIN, TURNER

        SearchCondition searchCondition = new SearchCondition();
        searchCondition.deptno = 30;
        searchCondition.job = "SALESMAN";
        emps = sqlTemplate.file("sql/selectByParam.sql")
                .param(searchCondition)
                .forList(Emp.class);
        emps.forEach(e -> System.out.println(e.ename)); // ALLEN, WARD, MARTIN, TURNER

        Map<String, Object> condition = new HashMap<>();
        condition.put("deptno", 30);
        condition.put("job", "SALESMAN");
        emps = sqlTemplate.file("sql/selectByParam.sql")
                .param(condition)
                .forList(Emp.class);
        emps.forEach(e -> System.out.println(e.ename)); // ALLEN, WARD, MARTIN, TURNER
    }

    public void selectMultipleResult() {
        List<Emp> emps1 = sqlTemplate.query("select * from emp")
                .forList(Emp.class);
        emps1.forEach(e -> System.out.println(e.ename)); // SMITH ... MILLER

        List<Map<String, Object>> emps2 = sqlTemplate.query("select * from emp")
                .forList();
        emps2.forEach(e -> System.out.println(e.get("ename"))); // SMITH ... MILLER

        Function<Stream<Emp>, Long> summing = stream -> stream.mapToLong(e -> e.sal.longValue()).sum();
        Long sum = sqlTemplate.query("select * from emp")
                .forStream(Emp.class, summing);
        System.out.println(sum);
    }

    public void selectSingleResult() {
        Emp emp1 = sqlTemplate.query("select * from emp where empno = ?")
                .params(7369)
                .forObject(Emp.class);
        System.out.println(emp1.ename); // SMITH

        Map<String, Object> emp2 = sqlTemplate.query("select * from emp where empno = ?")
                .params(7369)
                .forMap();
        System.out.println(emp2.get("ename")); // SMITH

        Optional<Emp> emp3 = sqlTemplate.query("select * from emp where empno = ?")
                .params(7369)
                .forOptional(Emp.class);
        emp3.ifPresent(e -> System.out.println(e.ename)); // SMITH
    }

    public void selectNoResult() {
        Emp emp1 = sqlTemplate.query("select * from emp where empno = ?")
                .params(0)
                .forObject(Emp.class);
        System.out.println(emp1); // null

        Map<String, Object> emp2 = sqlTemplate.query("select * from emp where empno = ?")
                .params(0)
                .forMap();
        System.out.println(emp2); // null

        Optional<Emp> emp3 = sqlTemplate.query("select * from emp where empno = ?")
                .params(0)
                .forOptional(Emp.class);
        emp3.ifPresent(e -> System.out.println(e.ename)); // Do not show
    }

    public void selectWithTemplate() {
        List<Emp> emps1 = sqlTemplate.file("sql/selectByArbitraryParam.sql")
                .addParam("deptno", 30)
                .forList(Emp.class);
        emps1.forEach(e -> System.out.println(e.ename)); // ALLEN, WARD, MARTIN, BLAKE, TURNER, JAMES
    }

    public void process() {
        gettingStarted();
        selectByQuery();
        selectByFile();
        selectWithParam();
        selectMultipleResult();
        selectSingleResult();
        selectNoResult();
        selectWithTemplate();
    }
}
