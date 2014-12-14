package springboot.sqltemplate.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import springboot.sqltemplate.example.entity.Emp;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SampleProcess {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    SqlTemplate query;

    public void process() {
        ReflectionUtils.doWithFields(Emp.class, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                System.out.println(field);
            }
        });

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
