package fr.catcore.fabricatedforge.mixin.forgefml.client.network;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.class_469;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @Shadow @Final private Minecraft field_1646;

    @Shadow private GameMode field_1656;

    @Shadow protected abstract void syncSelectedSlot();

    @Shadow @Final private class_469 field_1647;

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_1223(int par1, int par2, int par3, int par4) {
        ItemStack stack = this.field_1646.playerEntity.getMainHandStack();
        if (stack != null && stack.getItem() != null && stack.getItem().onBlockStartBreak(stack, par1, par2, par3, this.field_1646.playerEntity)) {
            return false;
        } else if (this.field_1656.isAdventure() && !this.field_1646.playerEntity.method_4579(par1, par2, par3)) {
            return false;
        } else {
            ClientWorld var5 = this.field_1646.world;
            Block var6 = Block.BLOCKS[var5.getBlock(par1, par2, par3)];
            if (var6 == null) {
                return false;
            } else {
                var5.playSound(2001, par1, par2, par3, var6.id + (var5.getBlockData(par1, par2, par3) << 12));
                int var7 = var5.getBlockData(par1, par2, par3);
                boolean var8 = var6.removeBlockByPlayer(var5, this.field_1646.playerEntity, par1, par2, par3);
                if (var8) {
                    var6.method_451(var5, par1, par2, par3, var7);
                }

                if (!this.field_1656.isCreative()) {
                    ItemStack var9 = this.field_1646.playerEntity.getMainHandStack();
                    if (var9 != null) {
                        var9.method_3417(var5, var6.id, par1, par2, par3, this.field_1646.playerEntity);
                        if (var9.count == 0) {
                            this.field_1646.playerEntity.removeSelectedSlotItem();
                        }
                    }
                }

                return var8;
            }
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_1229(PlayerEntity par1EntityPlayer, World par2World, ItemStack par3ItemStack, int par4, int par5, int par6, int par7, Vec3d par8Vec3) {
        this.syncSelectedSlot();
        float var9 = (float)par8Vec3.x - (float)par4;
        float var10 = (float)par8Vec3.y - (float)par5;
        float var11 = (float)par8Vec3.z - (float)par6;
        boolean var12 = false;
        int var13 = par2World.getBlock(par4, par5, par6);
        if (par3ItemStack != null
                && par3ItemStack.getItem() != null
                && par3ItemStack.getItem().onItemUseFirst(par3ItemStack, par1EntityPlayer, par2World, par4, par5, par6, par7, var9, var10, var11)) {
            return true;
        } else {
            if (var13 > 0 && Block.BLOCKS[var13].method_421(par2World, par4, par5, par6, par1EntityPlayer, par7, var9, var10, var11)) {
                var12 = true;
            }

            if (!var12 && par3ItemStack != null && par3ItemStack.getItem() instanceof BlockItem) {
                BlockItem var14 = (BlockItem)par3ItemStack.getItem();
                if (!var14.method_3463(par2World, par4, par5, par6, par7, par1EntityPlayer, par3ItemStack)) {
                    return false;
                }
            }

            this.field_1647
                    .sendPacket(new PlayerInteractBlockC2SPacket(par4, par5, par6, par7, par1EntityPlayer.inventory.getMainHandStack(), var9, var10, var11));
            if (var12) {
                return true;
            } else if (par3ItemStack == null) {
                return false;
            } else if (this.field_1656.isCreative()) {
                int var17 = par3ItemStack.getMeta();
                int var15 = par3ItemStack.count;
                boolean var16 = par3ItemStack.method_3413(par1EntityPlayer, par2World, par4, par5, par6, par7, var9, var10, var11);
                par3ItemStack.setDamage(var17);
                par3ItemStack.count = var15;
                return var16;
            } else if (!par3ItemStack.method_3413(par1EntityPlayer, par2World, par4, par5, par6, par7, var9, var10, var11)) {
                return false;
            } else {
                if (par3ItemStack.count <= 0) {
                    MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(par1EntityPlayer, par3ItemStack));
                }

                return true;
            }
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_1228(PlayerEntity par1EntityPlayer, World par2World, ItemStack par3ItemStack) {
        this.syncSelectedSlot();
        this.field_1647.sendPacket(new PlayerInteractBlockC2SPacket(-1, -1, -1, 255, par1EntityPlayer.inventory.getMainHandStack(), 0.0F, 0.0F, 0.0F));
        int var4 = par3ItemStack.count;
        ItemStack var5 = par3ItemStack.onStartUse(par2World, par1EntityPlayer);
        if (var5 != par3ItemStack || var5 != null && var5.count != var4) {
            par1EntityPlayer.inventory.main[par1EntityPlayer.inventory.selectedSlot] = var5;
            if (var5.count <= 0) {
                par1EntityPlayer.inventory.main[par1EntityPlayer.inventory.selectedSlot] = null;
                MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(par1EntityPlayer, var5));
            }

            return true;
        } else {
            return false;
        }
    }
}
