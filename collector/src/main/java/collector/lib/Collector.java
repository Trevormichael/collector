package collector.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import collector.lib.internal.CollectorSuffix;

public class Collector {
    private Collector() {

    }


    @SuppressWarnings("TryWithIdenticalCatches")
    public static void bind(Object target) {
        Class<?> targetClass = target.getClass();
        String className = targetClass.getName();
        try {
            Class<?> collectorClass = targetClass
                    .getClassLoader()
                    .loadClass(className + CollectorSuffix.SUFFIX);
            Constructor<?> classConstructor = collectorClass.getConstructor();
            try {
                Object instance = classConstructor.newInstance();
                instance.getClass().getMethod("bind", targetClass).invoke(instance, target);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to invoke " + classConstructor, e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Unable to invoke " + classConstructor, e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Unable to create instance", e.getCause());
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to find collector class " + className + CollectorSuffix.SUFFIX, e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find constructor for " + className + CollectorSuffix.SUFFIX, e);
        }
    }
}
