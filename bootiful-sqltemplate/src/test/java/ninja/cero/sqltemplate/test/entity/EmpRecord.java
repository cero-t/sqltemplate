package ninja.cero.sqltemplate.test.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmpRecord(Integer empno,
                        String ename,
                        String job,
                        Integer mgr,
                        LocalDate hiredate,
                        BigDecimal sal,
                        BigDecimal comm,
                        Integer deptno) {
}
