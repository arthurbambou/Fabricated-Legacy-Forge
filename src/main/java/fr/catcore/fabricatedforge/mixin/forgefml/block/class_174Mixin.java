package fr.catcore.fabricatedforge.mixin.forgefml.block;

import fr.catcore.fabricatedforge.mixininterface.IRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.RailBlock;
import net.minecraft.block.class_174;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(class_174.class)
public abstract class class_174Mixin {

    @Mutable
    @Shadow @Final private boolean field_310;

    @Shadow protected abstract void method_358(int i);

    @Shadow private World field_306;
    @Shadow private List field_311;

    @Shadow protected abstract boolean method_364(int i, int j, int k);

    @Shadow private int field_307;
    @Shadow private int field_308;
    @Shadow private int field_309;

    @Shadow protected abstract boolean method_366(int i, int j, int k);

    @Shadow protected abstract void method_357();

    @Shadow protected abstract boolean method_367(class_174 arg);

    @Shadow protected abstract class_174 method_361(Vec3i vec3i);

    // Forge Fields
    @Unique // final
    RailBlock field_305;
    @Unique // final
    private boolean canMakeSlopes;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void overwriteCtr(RailBlock par1BlockRail, World par2World, int par3, int par4, int par5, CallbackInfo ci) {
        int var6 = par2World.getBlock(par3, par4, par5);
        RailBlock target = (RailBlock)Block.BLOCKS[var6];
        int var7 = target.getBasicRailMetadata(par2World, null, par3, par4, par5);
        this.field_310 = !target.isFlexibleRail(par2World, par3, par4, par5);
        this.canMakeSlopes = target.canMakeSlopes(par2World, par3, par4, par5);
        this.method_358(var7);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private void method_368(class_174 par1RailLogic) {
        this.field_311.add(new Vec3i(((class_174Mixin)(Object)par1RailLogic).field_307, ((class_174Mixin)(Object)par1RailLogic).field_308, ((class_174Mixin)(Object)par1RailLogic).field_309));
        boolean var2 = this.method_364(this.field_307, this.field_308, this.field_309 - 1);
        boolean var3 = this.method_364(this.field_307, this.field_308, this.field_309 + 1);
        boolean var4 = this.method_364(this.field_307 - 1, this.field_308, this.field_309);
        boolean var5 = this.method_364(this.field_307 + 1, this.field_308, this.field_309);
        byte var6 = -1;
        if (var2 || var3) {
            var6 = 0;
        }

        if (var4 || var5) {
            var6 = 1;
        }

        if (!this.field_310) {
            if (var3 && var5 && !var2 && !var4) {
                var6 = 6;
            }

            if (var3 && var4 && !var2 && !var5) {
                var6 = 7;
            }

            if (var2 && var4 && !var3 && !var5) {
                var6 = 8;
            }

            if (var2 && var5 && !var3 && !var4) {
                var6 = 9;
            }
        }

        if (var6 == 0 && this.canMakeSlopes) {
            if (RailBlock.method_355(this.field_306, this.field_307, this.field_308 + 1, this.field_309 - 1)) {
                var6 = 4;
            }

            if (RailBlock.method_355(this.field_306, this.field_307, this.field_308 + 1, this.field_309 + 1)) {
                var6 = 5;
            }
        }

        if (var6 == 1 && this.canMakeSlopes) {
            if (RailBlock.method_355(this.field_306, this.field_307 + 1, this.field_308 + 1, this.field_309)) {
                var6 = 2;
            }

            if (RailBlock.method_355(this.field_306, this.field_307 - 1, this.field_308 + 1, this.field_309)) {
                var6 = 3;
            }
        }

        if (var6 < 0) {
            var6 = 0;
        }

        int var7 = var6;
        if (this.field_310) {
            var7 = this.field_306.getBlockData(this.field_307, this.field_308, this.field_309) & 8 | var6;
        }

        this.field_306.method_3672(this.field_307, this.field_308, this.field_309, var7);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_362(boolean par1, boolean par2) {
        boolean var3 = this.method_366(this.field_307, this.field_308, this.field_309 - 1);
        boolean var4 = this.method_366(this.field_307, this.field_308, this.field_309 + 1);
        boolean var5 = this.method_366(this.field_307 - 1, this.field_308, this.field_309);
        boolean var6 = this.method_366(this.field_307 + 1, this.field_308, this.field_309);
        byte var7 = -1;
        if ((var3 || var4) && !var5 && !var6) {
            var7 = 0;
        }

        if ((var5 || var6) && !var3 && !var4) {
            var7 = 1;
        }

        if (!this.field_310) {
            if (var4 && var6 && !var3 && !var5) {
                var7 = 6;
            }

            if (var4 && var5 && !var3 && !var6) {
                var7 = 7;
            }

            if (var3 && var5 && !var4 && !var6) {
                var7 = 8;
            }

            if (var3 && var6 && !var4 && !var5) {
                var7 = 9;
            }
        }

        if (var7 == -1) {
            if (var3 || var4) {
                var7 = 0;
            }

            if (var5 || var6) {
                var7 = 1;
            }

            if (!this.field_310) {
                if (par1) {
                    if (var4 && var6) {
                        var7 = 6;
                    }

                    if (var5 && var4) {
                        var7 = 7;
                    }

                    if (var6 && var3) {
                        var7 = 9;
                    }

                    if (var3 && var5) {
                        var7 = 8;
                    }
                } else {
                    if (var3 && var5) {
                        var7 = 8;
                    }

                    if (var6 && var3) {
                        var7 = 9;
                    }

                    if (var5 && var4) {
                        var7 = 7;
                    }

                    if (var4 && var6) {
                        var7 = 6;
                    }
                }
            }
        }

        if (var7 == 0 && this.canMakeSlopes) {
            if (RailBlock.method_355(this.field_306, this.field_307, this.field_308 + 1, this.field_309 - 1)) {
                var7 = 4;
            }

            if (RailBlock.method_355(this.field_306, this.field_307, this.field_308 + 1, this.field_309 + 1)) {
                var7 = 5;
            }
        }

        if (var7 == 1 && this.canMakeSlopes) {
            if (RailBlock.method_355(this.field_306, this.field_307 + 1, this.field_308 + 1, this.field_309)) {
                var7 = 2;
            }

            if (RailBlock.method_355(this.field_306, this.field_307 - 1, this.field_308 + 1, this.field_309)) {
                var7 = 3;
            }
        }

        if (var7 < 0) {
            var7 = 0;
        }

        this.method_358(var7);
        int var8 = var7;
        if (this.field_310) {
            var8 = this.field_306.getBlockData(this.field_307, this.field_308, this.field_309) & 8 | var7;
        }

        if (par2 || this.field_306.getBlockData(this.field_307, this.field_308, this.field_309) != var8) {
            this.field_306.method_3672(this.field_307, this.field_308, this.field_309, var8);

            for(Vec3i var10 : (List<Vec3i>)this.field_311) {
                class_174 var11 = this.method_361(var10);
                if (var11 != null) {
                    ((class_174Mixin)(Object)var11).method_357();
                    if (((class_174Mixin)(Object)var11).method_367((class_174)(Object) this)) {
                        ((class_174Mixin)(Object)var11).method_368((class_174)(Object) this);
                    }
                }
            }
        }

    }
}
