SELECT
        *
    FROM
        emp
    WHERE
        deptno = ?
        AND job = ?
    ORDER BY
        empno
    LIMIT 1
