package net.minecraftforge.transformers;

import cpw.mods.fml.relauncher.IClassTransformer;
import net.minecraftforge.event.Event;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class EventTransformer implements IClassTransformer {
    public EventTransformer() {
    }

    public byte[] transform(String name, byte[] bytes) {
        if (!name.equals("net.minecraftforge.event.Event") && !name.startsWith("net.minecraft.") && name.indexOf(46) != -1) {
            ClassReader cr = new ClassReader(bytes);
            ClassNode classNode = new ClassNode();
            cr.accept(classNode, 0);

            try {
                if (this.buildEvents(classNode)) {
                    ClassWriter cw = new ClassWriter(3);
                    classNode.accept(cw);
                    return cw.toByteArray();
                } else {
                    return bytes;
                }
            } catch (Exception var6) {
                var6.printStackTrace();
                return bytes;
            }
        } else {
            return bytes;
        }
    }

    private boolean buildEvents(ClassNode classNode) throws Exception {
        Class<?> parent = this.getClass().getClassLoader().loadClass(classNode.superName.replace('/', '.'));
        if (!Event.class.isAssignableFrom(parent)) {
            return false;
        } else {
            boolean hasSetup = false;
            boolean hasGetListenerList = false;
            boolean hasDefaultCtr = false;
            Class<?> listenerListClazz = Class.forName("net.minecraftforge.event.ListenerList", false, this.getClass().getClassLoader());
            Type tList = Type.getType(listenerListClazz);

            for(MethodNode method : classNode.methods) {
                if (method.name.equals("setup") && method.desc.equals(Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0])) && (method.access & 4) == 4) {
                    hasSetup = true;
                }

                if (method.name.equals("getListenerList") && method.desc.equals(Type.getMethodDescriptor(tList, new Type[0])) && (method.access & 1) == 1) {
                    hasGetListenerList = true;
                }

                if (method.name.equals("<init>") && method.desc.equals(Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]))) {
                    hasDefaultCtr = true;
                }
            }

            if (!hasSetup) {
                Type tSuper = Type.getType(classNode.superName);
                classNode.fields.add(new FieldNode(10, "LISTENER_LIST", tList.getDescriptor(), null, null));
                MethodNode method = new MethodNode(262144, 1, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), null, null);
                method.instructions.add(new VarInsnNode(25, 0));
                method.instructions.add(new MethodInsnNode(183, tSuper.getInternalName(), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0])));
                method.instructions.add(new InsnNode(177));
                if (!hasDefaultCtr) {
                    classNode.methods.add(method);
                }

                method = new MethodNode(262144, 4, "setup", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), null, null);
                method.instructions.add(new VarInsnNode(25, 0));
                method.instructions.add(new MethodInsnNode(183, tSuper.getInternalName(), "setup", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0])));
                method.instructions.add(new FieldInsnNode(178, classNode.name, "LISTENER_LIST", tList.getDescriptor()));
                LabelNode initLisitener = new LabelNode();
                method.instructions.add(new JumpInsnNode(198, initLisitener));
                method.instructions.add(new InsnNode(177));
                method.instructions.add(initLisitener);
                method.instructions.add(new FrameNode(3, 0, null, 0, null));
                method.instructions.add(new TypeInsnNode(187, tList.getInternalName()));
                method.instructions.add(new InsnNode(89));
                method.instructions.add(new VarInsnNode(25, 0));
                method.instructions.add(new MethodInsnNode(183, tSuper.getInternalName(), "getListenerList", Type.getMethodDescriptor(tList, new Type[0])));
                method.instructions
                        .add(new MethodInsnNode(183, tList.getInternalName(), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]{tList})));
                method.instructions.add(new FieldInsnNode(179, classNode.name, "LISTENER_LIST", tList.getDescriptor()));
                method.instructions.add(new InsnNode(177));
                classNode.methods.add(method);
                method = new MethodNode(262144, 1, "getListenerList", Type.getMethodDescriptor(tList, new Type[0]), null, null);
                method.instructions.add(new FieldInsnNode(178, classNode.name, "LISTENER_LIST", tList.getDescriptor()));
                method.instructions.add(new InsnNode(176));
                classNode.methods.add(method);
                return true;
            } else if (!hasGetListenerList) {
                throw new RuntimeException("Event class defines setup() but does not define getListenerList! " + classNode.name);
            } else {
                return false;
            }
        }
    }
}
