package cpw.mods.fml.common.asm.transformers;

import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.relauncher.FMLRelauncher;
import cpw.mods.fml.relauncher.IClassTransformer;
import java.util.Iterator;
import java.util.List;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class SideTransformer implements IClassTransformer {
    private static String SIDE = FMLRelauncher.side();
    private static final boolean DEBUG = false;

    public SideTransformer() {
    }

    public byte[] transform(String name, byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        if (this.remove(classNode.visibleAnnotations, SIDE)) {
            return null;
        } else {
            Iterator<FieldNode> fields = classNode.fields.iterator();

            while(fields.hasNext()) {
                FieldNode field = (FieldNode)fields.next();
                if (this.remove(field.visibleAnnotations, SIDE)) {
                    fields.remove();
                }
            }

            Iterator<MethodNode> methods = classNode.methods.iterator();

            while(methods.hasNext()) {
                MethodNode method = (MethodNode)methods.next();
                if (this.remove(method.visibleAnnotations, SIDE)) {
                    methods.remove();
                }
            }

            ClassWriter writer = new ClassWriter(1);
            classNode.accept(writer);
            return writer.toByteArray();
        }
    }

    private boolean remove(List<AnnotationNode> anns, String side) {
        if (anns == null) {
            return false;
        } else {
            for(AnnotationNode ann : anns) {
                if (ann.desc.equals(Type.getDescriptor(SideOnly.class)) && ann.values != null) {
                    for(int x = 0; x < ann.values.size() - 1; x += 2) {
                        Object key = ann.values.get(x);
                        Object value = ann.values.get(x + 1);
                        if (key instanceof String && key.equals("value") && value instanceof String[] && !((String[])value)[1].equals(side)) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }
    }
}
