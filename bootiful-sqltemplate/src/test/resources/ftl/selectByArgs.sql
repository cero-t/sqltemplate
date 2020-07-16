SELECT
        *
    FROM
        emp
    WHERE
        1 = 1
<#if job??>
        AND job = :job
</#if>
<#if mgr??>
        AND mgr = :mgr
</#if>
<#if deptno??>
        AND deptno = :deptno
</#if>
