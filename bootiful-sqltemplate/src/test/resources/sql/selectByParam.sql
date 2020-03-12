SELECT
        *
    FROM
        emp
    WHERE
        deptno = :deptno
        AND job = :job
    ORDER BY
        empno
