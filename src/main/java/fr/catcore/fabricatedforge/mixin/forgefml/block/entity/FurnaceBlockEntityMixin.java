package fr.catcore.fabricatedforge.mixin.forgefml.block.entity;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.item.*;
import net.minecraft.recipe.SmeltingRecipeRegistry;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FurnaceBlockEntity.class)
public abstract class FurnaceBlockEntityMixin extends BlockEntity implements ISidedInventory {

    @Shadow public int fuelTime;

    @Shadow private ItemStack[] stacks;

    @Shadow public int totalFuelTime;

    @Shadow public abstract boolean isFueled();

    @Shadow public int field_547;

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_545() {
        boolean var1 = this.fuelTime > 0;
        boolean var2 = false;
        if (this.fuelTime > 0) {
            --this.fuelTime;
        }

        if (!this.world.isClient) {
            if (this.fuelTime == 0 && this.method_525()) {
                this.totalFuelTime = this.fuelTime = getBurnTime(this.stacks[1]);
                if (this.fuelTime > 0) {
                    var2 = true;
                    if (this.stacks[1] != null) {
                        --this.stacks[1].count;
                        if (this.stacks[1].count == 0) {
                            this.stacks[1] = this.stacks[1].getItem().getContainerItemStack(this.stacks[1]);
                        }
                    }
                }
            }

            if (this.isFueled() && this.method_525()) {
                ++this.field_547;
                if (this.field_547 == 200) {
                    this.field_547 = 0;
                    this.method_524();
                    var2 = true;
                }
            } else {
                this.field_547 = 0;
            }

            if (var1 != this.fuelTime > 0) {
                var2 = true;
                FurnaceBlock.method_321(this.fuelTime > 0, this.world, this.x, this.y, this.z);
            }
        }

        if (var2) {
            this.markDirty();
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private boolean method_525() {
        if (this.stacks[0] == null) {
            return false;
        } else {
            ItemStack var1 = SmeltingRecipeRegistry.getInstance().getSmeltingResult(this.stacks[0]);
            if (var1 == null) {
                return false;
            } else if (this.stacks[2] == null) {
                return true;
            } else if (!this.stacks[2].equalsIgnoreNbt(var1)) {
                return false;
            } else {
                int result = this.stacks[2].count + var1.count;
                return result <= this.getInvMaxStackAmount() && result <= var1.getMaxCount();
            }
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_524() {
        if (this.method_525()) {
            ItemStack var1 = SmeltingRecipeRegistry.getInstance().getSmeltingResult(this.stacks[0]);
            if (this.stacks[2] == null) {
                this.stacks[2] = var1.copy();
            } else if (this.stacks[2].equalsIgnoreNbt(var1)) {
                this.stacks[2].count += var1.count;
            }

            --this.stacks[0].count;
            if (this.stacks[0].count <= 0) {
                this.stacks[0] = null;
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public static int getBurnTime(ItemStack par0ItemStack) {
        if (par0ItemStack == null) {
            return 0;
        } else {
            int var1 = par0ItemStack.getItem().id;
            Item var2 = par0ItemStack.getItem();
            if (par0ItemStack.getItem() instanceof BlockItem && Block.BLOCKS[var1] != null) {
                Block var3 = Block.BLOCKS[var1];
                if (var3 == Block.WOODEN_SLAB) {
                    return 150;
                }

                if (var3.material == Material.WOOD) {
                    return 300;
                }
            }

            if (var2 instanceof ToolItem && ((ToolItem)var2).getMaterialAsString().equals("WOOD")) {
                return 200;
            } else if (var2 instanceof SwordItem && ((SwordItem)var2).getToolMaterial().equals("WOOD")) {
                return 200;
            } else if (var2 instanceof HoeItem && ((HoeItem)var2).getAsString().equals("WOOD")) {
                return 200;
            } else if (var1 == Item.STICK.id) {
                return 100;
            } else if (var1 == Item.COAL.id) {
                return 1600;
            } else if (var1 == Item.LAVA_BUCKET.id) {
                return 20000;
            } else if (var1 == Block.SAPLING.id) {
                return 100;
            } else {
                return var1 == Item.BLAZE_ROD.id ? 2400 : GameRegistry.getFuelValue(par0ItemStack);
            }
        }
    }

    @Override
    public int getStartInventorySide(ForgeDirection side) {
        if (side == ForgeDirection.DOWN) {
            return 1;
        } else {
            return side == ForgeDirection.UP ? 0 : 2;
        }
    }

    @Override
    public int getSizeInventorySide(ForgeDirection side) {
        return 1;
    }
}
