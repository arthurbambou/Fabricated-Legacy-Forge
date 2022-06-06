package fr.catcore.fabricatedforge.mixin.forgefml.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.ArrayList;

@Mixin(MooshroomEntity.class)
public abstract class MooshroomEntityMixin extends CowEntity implements IShearable {

    public MooshroomEntityMixin(World world) {
        super(world);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_2537(PlayerEntity par1EntityPlayer) {
        ItemStack var2 = par1EntityPlayer.inventory.getMainHandStack();
        if (var2 != null && var2.id == Item.BOWL.id && this.age() >= 0) {
            if (var2.count == 1) {
                par1EntityPlayer.inventory.setInvStack(par1EntityPlayer.inventory.selectedSlot, new ItemStack(Item.MUSHROOM_STEW));
                return true;
            }

            if (par1EntityPlayer.inventory.insertStack(new ItemStack(Item.MUSHROOM_STEW)) && !par1EntityPlayer.abilities.creativeMode) {
                par1EntityPlayer.inventory.takeInvStack(par1EntityPlayer.inventory.selectedSlot, 1);
                return true;
            }
        }

        return super.method_2537(par1EntityPlayer);
    }

    @Override
    public boolean isShearable(ItemStack item, World world, int X, int Y, int Z) {
        return this.age() >= 0;
    }

    @Override
    public ArrayList<ItemStack> onSheared(ItemStack item, World world, int X, int Y, int Z, int fortune) {
        this.remove();
        CowEntity entitycow = new CowEntity(this.world);
        entitycow.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
        entitycow.method_2668(this.method_2600());
        entitycow.field_3313 = this.field_3313;
        this.world.spawnEntity(entitycow);
        this.world.method_3621("largeexplode", this.x, this.y + (double)(this.height / 2.0F), this.z, 0.0, 0.0, 0.0);
        ArrayList<ItemStack> ret = new ArrayList();

        for(int x = 0; x < 5; ++x) {
            ret.add(new ItemStack(Block.RED_MUSHROOM));
        }

        return ret;
    }
}
