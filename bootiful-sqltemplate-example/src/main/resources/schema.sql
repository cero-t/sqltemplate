CREATE TABLE DEPT (
  DEPTNO int primary key NOT NULL,
  DNAME varchar default NULL,
  LOC varchar default NULL
);

CREATE TABLE EMP (
  EMPNO int primary key NOT NULL,
  ENAME varchar default NULL,
  JOB varchar default NULL,
  MGR int default NULL,
  HIREDATE date default NULL,
  SAL float default NULL,
  COMM float default NULL,
  DEPTNO int default NULL,
  foreign key(DEPTNO) references DEPT(DEPTNO)
);
