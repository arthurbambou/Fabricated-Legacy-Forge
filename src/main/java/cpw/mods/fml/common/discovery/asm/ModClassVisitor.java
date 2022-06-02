package cpw.mods.fml.common.discovery.asm;

import java.util.Collections;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class ModClassVisitor extends ClassVisitor {
    private ASMModParser discoverer;

    public ModClassVisitor(ASMModParser discoverer) {
        super(262144);
        this.discoverer = discoverer;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.discoverer.beginNewTypeName(name, version, superName);
    }

    public AnnotationVisitor visitAnnotation(String annotationName, boolean runtimeVisible) {
        this.discoverer.startClassAnnotation(annotationName);
        return new ModAnnotationVisitor(this.discoverer);
    }

    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return new ModFieldVisitor(name, this.discoverer);
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return this.discoverer.isBaseMod(Collections.emptyList())
                && name.equals("getPriorities")
                && desc.equals(Type.getMethodDescriptor(Type.getType(String.class), new Type[0]))
                ? new ModMethodVisitor(name, this.discoverer)
                : null;
    }
}
