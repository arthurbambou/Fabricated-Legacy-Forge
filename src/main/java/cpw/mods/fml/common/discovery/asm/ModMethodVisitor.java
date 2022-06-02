package cpw.mods.fml.common.discovery.asm;

import com.google.common.collect.Lists;
import java.util.LinkedList;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class ModMethodVisitor extends MethodVisitor {
    private ASMModParser discoverer;
    private boolean inCode;
    private LinkedList<Label> labels = Lists.newLinkedList();
    private String foundProperties;
    private boolean validProperties;

    public ModMethodVisitor(String name, ASMModParser discoverer) {
        super(262144);
        this.discoverer = discoverer;
    }

    public void visitCode() {
        this.labels.clear();
    }

    public void visitLdcInsn(Object cst) {
        if (cst instanceof String && this.labels.size() == 1) {
            this.foundProperties = (String)cst;
        }

    }

    public void visitInsn(int opcode) {
        if (176 == opcode && this.labels.size() == 1 && this.foundProperties != null) {
            this.validProperties = true;
        }

    }

    public void visitLabel(Label label) {
        this.labels.push(label);
    }

    public void visitEnd() {
        if (this.validProperties) {
            this.discoverer.setBaseModProperties(this.foundProperties);
        }

    }
}
