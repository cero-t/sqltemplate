package ninja.cero.sqltemplate.example;

import ninja.cero.sqltemplate.example.entity.Emp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ninja.cero.sqltemplate.core.SqlTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SampleProcess {
    @Autowired
    SqlTemplate template;

    public void process() {
        List<Emp> emps = template.forList("sql/selectAll.sql", Emp.class);
        emps.forEach(e -> System.out.println(e.ename)); // SMITH ... MILLER

        Emp emp = template.forObject("sql/selectByEmpno.sql", Emp.class, 7839);
        System.out.println(emp.ename); // KING

        Map<String, Object> condition = new HashMap<>();
        condition.put("deptno", 30);
        condition.put("job", "SALESMAN");
        emps = template.forList("sql/selectByParam.sql", Emp.class, condition);
        emps.forEach(e -> System.out.println(e.ename)); // ALLEN, WARD, MARTIN, TURNER

        emps = template.query("sql/selectByParam.sql", Emp.class)
                .add("deptno", 30)
                .add("job", "SALESMAN")
                .forList();
        emps.forEach(e -> System.out.println(e.ename)); // ALLEN, WARD, MARTIN, TURNER

        emps = template.forList("sql/selectByArgs.sql", Emp.class, 30, "SALESMAN");
        emps.forEach(e -> System.out.println(e.ename)); // ALLEN, WARD, MARTIN, TURNER
    }
}
