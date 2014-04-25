package info.paveway.util;

public class StringUtil {

    private StringUtil() {

    }

    public static boolean isNullOrEmpty(String string) {
        if ((null != string) && !"".equals(string)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isNotNullOrEmpty(String string) {
        return !isNullOrEmpty(string);
    }
}
