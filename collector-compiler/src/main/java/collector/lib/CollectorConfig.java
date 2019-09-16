package collector.lib;

import javax.lang.model.element.Element;

import collector.lib.internal.CollectorSuffix;

@SuppressWarnings("WeakerAccess")
public class CollectorConfig {
    private CollectorConfig() {
    }

    public static final String COLLECTOR_CLASS_SUFFIX = CollectorSuffix.SUFFIX;

    public static final String METHOD_BIND = "bind";

    public static final String PARAMETER_INSTANCE = "instance";

    public static final String JAVA_UTIL_ARRAYS = "java.util.Arrays";

    public static String getBuilderName(Element element) {
        String elementName = element.getSimpleName().toString();
        return "build" + Utils.upperCaseFirstLetter(elementName);
    }
}
