package ninja.cero.sqltemplate;

/**
 * A non-public (package-private) record, used to verify that RecordMapper can
 * map into records whose canonical constructor is not public. RecordMapper lives
 * in a different package, so this only works via getDeclaredConstructor + setAccessible.
 */
record PackagePrivateEmp(Integer empno, String ename) {
}
