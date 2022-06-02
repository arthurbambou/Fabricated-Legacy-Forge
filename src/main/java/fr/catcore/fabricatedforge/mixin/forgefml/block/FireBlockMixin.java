package fr.catcore.fabricatedforge.mixin.forgefml.block;

import fr.catcore.fabricatedforge.mixininterface.IFireBlock;
import fr.catcore.fabricatedforge.forged.ReflectionUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.FireBlock;
import net.minecraft.block.Material;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin extends Block implements IFireBlock {

    @Shadow private int[] field_278;

    @Shadow private int[] field_279;

    public FireBlockMixin(int id, Material material) {
        super(id, material);
    }

    @Shadow public abstract boolean method_434(World world, int i, int j, int k);

    @Inject(method = "method_477", at = @At("HEAD"))
    private void forge_method_477(CallbackInfo ci) {
        this.field_279 = ReflectionUtils.Block_blockFlammability;
        this.field_278 = ReflectionUtils.Block_blockFireSpreadSpeed;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private void method_315(int par1, int par2, int par3) {
        ReflectionUtils.Block_setBurnProperties(par1, par2, par3);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_436(World par1World, int par2, int par3, int par4, Random par5Random) {
        if (par1World.getGameRules().getBoolean("doFireTick")) {
            Block base = Block.BLOCKS[par1World.getBlock(par2, par3 - 1, par4)];
            boolean var6 = base != null && base.isFireSource(par1World, par2, par3 - 1, par4, par1World.getBlockData(par2, par3 - 1, par4), ForgeDirection.UP);
            if (!this.method_434(par1World, par2, par3, par4)) {
                par1World.method_3690(par2, par3, par4, 0);
            }

            if (var6
                    || !par1World.isRaining()
                    || !par1World.method_3580(par2, par3, par4)
                    && !par1World.method_3580(par2 - 1, par3, par4)
                    && !par1World.method_3580(par2 + 1, par3, par4)
                    && !par1World.method_3580(par2, par3, par4 - 1)
                    && !par1World.method_3580(par2, par3, par4 + 1)) {
                int var7 = par1World.getBlockData(par2, par3, par4);
                if (var7 < 15) {
                    par1World.method_3682(par2, par3, par4, var7 + par5Random.nextInt(3) / 2);
                }

                par1World.method_3599(par2, par3, par4, this.id, this.method_473() + par5Random.nextInt(10));
                if (!var6 && !this.method_319(par1World, par2, par3, par4)) {
                    if (!par1World.method_3784(par2, par3 - 1, par4) || var7 > 3) {
                        par1World.method_3690(par2, par3, par4, 0);
                    }
                } else if (!var6 && !this.canBlockCatchFire(par1World, par2, par3 - 1, par4, ForgeDirection.UP) && var7 == 15 && par5Random.nextInt(4) == 0) {
                    par1World.method_3690(par2, par3, par4, 0);
                } else {
                    boolean var8 = par1World.method_3582(par2, par3, par4);
                    byte var9 = 0;
                    if (var8) {
                        var9 = -50;
                    }

                    this.tryToCatchBlockOnFire(par1World, par2 + 1, par3, par4, 300 + var9, par5Random, var7, ForgeDirection.WEST);
                    this.tryToCatchBlockOnFire(par1World, par2 - 1, par3, par4, 300 + var9, par5Random, var7, ForgeDirection.EAST);
                    this.tryToCatchBlockOnFire(par1World, par2, par3 - 1, par4, 250 + var9, par5Random, var7, ForgeDirection.UP);
                    this.tryToCatchBlockOnFire(par1World, par2, par3 + 1, par4, 250 + var9, par5Random, var7, ForgeDirection.DOWN);
                    this.tryToCatchBlockOnFire(par1World, par2, par3, par4 - 1, 300 + var9, par5Random, var7, ForgeDirection.SOUTH);
                    this.tryToCatchBlockOnFire(par1World, par2, par3, par4 + 1, 300 + var9, par5Random, var7, ForgeDirection.NORTH);

                    for(int var10 = par2 - 1; var10 <= par2 + 1; ++var10) {
                        for(int var11 = par4 - 1; var11 <= par4 + 1; ++var11) {
                            for(int var12 = par3 - 1; var12 <= par3 + 4; ++var12) {
                                if (var10 != par2 || var12 != par3 || var11 != par4) {
                                    int var13 = 100;
                                    if (var12 > par3 + 1) {
                                        var13 += (var12 - (par3 + 1)) * 100;
                                    }

                                    int var14 = this.method_320(par1World, var10, var12, var11);
                                    if (var14 > 0) {
                                        int var15 = (var14 + 40 + par1World.field_4556 * 7) / (var7 + 30);
                                        if (var8) {
                                            var15 /= 2;
                                        }

                                        if (var15 > 0
                                                && par5Random.nextInt(var13) <= var15
                                                && (!par1World.isRaining() || !par1World.method_3580(var10, var12, var11))
                                                && !par1World.method_3580(var10 - 1, var12, par4)
                                                && !par1World.method_3580(var10 + 1, var12, var11)
                                                && !par1World.method_3580(var10, var12, var11 - 1)
                                                && !par1World.method_3580(var10, var12, var11 + 1)) {
                                            int var16 = var7 + par5Random.nextInt(5) / 4;
                                            if (var16 > 15) {
                                                var16 = 15;
                                            }

                                            par1World.method_3683(var10, var12, var11, this.id, var16);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                par1World.method_3690(par2, par3, par4, 0);
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private void method_316(World par1World, int par2, int par3, int par4, int par5, Random par6Random, int par7) {
        this.tryToCatchBlockOnFire(par1World, par2, par3, par4, par5, par6Random, par7, ForgeDirection.UP);
    }

    @Unique
    private void tryToCatchBlockOnFire(World par1World, int par2, int par3, int par4, int par5, Random par6Random, int par7, ForgeDirection face) {
        int var8 = 0;
        Block block = Block.BLOCKS[par1World.getBlock(par2, par3, par4)];
        if (block != null) {
            var8 = block.getFlammability(par1World, par2, par3, par4, par1World.getBlockData(par2, par3, par4), face);
        }

        if (par6Random.nextInt(par5) < var8) {
            boolean var9 = par1World.getBlock(par2, par3, par4) == Block.TNT.id;
            if (par6Random.nextInt(par7 + 10) < 5 && !par1World.method_3580(par2, par3, par4)) {
                int var10 = par7 + par6Random.nextInt(5) / 4;
                if (var10 > 15) {
                    var10 = 15;
                }

                par1World.method_3683(par2, par3, par4, this.id, var10);
            } else {
                par1World.method_3690(par2, par3, par4, 0);
            }

            if (var9) {
                Block.TNT.method_451(par1World, par2, par3, par4, 1);
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private boolean method_319(World par1World, int par2, int par3, int par4) {
        return this.canBlockCatchFire(par1World, par2 + 1, par3, par4, ForgeDirection.WEST)
                || this.canBlockCatchFire(par1World, par2 - 1, par3, par4, ForgeDirection.EAST)
                || this.canBlockCatchFire(par1World, par2, par3 - 1, par4, ForgeDirection.UP)
                || this.canBlockCatchFire(par1World, par2, par3 + 1, par4, ForgeDirection.DOWN)
                || this.canBlockCatchFire(par1World, par2, par3, par4 - 1, ForgeDirection.SOUTH)
                || this.canBlockCatchFire(par1World, par2, par3, par4 + 1, ForgeDirection.NORTH);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private int method_320(World par1World, int par2, int par3, int par4) {
        byte var5 = 0;
        if (!par1World.isAir(par2, par3, par4)) {
            return 0;
        } else {
            int var6 = this.getChanceToEncourageFire(par1World, par2 + 1, par3, par4, var5, ForgeDirection.WEST);
            var6 = this.getChanceToEncourageFire(par1World, par2 - 1, par3, par4, var6, ForgeDirection.EAST);
            var6 = this.getChanceToEncourageFire(par1World, par2, par3 - 1, par4, var6, ForgeDirection.UP);
            var6 = this.getChanceToEncourageFire(par1World, par2, par3 + 1, par4, var6, ForgeDirection.DOWN);
            var6 = this.getChanceToEncourageFire(par1World, par2, par3, par4 - 1, var6, ForgeDirection.SOUTH);
            return this.getChanceToEncourageFire(par1World, par2, par3, par4 + 1, var6, ForgeDirection.NORTH);
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_317(BlockView par1IBlockAccess, int par2, int par3, int par4) {
        return this.canBlockCatchFire(par1IBlockAccess, par2, par3, par4, ForgeDirection.UP);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public int method_318(World par1World, int par2, int par3, int par4, int par5) {
        return this.getChanceToEncourageFire(par1World, par2, par3, par4, par5, ForgeDirection.UP);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public void method_415(World par1World, int par2, int par3, int par4, Random par5Random) {
        if (par5Random.nextInt(24) == 0) {
            par1World.method_3648(
                    (double)((float)par2 + 0.5F),
                    (double)((float)par3 + 0.5F),
                    (double)((float)par4 + 0.5F),
                    "fire.fire",
                    1.0F + par5Random.nextFloat(),
                    par5Random.nextFloat() * 0.7F + 0.3F
            );
        }

        if (!par1World.method_3784(par2, par3 - 1, par4) && !Block.FIRE.canBlockCatchFire(par1World, par2, par3 - 1, par4, ForgeDirection.UP)) {
            if (Block.FIRE.canBlockCatchFire(par1World, par2 - 1, par3, par4, ForgeDirection.EAST)) {
                for(int var6 = 0; var6 < 2; ++var6) {
                    float var7 = (float)par2 + par5Random.nextFloat() * 0.1F;
                    float var8 = (float)par3 + par5Random.nextFloat();
                    float var9 = (float)par4 + par5Random.nextFloat();
                    par1World.method_3621("largesmoke", (double)var7, (double)var8, (double)var9, 0.0, 0.0, 0.0);
                }
            }

            if (Block.FIRE.canBlockCatchFire(par1World, par2 + 1, par3, par4, ForgeDirection.WEST)) {
                for(int var6 = 0; var6 < 2; ++var6) {
                    float var7 = (float)(par2 + 1) - par5Random.nextFloat() * 0.1F;
                    float var8 = (float)par3 + par5Random.nextFloat();
                    float var9 = (float)par4 + par5Random.nextFloat();
                    par1World.method_3621("largesmoke", (double)var7, (double)var8, (double)var9, 0.0, 0.0, 0.0);
                }
            }

            if (Block.FIRE.canBlockCatchFire(par1World, par2, par3, par4 - 1, ForgeDirection.SOUTH)) {
                for(int var6 = 0; var6 < 2; ++var6) {
                    float var7 = (float)par2 + par5Random.nextFloat();
                    float var8 = (float)par3 + par5Random.nextFloat();
                    float var9 = (float)par4 + par5Random.nextFloat() * 0.1F;
                    par1World.method_3621("largesmoke", (double)var7, (double)var8, (double)var9, 0.0, 0.0, 0.0);
                }
            }

            if (Block.FIRE.canBlockCatchFire(par1World, par2, par3, par4 + 1, ForgeDirection.NORTH)) {
                for(int var6 = 0; var6 < 2; ++var6) {
                    float var7 = (float)par2 + par5Random.nextFloat();
                    float var8 = (float)par3 + par5Random.nextFloat();
                    float var9 = (float)(par4 + 1) - par5Random.nextFloat() * 0.1F;
                    par1World.method_3621("largesmoke", (double)var7, (double)var8, (double)var9, 0.0, 0.0, 0.0);
                }
            }

            if (Block.FIRE.canBlockCatchFire(par1World, par2, par3 + 1, par4, ForgeDirection.DOWN)) {
                for(int var6 = 0; var6 < 2; ++var6) {
                    float var7 = (float)par2 + par5Random.nextFloat();
                    float var8 = (float)(par3 + 1) - par5Random.nextFloat() * 0.1F;
                    float var9 = (float)par4 + par5Random.nextFloat();
                    par1World.method_3621("largesmoke", (double)var7, (double)var8, (double)var9, 0.0, 0.0, 0.0);
                }
            }
        } else {
            for(int var6 = 0; var6 < 3; ++var6) {
                float var7 = (float)par2 + par5Random.nextFloat();
                float var8 = (float)par3 + par5Random.nextFloat() * 0.5F + 0.5F;
                float var9 = (float)par4 + par5Random.nextFloat();
                par1World.method_3621("largesmoke", (double)var7, (double)var8, (double)var9, 0.0, 0.0, 0.0);
            }
        }

    }

    @Override
    public boolean canBlockCatchFire(BlockView world, int x, int y, int z, ForgeDirection face) {
        Block block = Block.BLOCKS[world.getBlock(x, y, z)];
        return block != null && block.isFlammable(world, x, y, z, world.getBlockData(x, y, z), face);
    }

    @Override
    public int getChanceToEncourageFire(World world, int x, int y, int z, int oldChance, ForgeDirection face) {
        int newChance = 0;
        Block block = Block.BLOCKS[world.getBlock(x, y, z)];
        if (block != null) {
            newChance = block.getFireSpreadSpeed(world, x, y, z, world.getBlockData(x, y, z), face);
        }

        return Math.max(newChance, oldChance);
    }
}
