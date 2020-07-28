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
