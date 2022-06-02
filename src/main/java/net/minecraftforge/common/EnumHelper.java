package net.minecraftforge.common;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Material;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.decoration.Paintings;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.CanSleepEnum;
import net.minecraft.util.EntityBoundaryEnum;
import net.minecraft.util.UseAction;
import net.minecraft.util.class_171;
import net.minecraft.util.class_32;
import net.minecraft.util.hit.HitResultType;
import net.minecraft.world.LightType;

public class EnumHelper {
    private static Object reflectionFactory = null;
    private static Method newConstructorAccessor = null;
    private static Method newInstance = null;
    private static Method newFieldAccessor = null;
    private static Method fieldAccessorSet = null;
    private static boolean isSetup = false;
    private static Class[][] commonTypes = new Class[][]{
            {UseAction.class},
            {ArmorMaterial.class, Integer.TYPE, int[].class, Integer.TYPE},
            {Paintings.class, String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE},
            {EntityGroup.class},
            {EntityCategory.class, Class.class, Integer.TYPE, Material.class, Boolean.TYPE},
            {class_32.class},
            {EnchantmentTarget.class},
            {EntityBoundaryEnum.class},
            {class_171.class},
            {HitResultType.class},
            {LightType.class, Integer.TYPE},
            {CanSleepEnum.class},
            {ToolMaterial.class, Integer.TYPE, Integer.TYPE, Float.TYPE, Integer.TYPE, Integer.TYPE}
    };

    public EnumHelper() {
    }

    public static UseAction addAction(String name) {
        return addEnum(UseAction.class, name);
    }

    public static ArmorMaterial addArmorMaterial(String name, int durability, int[] reductionAmounts, int enchantability) {
        return addEnum(ArmorMaterial.class, name, durability, reductionAmounts, enchantability);
    }

    public static Paintings addArt(String name, String tile, int sizeX, int sizeY, int offsetX, int offsetY) {
        return addEnum(Paintings.class, name, tile, sizeX, sizeY, offsetX, offsetY);
    }

    public static EntityGroup addCreatureAttribute(String name) {
        return addEnum(EntityGroup.class, name);
    }

    public static EntityCategory addCreatureType(String name, Class typeClass, int maxNumber, Material material, boolean peaceful) {
        return addEnum(EntityCategory.class, name, typeClass, maxNumber, material, peaceful);
    }

    public static class_32 addDoor(String name) {
        return addEnum(class_32.class, name);
    }

    public static EnchantmentTarget addEnchantmentType(String name) {
        return addEnum(EnchantmentTarget.class, name);
    }

    public static EntityBoundaryEnum addEntitySize(String name) {
        return addEnum(EntityBoundaryEnum.class, name);
    }

    public static class_171 addMobType(String name) {
        return addEnum(class_171.class, name);
    }

    public static HitResultType addMovingObjectType(String name) {
        if (!isSetup) {
            setup();
        }

        return addEnum(HitResultType.class, name);
    }

    public static LightType addSkyBlock(String name, int lightValue) {
        return addEnum(LightType.class, name, lightValue);
    }

    public static CanSleepEnum addStatus(String name) {
        return addEnum(CanSleepEnum.class, name);
    }

    public static ToolMaterial addToolMaterial(String name, int harvestLevel, int maxUses, float efficiency, int damage, int enchantability) {
        return addEnum(ToolMaterial.class, name, harvestLevel, maxUses, efficiency, damage, enchantability);
    }

    private static void setup() {
        if (!isSetup) {
            try {
                Method getReflectionFactory = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("getReflectionFactory");
                reflectionFactory = getReflectionFactory.invoke(null);
                newConstructorAccessor = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("newConstructorAccessor", Constructor.class);
                newInstance = Class.forName("sun.reflect.ConstructorAccessor").getDeclaredMethod("newInstance", Object[].class);
                newFieldAccessor = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("newFieldAccessor", Field.class, Boolean.TYPE);
                fieldAccessorSet = Class.forName("sun.reflect.FieldAccessor").getDeclaredMethod("set", Object.class, Object.class);
            } catch (Exception var1) {
                var1.printStackTrace();
            }

            isSetup = true;
        }
    }

    private static Object getConstructorAccessor(Class<?> enumClass, Class<?>[] additionalParameterTypes) throws Exception {
        Class<?>[] parameterTypes = new Class[additionalParameterTypes.length + 2];
        parameterTypes[0] = String.class;
        parameterTypes[1] = Integer.TYPE;
        System.arraycopy(additionalParameterTypes, 0, parameterTypes, 2, additionalParameterTypes.length);
        return newConstructorAccessor.invoke(reflectionFactory, enumClass.getDeclaredConstructor(parameterTypes));
    }

    private static <T extends Enum<?>> T makeEnum(Class<T> enumClass, String value, int ordinal, Class<?>[] additionalTypes, Object[] additionalValues) throws Exception {
        Object[] parms = new Object[additionalValues.length + 2];
        parms[0] = value;
        parms[1] = ordinal;
        System.arraycopy(additionalValues, 0, parms, 2, additionalValues.length);
        return (T)enumClass.cast(newInstance.invoke(getConstructorAccessor(enumClass, additionalTypes), (Object)parms));
    }

    public static void setFailsafeFieldValue(Field field, Object target, Object value) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & -17);
        Object fieldAccessor = newFieldAccessor.invoke(reflectionFactory, field, false);
        fieldAccessorSet.invoke(fieldAccessor, target, value);
    }

    private static void blankField(Class<?> enumClass, String fieldName) throws Exception {
        for(Field field : Class.class.getDeclaredFields()) {
            if (field.getName().contains(fieldName)) {
                field.setAccessible(true);
                setFailsafeFieldValue(field, enumClass, null);
                break;
            }
        }

    }

    private static void cleanEnumCache(Class<?> enumClass) throws Exception {
        blankField(enumClass, "enumConstantDirectory");
        blankField(enumClass, "enumConstants");
    }

    public static <T extends Enum<?>> T addEnum(Class<T> enumType, String enumName, Object... paramValues) {
        return addEnum(commonTypes, enumType, enumName, paramValues);
    }

    public static <T extends Enum<?>> T addEnum(Class[][] map, Class<T> enumType, String enumName, Object... paramValues) {
        for(Class[] lookup : map) {
            if (lookup[0] == enumType) {
                Class<?>[] paramTypes = new Class[lookup.length - 1];
                if (paramTypes.length > 0) {
                    System.arraycopy(lookup, 1, paramTypes, 0, paramTypes.length);
                }

                return addEnum(enumType, enumName, paramTypes, paramValues);
            }
        }

        return null;
    }

    public static <T extends Enum<?>> T addEnum(Class<T> enumType, String enumName, Class<?>[] paramTypes, Object[] paramValues) {
        if (!isSetup) {
            setup();
        }

        Field valuesField = null;
        Field[] fields = enumType.getDeclaredFields();
        int flags = 4122;
        String valueType = String.format("[L%s;", enumType.getName().replace('.', '/'));

        for(Field field : fields) {
            if ((field.getModifiers() & flags) == flags && field.getType().getName().replace('.', '/').equals(valueType)) {
                valuesField = field;
                break;
            }
        }

        valuesField.setAccessible(true);

        try {
            T[] previousValues = (T[]) valuesField.get(enumType);
            List<T> values = new ArrayList(Arrays.asList(previousValues));
            T newValue = makeEnum(enumType, enumName, values.size(), paramTypes, paramValues);
            values.add(newValue);
            setFailsafeFieldValue(valuesField, null, values.toArray((Enum[])Array.newInstance(enumType, 0)));
            cleanEnumCache(enumType);
            return newValue;
        } catch (Exception var12) {
            var12.printStackTrace();
            throw new RuntimeException(var12.getMessage(), var12);
        }
    }

    static {
        if (!isSetup) {
            setup();
        }

    }
}
