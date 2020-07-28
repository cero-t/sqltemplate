package ninja.cero.sqltemplate.example;

import ninja.cero.sqltemplate.example.entity.Emp;
import org.springframework.stereotype.Component;
import ninja.cero.sqltemplate.SqlTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SampleProcess {
    private SqlTemplate template;

    public SampleProcess(SqlTemplate template) {
        this.template = template;
    }

    public void process() {
        List<Emp> emps = template.file("sql/selectAll.sql")
                .forList(Emp.class);
        emps.forEach(e -> System.out.println(e.ename)); // SMITH ... MILLER

        Emp emp = template.file("sql/selectByEmpno.sql")
                .args(7839)
                .forObject(Emp.class);
        System.out.println(emp.ename); // KING

        Map<String, Object> condition = new HashMap<>();
        condition.put("deptno", 30);
        condition.put("job", "SALESMAN");
        emps = template.file("sql/selectByParam.sql")
                .args(condition)
                .forList(Emp.class);
        emps.forEach(e -> System.out.println(e.ename)); // ALLEN, WARD, MARTIN, TURNER

        emps = template.file("sql/selectByParam.sql")
                .add("deptno", 30)
                .add("job", "SALESMAN")
                .forList(Emp.class);
        emps.forEach(e -> System.out.println(e.ename)); // ALLEN, WARD, MARTIN, TURNER

        emps = template.file("sql/selectByArgs.sql")
                .args(30, "SALESMAN")
                .forList(Emp.class);
        emps.forEach(e -> System.out.println(e.ename)); // ALLEN, WARD, MARTIN, TURNER
    }
}
