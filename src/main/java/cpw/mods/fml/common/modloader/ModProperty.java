package cpw.mods.fml.common.modloader;

import java.lang.reflect.Field;
import java.util.Map;

public class ModProperty {
    private String info;
    private double min;
    private double max;
    private String name;
    private Field field;

    public ModProperty(Field f, String info, Double min, Double max, String name) {
        this.field = f;
        this.info = info;
        this.min = min != null ? min : Double.MIN_VALUE;
        this.max = max != null ? max : Double.MAX_VALUE;
        this.name = name;
    }

    public ModProperty(Field field, Map<String, Object> annotationInfo) {
        this(
                field, (String)annotationInfo.get("info"), (Double)annotationInfo.get("min"), (Double)annotationInfo.get("max"), (String)annotationInfo.get("name")
        );
    }

    public String name() {
        return this.name;
    }

    public double min() {
        return this.min;
    }

    public double max() {
        return this.max;
    }

    public String info() {
        return this.info;
    }

    public Field field() {
        return this.field;
    }
}
