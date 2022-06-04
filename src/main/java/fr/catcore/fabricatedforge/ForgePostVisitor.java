package fr.catcore.fabricatedforge;

import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.api.TrClass;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

public class ForgePostVisitor implements TinyRemapper.ApplyVisitorProvider {

    private static final Map<ForgeModRemapper.Entry, ForgeModRemapper.Entry> METHOD_OVERWRITES = new HashMap<>();

    static {
        // Forge
        METHOD_OVERWRITES.put(new ForgeModRemapper.Entry(
                "setBurnProperties",
                "(III)V",
                "net/minecraft/class_197"
        ), new ForgeModRemapper.Entry(
                "Block_setBurnProperties",
                "(III)V",
                "fr/catcore/fabricatedforge/forged/ReflectionUtils"
        ));
        METHOD_OVERWRITES.put(new ForgeModRemapper.Entry(
                "method_360",
                "(Lnet/minecraft/class_174;)I",
                "net/minecraft/class_174"
        ), new ForgeModRemapper.Entry(
                "class_174_method_360",
                "(Lnet/minecraft/class_174;)I",
                "fr/catcore/fabricatedforge/forged/ReflectionUtils"
        ));
        METHOD_OVERWRITES.put(new ForgeModRemapper.Entry(
                "method_350",
                "(Lnet/minecraft/class_173;)Z",
                "net/minecraft/class_173"
        ), new ForgeModRemapper.Entry(
                "RailBlock_method_350",
                "(Lnet/minecraft/class_173;)Z",
                "fr/catcore/fabricatedforge/forged/ReflectionUtils"
        ));
        METHOD_OVERWRITES.put(new ForgeModRemapper.Entry(
                "setConnectionCompatibilityLevel",
                "(B)V",
                "net/minecraft/class_469"
        ), new ForgeModRemapper.Entry(
                "class_469_setConnectionCompatibilityLevel",
                "(B)V",
                "fr/catcore/fabricatedforge/forged/ClientReflectionUtils"
        ));
        METHOD_OVERWRITES.put(new ForgeModRemapper.Entry(
                "getConnectionCompatibilityLevel",
                "()B",
                "net/minecraft/class_469"
        ), new ForgeModRemapper.Entry(
                "class_469_getConnectionCompatibilityLevel",
                "()B",
                "fr/catcore/fabricatedforge/forged/ClientReflectionUtils"
        ));
//        METHOD_OVERWRITES.put(new ForgeModRemapper.Entry(
//                "<init>",
//                "(Ljava/lang/String;)V",
//                "net/minecraft/class_1041"
//        ), new ForgeModRemapper.Entry(
//                "<init>",
//                "(Ljava/lang/String;)V",
//                "fr/catcore/fabricatedforge/forged/ItemGroupForged"
//        ));
//        METHOD_OVERWRITES.put(new ForgeModRemapper.Entry(
//                "<init>",
//                "(ILjava/lang/String;)V",
//                "net/minecraft/class_1041"
//        ), new ForgeModRemapper.Entry(
//                "<init>",
//                "(ILjava/lang/String;)V",
//                "fr/catcore/fabricatedforge/forged/ItemGroupForged"
//        ));
//        METHOD_OVERWRITES.put(new ForgeModRemapper.Entry(
//                "<init>",
//                "(Lnet/minecraft/class_1071;III)V",
//                "net/minecraft/class_847"
//        ), new ForgeModRemapper.Entry(
//                "<init>",
//                "(Lnet/minecraft/class_1071;III)V",
//                "fr/catcore/fabricatedforge/forged/WeightedRandomChestContentForged"
//        ));
//        METHOD_OVERWRITES.put(new ForgeModRemapper.Entry(
//                "<init>",
//                "(IIIII)V",
//                "net/minecraft/class_847"
//        ), new ForgeModRemapper.Entry(
//                "<init>",
//                "(IIIII)V",
//                "fr/catcore/fabricatedforge/forged/WeightedRandomChestContentForged"
//        ));
//        METHOD_OVERWRITES.put(new ForgeModRemapper.Entry(
//                "<init>",
//                "(Lnet/minecraft/class_1150;II)V",
//                "net/minecraft/class_1196"
//        ), new ForgeModRemapper.Entry(
//                "<init>",
//                "(Lnet/minecraft/class_1150;II)V",
//                "fr/catcore/fabricatedforge/forged/ChunkForged"
//        ));
//        METHOD_OVERWRITES.put(new ForgeModRemapper.Entry(
//                "<init>",
//                "(Lnet/minecraft/class_1150;[BII)V",
//                "net/minecraft/class_1196"
//        ), new ForgeModRemapper.Entry(
//                "<init>",
//                "(Lnet/minecraft/class_1150;[BII)V",
//                "fr/catcore/fabricatedforge/forged/ChunkForged"
//        ));
//        METHOD_OVERWRITES.put(new ForgeModRemapper.Entry(
//                "<init>",
//                "(Lnet/minecraft/class_1150;[B[BII)V",
//                "net/minecraft/class_1196"
//        ), new ForgeModRemapper.Entry(
//                "<init>",
//                "(Lnet/minecraft/class_1150;[B[BII)V",
//                "fr/catcore/fabricatedforge/forged/ChunkForged"
//        ));
    }

    @Override
    public ClassVisitor insertApplyVisitor(TrClass cls, ClassVisitor next) {
        return new ClassVisitor(Opcodes.ASM9, next) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                return new MethodVisitor(Opcodes.ASM9, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                    @Override
                    public void visitMethodInsn(int opcode, String methodOwner, String methodName, String methodDescriptor, boolean isInterface) {
                        for (Map.Entry<ForgeModRemapper.Entry, ForgeModRemapper.Entry> entry : METHOD_OVERWRITES.entrySet()) {
                            ForgeModRemapper.Entry original = entry.getKey();
                            ForgeModRemapper.Entry newEntry = entry.getValue();

                            if (original.name.equals(methodName) && original.descriptor.equals(methodDescriptor) && original.owner.equals(methodOwner)) {
                                methodName = newEntry.name;
                                methodDescriptor = newEntry.descriptor;
                                methodOwner = newEntry.owner;
                                break;
                            }
                        }

                        super.visitMethodInsn(opcode, methodOwner, methodName, methodDescriptor, isInterface);
                    }

                    @Override
                    public void visitTypeInsn(int opcode, String type) {
                        switch (opcode) {
                            case Opcodes.NEW:

                                switch (type) {
                                    // Forge
//                                    case "net/minecraft/class_1041":
//                                        type = "fr/catcore/fabricatedforge/forged/ItemGroupForged";
//                                        break;
//                                    case "net/minecraft/class_847":
//                                        type = "fr/catcore/fabricatedforge/forged/WeightedRandomChestContentForged";
//                                        break;
//                                    case "net/minecraft/class_1196":
//                                        type = "fr/catcore/fabricatedforge/forged/ChunkForged";
//                                        break;
                                }

                                break;
                        }

                        super.visitTypeInsn(opcode, type);
                    }

                    @Override
                    public void visitFieldInsn(int opcode, String fieldOwner, String fieldName, String fieldDescriptor) {
                        switch (fieldOwner) {
                            // Forge
                            case "net/minecraft/class_197":
                                if (fieldName.equals("blockFireSpreadSpeed")) {
                                    fieldOwner = "fr/catcore/fabricatedforge/forged/ReflectionUtils";
                                    fieldName = "Block_blockFireSpreadSpeed";
                                } else if (fieldName.equals("blockFlammability")) {
                                    fieldOwner = "fr/catcore/fabricatedforge/forged/ReflectionUtils";
                                    fieldName = "Block_blockFlammability";
                                }
                                break;
                            case "net/minecraft/class_205":
                                if (fieldName.equals("disableValidation")) {
                                    fieldOwner = "fr/catcore/fabricatedforge/forged/ReflectionUtils";
                                    fieldName = "TrapdoorBlock_disableValidation";
                                }
                                break;
                            case "net/minecraft/class_469":
                                if (fieldName.equals("connectionCompatibilityLevel")) {
                                    fieldOwner = "fr/catcore/fabricatedforge/forged/ClientReflectionUtils";
                                    fieldName = "class_469_connectionCompatibilityLevel";
                                }
                                break;
                            case "net/minecraft/class_534":
                                if (fieldName.equals("log")) {
                                    fieldOwner = "fr/catcore/fabricatedforge/forged/ClientReflectionUtils";
                                    fieldName = "class_534_log";
                                }
                                break;
                            case "net/minecraft/class_533":
                                if (fieldName.equals("renderingWorldRenderer")) {
                                    fieldOwner = "fr/catcore/fabricatedforge/forged/ClientReflectionUtils";
                                    fieldName = "Tessellator_renderingWorldRenderer";
                                }
                                break;
//                            // Mystcraft
//                            case "xcompwiz/mystcraft/Mystcraft":
//                                if (fieldName.equals("registeredDims")) {
//                                    fieldOwner = "fr/catcore/fabricatedforge/compat/MystcraftCompat";
//                                }
//                                break;
                        }

                        super.visitFieldInsn(opcode, fieldOwner, fieldName, fieldDescriptor);
                    }

                    @Override
                    public void visitLdcInsn(Object value) {
//                        if (value instanceof String) {
//                            String stringValue = (String) value;
//
//                            switch (cls.getName()) {
//                                // Inventory Tweaks
//                                case "InvTweaksLocalization":
//                                    if (name.equals("load") && stringValue.startsWith("invtweaks")) {
//                                        value = "/" + stringValue;
//                                    }
//                                    break;
//
//                                case "InvTweaksObfuscation":
//                                    if (stringValue.equals("b")) value = "field_1386";
//                                    else if (stringValue.equals("k")) value = "field_1981";
//                                    break;
//
//                                // Friendssss
//                                case "peterix/friendsss/Friendsss":
//                                    if (stringValue.equals("d")) {
//                                        value = "field_3919";
//                                    }
//                                    break;
//
//                                // Glowstone Seeds
//                                case "mod_GlowstoneSeeds":
//                                    if (stringValue.equals("glowstoneseed.png")) {
//                                        value = "/glowstone seeds 1.3.2/glowstoneseed.png";
//                                    }
//                                    break;
//
//                                // Smart Moving
//                                case "mod_SmartMoving":
//                                    if (name.equals("<init>") && stringValue.equals("e")) {
//                                        value = "field_2897";
//                                    }
//                                    break;
//
//                                case "net/minecraft/move/SmartMovingContext":
//                                case "net/smart/moving/SmartMovingContext":
//                                case "net/smart/render/SmartRenderContext":
//                                    if (name.equals("<clinit>") && stringValue.equals("P")) {
//                                        value = "field_3774";
//                                    } else if (name.equals("registerAnimation") && stringValue.equals("o")) {
//                                        value = "field_2108";
//                                    } else if (name.equals("TranslateIfNecessary") && stringValue.equals("b")) {
//                                        value = "field_618";
//                                    }
//                                    break;
//
//                                case "net/minecraft/move/render/ModelRotationRenderer":
//                                case "net/smart/moving/render/ModelRotationRenderer":
//                                case "net/smart/render/ModelRotationRenderer":
//                                    if (name.equals("<clinit>")) {
//                                        switch (stringValue) {
//                                            case "q":
//                                                value = "field_1611";
//                                                break;
//                                            case "d":
//                                                value = "method_1196";
//                                                break;
//                                            case "r":
//                                                value = "field_1612";
//                                                break;
//                                        }
//                                    }
//                                    break;
//
//                                case "net/minecraft/move/render/RenderPlayer":
//                                case "net/smart/moving/render/RenderPlayer":
//                                    if (name.equals("initialize")) {
//                                        switch (stringValue) {
//                                            case "a":
//                                                value = "field_2133";
//                                                break;
//                                            case "b":
//                                                value = "field_2134";
//                                                break;
//                                            case "i":
//                                                value = "field_2135";
//                                                break;
//                                        }
//                                    }
//                                    break;
//
//                                case "net/minecraft/move/playerapi/NetServerHandler":
//                                case "net/smart/moving/playerapi/NetServerHandler":
//                                    if (name.equals("<clinit>")) {
//                                        switch (stringValue) {
//                                            case "e":
//                                                value = "field_2897";
//                                                break;
//                                            case "d":
//                                                value = "field_2896";
//                                                break;
//                                            case "connections":
//                                                value = "field_2923";
//                                                break;
//                                        }
//                                    }
//                                    break;
//
//                                case "net/minecraft/move/SmartMovingSelf":
//                                case "net/smart/moving/SmartMovingSelf":
//                                    if (name.equals("<clinit>") && stringValue.equals("c")) {
//                                        value = "field_1059";
//                                    }
//                                    break;
//
//                                case "net/minecraft/move/config/SmartMovingOptions":
//                                case "net/smart/moving/config/SmartMovingOptions":
//                                    if (name.equals("<clinit>") && stringValue.equals("k")) {
//                                        value = "field_1656";
//                                    }
//                                    break;
//
//                                // Portal Gun
//                                case "portalgun/client/core/TickHandlerClient":
//                                    if (name.equals("renderTick")) {
//                                        switch (stringValue) {
//                                            case "equippedProgress":
//                                                value = "field_1878";
//                                                break;
//                                            case "prevEquippedProgress":
//                                                value = "field_1879";
//                                                break;
//                                            case "itemToRender":
//                                                value = "field_1877";
//                                                break;
//                                            case "equippedItemSlot":
//                                                value = "field_1882";
//                                                break;
//                                        }
//                                    }
//                                    break;
//
//                                case "portalgun/common/core/CommonProxy":
//                                    if (name.equals("getWorldDir") && stringValue.equals("chunkSaveLocation")) {
//                                        value = "field_4782";
//                                    }
//                                    break;
//
//                                case "portalgun/client/render/TileRendererPortalMod":
//                                    if (name.equals("updateTexture")) {
//                                        switch (stringValue) {
//                                            case "camRoll":
//                                                value = "field_1826";
//                                                break;
//                                            case "prevCamRoll":
//                                                value = "field_1827";
//                                                break;
//                                            case "fovModifierHand":
//                                                value = "field_1829";
//                                                break;
//                                            case "fovModifierHandPrev":
//                                                value = "field_1830";
//                                                break;
//                                            case "cameraZoom":
//                                                value = "field_1833";
//                                                break;
//                                        }
//                                    }
//                                    break;
//
//                                // GregTech
//                                case "gregtechmod/common/gui/GT_GUIContainer_AESU":
//                                case "gregtechmod/common/gui/GT_GUIContainer_Centrifuge":
//                                case "gregtechmod/common/gui/GT_GUIContainer_ChargeOMat":
//                                case "gregtechmod/common/gui/GT_GUIContainer_ComputerCube":
//                                case "gregtechmod/common/gui/GT_GUIContainer_Destructopack":
//                                case "gregtechmod/common/gui/GT_GUIContainer_ECraftingtable":
//                                case "gregtechmod/common/gui/GT_GUIContainer_ElectricBufferLarge":
//                                case "gregtechmod/common/gui/GT_GUIContainer_ElectricBufferSmall":
//                                case "gregtechmod/common/gui/GT_GUIContainer_Fusionreactor":
//                                case "gregtechmod/common/gui/GT_GUIContainer_IDSU":
//                                case "gregtechmod/common/gui/GT_GUIContainer_LESU":
//                                case "gregtechmod/common/gui/GT_GUIContainer_Matterfabricator":
//                                case "gregtechmod/common/gui/GT_GUIContainer_Quantumchest":
//                                case "gregtechmod/common/gui/GT_GUIContainer_Sonictron":
//                                case "gregtechmod/common/gui/GT_GUIContainer_Translocator":
//                                case "gregtechmod/common/gui/GT_GUIContainer_UUMAssembler":
//                                case "gregtechmod/common/gui/GT_GUIContainerMetaID_Machine":
//                                case "gregtechmod/common/gui/GT_GUIContainerMetaTile_Machine":
//                                    if (stringValue.startsWith("gregtechmod/")) {
//                                        value = "/" + stringValue;
//                                    }
//                                    break;
//                            }
//                        }

                        super.visitLdcInsn(value);
                    }
                };
            }
        };
    }

    protected ForgePostVisitor() {}
}
