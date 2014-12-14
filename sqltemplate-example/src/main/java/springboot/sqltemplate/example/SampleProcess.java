package springboot.sqltemplate.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springboot.sqltemplate.core.SqlTemplate;
import springboot.sqltemplate.example.entity.Emp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SampleProcess {
    @Autowired
    SqlTemplate query;

    public void process() {
        List<Emp> emps = query.forList("sql/selectAll.sql", Emp.class);
        emps.forEach(e -> System.out.println(e.ename)); // SMITH ... MILLER

        Emp emp = query.forObject("sql/selectByEmpno.sql", Emp.class, 7839);
        System.out.println(emp.ename); // KING

        Map<String, Object> condition = new HashMap<>();
        condition.put("deptno", 30);
        condition.put("job", "SALESMAN");
        emps = query.forList("sql/selectByCondition.sql", Emp.class, condition);
        emps.forEach(e -> System.out.println(e.ename)); // ALLEN, WARD, MARTIN, TURNER
    }
}
