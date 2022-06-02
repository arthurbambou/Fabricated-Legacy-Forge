package cpw.mods.fml.common;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
import cpw.mods.fml.relauncher.ReflectionHelper.UnableToFindFieldException;
import java.util.Arrays;
import java.util.logging.Level;

public class ObfuscationReflectionHelper {
    public static boolean obfuscation;

    public ObfuscationReflectionHelper() {
    }

    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, int fieldIndex) {
        try {
            return (T)ReflectionHelper.getPrivateValue(classToAccess, instance, fieldIndex);
        } catch (UnableToAccessFieldException var4) {
            FMLLog.log(Level.SEVERE, var4, "There was a problem getting field index %d from %s", new Object[]{fieldIndex, classToAccess.getName()});
            throw var4;
        }
    }

    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, String... fieldNames) {
        try {
            return (T)ReflectionHelper.getPrivateValue(classToAccess, instance, fieldNames);
        } catch (UnableToFindFieldException var4) {
            FMLLog.log(Level.SEVERE, var4, "Unable to locate any field %s on type %s", new Object[]{Arrays.toString(fieldNames), classToAccess.getName()});
            throw var4;
        } catch (UnableToAccessFieldException var5) {
            FMLLog.log(Level.SEVERE, var5, "Unable to access any field %s on type %s", new Object[]{Arrays.toString(fieldNames), classToAccess.getName()});
            throw var5;
        }
    }

    @Deprecated
    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, int fieldIndex, E value) {
        setPrivateValue(classToAccess, instance, value, fieldIndex);
    }

    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, int fieldIndex) {
        try {
            ReflectionHelper.setPrivateValue(classToAccess, instance, value, fieldIndex);
        } catch (UnableToAccessFieldException var5) {
            FMLLog.log(Level.SEVERE, var5, "There was a problem setting field index %d on type %s", new Object[]{fieldIndex, classToAccess.getName()});
            throw var5;
        }
    }

    @Deprecated
    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, String fieldName, E value) {
        setPrivateValue(classToAccess, instance, value, fieldName);
    }

    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, String... fieldNames) {
        try {
            ReflectionHelper.setPrivateValue(classToAccess, instance, value, fieldNames);
        } catch (UnableToFindFieldException var5) {
            FMLLog.log(Level.SEVERE, var5, "Unable to locate any field %s on type %s", new Object[]{Arrays.toString(fieldNames), classToAccess.getName()});
            throw var5;
        } catch (UnableToAccessFieldException var6) {
            FMLLog.log(Level.SEVERE, var6, "Unable to set any field %s on type %s", new Object[]{Arrays.toString(fieldNames), classToAccess.getName()});
            throw var6;
        }
    }

    public static void detectObfuscation(Class<?> clazz) {
        obfuscation = !clazz.getSimpleName().equals("World");
    }
}
