select
    *
from
    emp
inner join dept
    on emp.deptno = dept.deptno
where
    dept.deptno = :deptno
    and emp.job = :job
