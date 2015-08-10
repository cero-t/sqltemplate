DROP TABLE IF EXISTS emp;
DROP TABLE IF EXISTS dept;

CREATE TABLE dept(
    deptno INT PRIMARY KEY
    ,dname VARCHAR(20)
    ,loc VARCHAR(10)
);

CREATE TABLE emp(
    empno INT PRIMARY KEY
    ,ename VARCHAR(20)
    ,job VARCHAR(10)
    ,mgr INT
    ,hiredate DATE
    ,sal FLOAT
    ,comm FLOAT
    ,deptno INT
    ,FOREIGN KEY (deptno) references dept(deptno)
);

DROP TABLE IF EXISTS date_time;

CREATE TABLE date_time(
    util_date TIMESTAMP(6) NULL DEFAULT NULL
    ,sql_date DATE
    ,sql_time TIME(3)
    ,sql_timestamp TIMESTAMP(6) NULL DEFAULT NULL
    ,local_date_time TIMESTAMP(6) NULL DEFAULT NULL
    ,local_date DATE
    ,local_time TIME(3)
    ,zoned_date_time TIMESTAMP(6) NULL DEFAULT NULL
    ,offset_date_time TIMESTAMP(6) NULL DEFAULT NULL
    ,offset_time TIME(3)
);
