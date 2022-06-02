package net.minecraftforge.event;

import java.lang.reflect.Method;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class ASMEventHandler implements IEventListener {
    private static int IDs = 0;
    private static final String HANDLER_DESC = Type.getInternalName(IEventListener.class);
    private static final String HANDLER_FUNC_DESC = Type.getMethodDescriptor(IEventListener.class.getDeclaredMethods()[0]);
    private static final ASMEventHandler.ASMClassLoader LOADER = new ASMEventHandler.ASMClassLoader();
    private final IEventListener handler;
    private final ForgeSubscribe subInfo;

    public ASMEventHandler(Object target, Method method) throws Exception {
        this.handler = (IEventListener)this.createWrapper(method).getConstructor(Object.class).newInstance(target);
        this.subInfo = (ForgeSubscribe)method.getAnnotation(ForgeSubscribe.class);
    }

    public void invoke(Event event) {
        if (this.handler != null && (!event.isCancelable() || !event.isCanceled() || this.subInfo.receiveCanceled())) {
            this.handler.invoke(event);
        }

    }

    public EventPriority getPriority() {
        return this.subInfo.priority();
    }

    public Class<?> createWrapper(Method callback) {
        ClassWriter cw = new ClassWriter(0);
        String name = this.getUniqueName(callback);
        String desc = name.replace('.', '/');
        String instType = Type.getInternalName(callback.getDeclaringClass());
        String eventType = Type.getInternalName(callback.getParameterTypes()[0]);
        cw.visit(50, 33, desc, null, "java/lang/Object", new String[]{HANDLER_DESC});
        cw.visitSource(".dynamic", null);
        cw.visitField(1, "instance", "Ljava/lang/Object;", null, null).visitEnd();
        MethodVisitor mv = cw.visitMethod(1, "<init>", "(Ljava/lang/Object;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(183, "java/lang/Object", "<init>", "()V");
        mv.visitVarInsn(25, 0);
        mv.visitVarInsn(25, 1);
        mv.visitFieldInsn(181, desc, "instance", "Ljava/lang/Object;");
        mv.visitInsn(177);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
        mv = cw.visitMethod(1, "invoke", HANDLER_FUNC_DESC, null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitFieldInsn(180, desc, "instance", "Ljava/lang/Object;");
        mv.visitTypeInsn(192, instType);
        mv.visitVarInsn(25, 1);
        mv.visitTypeInsn(192, eventType);
        mv.visitMethodInsn(182, instType, callback.getName(), Type.getMethodDescriptor(callback));
        mv.visitInsn(177);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
        cw.visitEnd();
        return LOADER.define(name, cw.toByteArray());
    }

    private String getUniqueName(Method callback) {
        return String.format(
                "%s_%d_%s_%s_%s",
                this.getClass().getName(),
                IDs++,
                callback.getDeclaringClass().getSimpleName(),
                callback.getName(),
                callback.getParameterTypes()[0].getSimpleName()
        );
    }

    private static class ASMClassLoader extends ClassLoader {
        private ASMClassLoader() {
            super(ASMEventHandler.ASMClassLoader.class.getClassLoader());
        }

        public Class<?> define(String name, byte[] data) {
            return this.defineClass(name, data, 0, data.length);
        }
    }
}
