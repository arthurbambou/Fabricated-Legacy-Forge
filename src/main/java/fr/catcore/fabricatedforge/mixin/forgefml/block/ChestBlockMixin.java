package fr.catcore.fabricatedforge.mixin.forgefml.block;

import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin extends BlockWithEntity {

    @Shadow
    public static boolean method_289(World world, int i, int j, int k) {
        return false;
    }

    protected ChestBlockMixin(int i, Material material) {
        super(i, material);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_421(World par1World, int par2, int par3, int par4, PlayerEntity par5EntityPlayer, int par6, float par7, float par8, float par9) {
        Object var10 = (ChestBlockEntity)par1World.method_3781(par2, par3, par4);
        if (var10 == null) {
            return true;
        } else if (par1World.isBlockSolidOnSide(par2, par3 + 1, par4, ForgeDirection.DOWN)) {
            return true;
        } else if (method_289(par1World, par2, par3, par4)) {
            return true;
        } else if (par1World.getBlock(par2 - 1, par3, par4) != this.id
                || !par1World.isBlockSolidOnSide(par2 - 1, par3 + 1, par4, ForgeDirection.DOWN) && !method_289(par1World, par2 - 1, par3, par4)) {
            if (par1World.getBlock(par2 + 1, par3, par4) != this.id
                    || !par1World.isBlockSolidOnSide(par2 + 1, par3 + 1, par4, ForgeDirection.DOWN) && !method_289(par1World, par2 + 1, par3, par4)) {
                if (par1World.getBlock(par2, par3, par4 - 1) != this.id
                        || !par1World.isBlockSolidOnSide(par2, par3 + 1, par4 - 1, ForgeDirection.DOWN) && !method_289(par1World, par2, par3, par4 - 1)) {
                    if (par1World.getBlock(par2, par3, par4 + 1) != this.id
                            || !par1World.isBlockSolidOnSide(par2, par3 + 1, par4 + 1, ForgeDirection.DOWN) && !method_289(par1World, par2, par3, par4 + 1)) {
                        if (par1World.getBlock(par2 - 1, par3, par4) == this.id) {
                            var10 = new DoubleInventory(
                                    "container.chestDouble", (ChestBlockEntity)par1World.method_3781(par2 - 1, par3, par4), (Inventory)var10
                            );
                        }

                        if (par1World.getBlock(par2 + 1, par3, par4) == this.id) {
                            var10 = new DoubleInventory(
                                    "container.chestDouble", (Inventory)var10, (ChestBlockEntity)par1World.method_3781(par2 + 1, par3, par4)
                            );
                        }

                        if (par1World.getBlock(par2, par3, par4 - 1) == this.id) {
                            var10 = new DoubleInventory(
                                    "container.chestDouble", (ChestBlockEntity)par1World.method_3781(par2, par3, par4 - 1), (Inventory)var10
                            );
                        }

                        if (par1World.getBlock(par2, par3, par4 + 1) == this.id) {
                            var10 = new DoubleInventory(
                                    "container.chestDouble", (Inventory)var10, (ChestBlockEntity)par1World.method_3781(par2, par3, par4 + 1)
                            );
                        }

                        if (par1World.isClient) {
                            return true;
                        } else {
                            par5EntityPlayer.openInventory((Inventory)var10);
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
