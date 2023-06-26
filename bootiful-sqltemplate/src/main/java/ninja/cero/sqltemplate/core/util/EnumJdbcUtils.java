package ninja.cero.sqltemplate.core.util;

import static java.sql.Types.CHAR;
import static java.sql.Types.VARCHAR;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EnumJdbcUtils {

    public static Object getColumnValue(ResultSet rs, int index, Class<?> requiredType) throws SQLException {
        switch (rs.getMetaData().getColumnType(index)) {
            case CHAR:
            case VARCHAR:
                return getFromName(rs.getString(index), requiredType);
            default:
                throw new IllegalArgumentException(
                    "Unsupported sql type: " + rs.getMetaData().getColumnTypeName(index));
        }
    }

    private static Object getFromName(String value, Class<?> requiredType) {
        if (value == null) {
            return null;
        }
        for (Object enumConstant : requiredType.getEnumConstants()) {
            if (((Enum<?>) enumConstant).name().equals(value)) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException(
            "No enum constant " + requiredType.getCanonicalName() + "." + value);
    }
}
