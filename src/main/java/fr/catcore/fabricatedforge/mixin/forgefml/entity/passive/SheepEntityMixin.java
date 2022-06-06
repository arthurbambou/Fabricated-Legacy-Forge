package fr.catcore.fabricatedforge.mixin.forgefml.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;

@Mixin(SheepEntity.class)
public abstract class SheepEntityMixin extends AnimalEntity implements IShearable {
    @Shadow public abstract boolean isSheared();

    @Shadow public abstract void setSheared(boolean bl);

    @Shadow public abstract int method_2866();

    public SheepEntityMixin(World world) {
        super(world);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_2537(PlayerEntity par1EntityPlayer) {
        return super.method_2537(par1EntityPlayer);
    }

    @Override
    public boolean isShearable(ItemStack item, World world, int X, int Y, int Z) {
        return !this.isSheared() && !this.method_2662();
    }

    @Override
    public ArrayList<ItemStack> onSheared(ItemStack item, World world, int X, int Y, int Z, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList();
        this.setSheared(true);
        int i = 1 + this.random.nextInt(3);

        for(int j = 0; j < i; ++j) {
            ret.add(new ItemStack(Block.WOOL.id, 1, this.method_2866()));
        }

        this.world.playSound(this, "mob.sheep.shear", 1.0F, 1.0F);
        return ret;
    }
}
