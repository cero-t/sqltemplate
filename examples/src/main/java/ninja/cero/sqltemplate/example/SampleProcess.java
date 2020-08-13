package ninja.cero.sqltemplate.example;

import ninja.cero.sqltemplate.example.entity.Emp;
import ninja.cero.sqltemplate.example.entity.SearchCondition;
import org.springframework.stereotype.Component;
import ninja.cero.sqltemplate.SqlTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SampleProcess {
    private SqlTemplate sqlTemplate;

    public SampleProcess(SqlTemplate sqlTemplate) {
        this.sqlTemplate = sqlTemplate;
    }

    public void process() {
        List<Emp> emps = sqlTemplate.file("sql/selectAll.sql")
                .forList(Emp.class);
        emps.forEach(e -> System.out.println(e.ename)); // SMITH ... MILLER

        Emp emp = sqlTemplate.file("sql/selectByEmpno.sql")
                .args(7839)
                .forObject(Emp.class);
        System.out.println(emp.ename); // KING

        emps = sqlTemplate.file("sql/selectByArgs.sql")
                .args(30, "SALESMAN")
                .forList(Emp.class);
        emps.forEach(e -> System.out.println(e.ename)); // ALLEN, WARD, MARTIN, TURNER

        emps = sqlTemplate.file("sql/selectByParam.sql")
                .addArg("deptno", 30)
                .addArg("job", "SALESMAN")
                .forList(Emp.class);
        emps.forEach(e -> System.out.println(e.ename)); // ALLEN, WARD, MARTIN, TURNER

        SearchCondition searchCondition = new SearchCondition();
        searchCondition.deptno = 30;
        searchCondition.job = "SALESMAN";
        emps = sqlTemplate.file("sql/selectByParam.sql")
                .args(searchCondition)
                .forList(Emp.class);
        emps.forEach(e -> System.out.println(e.ename)); // ALLEN, WARD, MARTIN, TURNER

        Map<String, Object> condition = new HashMap<>();
        condition.put("deptno", 30);
        condition.put("job", "SALESMAN");
        emps = sqlTemplate.file("sql/selectByParam.sql")
                .args(condition)
                .forList(Emp.class);
        emps.forEach(e -> System.out.println(e.ename)); // ALLEN, WARD, MARTIN, TURNER
    }
}
