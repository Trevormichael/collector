package collector.lib;

import java.util.List;
import java.util.function.Function;

public class Utils {
    private Utils() {

    }

    public static String upperCaseFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static <T> String joinToString(List<T> list, Function<T, String> transform, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i ++) {
            sb.append(transform.apply(list.get(i)));
            if (i != list.size() - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }
}
