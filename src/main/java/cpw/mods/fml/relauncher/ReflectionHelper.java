package cpw.mods.fml.relauncher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionHelper {
    public ReflectionHelper() {
    }

    public static Field findField(Class<?> clazz, String... fieldNames) {
        Exception failed = null;

        for(String fieldName : fieldNames) {
            try {
                Field f = clazz.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            } catch (Exception var8) {
                failed = var8;
            }
        }

        throw new ReflectionHelper.UnableToFindFieldException(fieldNames, failed);
    }

    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, int fieldIndex) {
        try {
            Field f = classToAccess.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            return (T)f.get(instance);
        } catch (Exception var4) {
            throw new ReflectionHelper.UnableToAccessFieldException(new String[0], var4);
        }
    }

    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, String... fieldNames) {
        try {
            return (T)findField(classToAccess, fieldNames).get(instance);
        } catch (Exception var4) {
            throw new ReflectionHelper.UnableToAccessFieldException(fieldNames, var4);
        }
    }

    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, int fieldIndex) {
        try {
            Field f = classToAccess.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            f.set(instance, value);
        } catch (Exception var5) {
            throw new ReflectionHelper.UnableToAccessFieldException(new String[0], var5);
        }
    }

    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, String... fieldNames) {
        try {
            findField(classToAccess, fieldNames).set(instance, value);
        } catch (Exception var5) {
            throw new ReflectionHelper.UnableToAccessFieldException(fieldNames, var5);
        }
    }

    public static Class<?> getClass(ClassLoader loader, String... classNames) {
        Exception err = null;

        for(String className : classNames) {
            try {
                return Class.forName(className, false, loader);
            } catch (Exception var8) {
                err = var8;
            }
        }

        throw new ReflectionHelper.UnableToFindClassException(classNames, err);
    }

    public static <E> Method findMethod(Class<? super E> clazz, E instance, String[] methodNames, Class<?>... methodTypes) {
        Exception failed = null;

        for(String methodName : methodNames) {
            try {
                Method m = clazz.getDeclaredMethod(methodName, methodTypes);
                m.setAccessible(true);
                return m;
            } catch (Exception var10) {
                failed = var10;
            }
        }

        throw new ReflectionHelper.UnableToFindMethodException(methodNames, failed);
    }

    public static class UnableToAccessFieldException extends RuntimeException {
        private String[] fieldNameList;

        public UnableToAccessFieldException(String[] fieldNames, Exception e) {
            super(e);
            this.fieldNameList = fieldNames;
        }
    }

    public static class UnableToFindClassException extends RuntimeException {
        private String[] classNames;

        public UnableToFindClassException(String[] classNames, Exception err) {
            super(err);
            this.classNames = classNames;
        }
    }

    public static class UnableToFindFieldException extends RuntimeException {
        private String[] fieldNameList;

        public UnableToFindFieldException(String[] fieldNameList, Exception e) {
            super(e);
            this.fieldNameList = fieldNameList;
        }
    }

    public static class UnableToFindMethodException extends RuntimeException {
        private String[] methodNames;

        public UnableToFindMethodException(String[] methodNames, Exception failed) {
            super(failed);
            this.methodNames = methodNames;
        }
    }
}
