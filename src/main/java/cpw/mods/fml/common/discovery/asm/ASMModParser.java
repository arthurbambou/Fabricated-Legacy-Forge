package cpw.mods.fml.common.discovery.asm;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ModCandidate;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

public class ASMModParser {
    private Type asmType;
    private int classVersion;
    private Type asmSuperType;
    private LinkedList<ModAnnotation> annotations = Lists.newLinkedList();
    private String baseModProperties;

    public ASMModParser(InputStream stream) throws IOException {
        try {
            ClassReader reader = new ClassReader(stream);
            reader.accept(new ModClassVisitor(this), 0);
        } catch (Exception var3) {
            FMLLog.log(Level.SEVERE, var3, "Unable to read a class file correctly", new Object[0]);
            throw new LoaderException(var3);
        }
    }

    public void beginNewTypeName(String typeQName, int classVersion, String superClassQName) {
        this.asmType = Type.getObjectType(typeQName);
        this.classVersion = classVersion;
        this.asmSuperType = Type.getObjectType(superClassQName);
    }

    public void startClassAnnotation(String annotationName) {
        ModAnnotation ann = new ModAnnotation(ASMModParser.AnnotationType.CLASS, Type.getType(annotationName), this.asmType.getClassName());
        this.annotations.addFirst(ann);
    }

    public void addAnnotationProperty(String key, Object value) {
        ((ModAnnotation)this.annotations.getFirst()).addProperty(key, value);
    }

    public void startFieldAnnotation(String fieldName, String annotationName) {
        ModAnnotation ann = new ModAnnotation(ASMModParser.AnnotationType.FIELD, Type.getType(annotationName), fieldName);
        this.annotations.addFirst(ann);
    }

    public String toString() {
        return Objects.toStringHelper("ASMAnnotationDiscoverer")
                .add("className", this.asmType.getClassName())
                .add("classVersion", this.classVersion)
                .add("superName", this.asmSuperType.getClassName())
                .add("annotations", this.annotations)
                .add("isBaseMod", this.isBaseMod(Collections.emptyList()))
                .add("baseModProperties", this.baseModProperties)
                .toString();
    }

    public Type getASMType() {
        return this.asmType;
    }

    public int getClassVersion() {
        return this.classVersion;
    }

    public Type getASMSuperType() {
        return this.asmSuperType;
    }

    public LinkedList<ModAnnotation> getAnnotations() {
        return this.annotations;
    }

    public void validate() {
    }

    public boolean isBaseMod(List<String> rememberedTypes) {
        return this.getASMSuperType().equals(Type.getType(BaseMod.class)) || rememberedTypes.contains(this.getASMSuperType().getClassName());
    }

    public void setBaseModProperties(String foundProperties) {
        this.baseModProperties = foundProperties;
    }

    public String getBaseModProperties() {
        return this.baseModProperties;
    }

    public void sendToTable(ASMDataTable table, ModCandidate candidate) {
        for(ModAnnotation ma : this.annotations) {
            table.addASMData(candidate, ma.asmType.getClassName(), this.asmType.getClassName(), ma.member, ma.values);
        }

    }

    public void addAnnotationArray(String name) {
        ((ModAnnotation)this.annotations.getFirst()).addArray(name);
    }

    public void addAnnotationEnumProperty(String name, String desc, String value) {
        ((ModAnnotation)this.annotations.getFirst()).addEnumProperty(name, desc, value);
    }

    public void endArray() {
        ((ModAnnotation)this.annotations.getFirst()).endArray();
    }

    public void addSubAnnotation(String name, String desc) {
        ModAnnotation ma = (ModAnnotation)this.annotations.getFirst();
        this.annotations.addFirst(ma.addChildAnnotation(name, desc));
    }

    public void endSubAnnotation() {
        ModAnnotation child = (ModAnnotation)this.annotations.removeFirst();
        this.annotations.addLast(child);
    }

    static enum AnnotationType {
        CLASS,
        FIELD,
        METHOD,
        SUBTYPE;

        private AnnotationType() {
        }
    }
}
