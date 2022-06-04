package fr.catcore.fabricatedforge.mixin.forgefml.client.render.entity;

import net.minecraft.block.Block;
import net.minecraft.client.class_534;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Shadow public World world;

    @Shadow public class_534 field_2098;

    @Shadow public GameOptions field_2104;

    @Shadow public MobEntity field_2101;

    @Shadow private TextRenderer textRenderer;

    @Shadow public float yaw;

    @Shadow public float pitch;

    @Shadow public double field_2105;

    @Shadow public double field_2106;

    @Shadow public double field_2107;

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_1523(
            World par1World, class_534 par2RenderEngine, TextRenderer par3FontRenderer, MobEntity par4EntityLiving, GameOptions par5GameSettings, float par6
    ) {
        this.world = par1World;
        this.field_2098 = par2RenderEngine;
        this.field_2104 = par5GameSettings;
        this.field_2101 = par4EntityLiving;
        this.textRenderer = par3FontRenderer;
        if (par4EntityLiving.method_2641()) {
            int x = MathHelper.floor(par4EntityLiving.x);
            int y = MathHelper.floor(par4EntityLiving.y);
            int z = MathHelper.floor(par4EntityLiving.z);
            Block block = Block.BLOCKS[par1World.getBlock(x, y, z)];
            if (block != null && block.isBed(par1World, x, y, z, par4EntityLiving)) {
                int var9 = block.getBedDirection(par1World, x, y, z);
                this.yaw = (float)(var9 * 90 + 180);
                this.pitch = 0.0F;
            }
        } else {
            this.yaw = par4EntityLiving.prevYaw + (par4EntityLiving.yaw - par4EntityLiving.prevYaw) * par6;
            this.pitch = par4EntityLiving.prevPitch + (par4EntityLiving.pitch - par4EntityLiving.prevPitch) * par6;
        }

        if (par5GameSettings.perspective == 2) {
            this.yaw += 180.0F;
        }

        this.field_2105 = par4EntityLiving.prevTickX + (par4EntityLiving.x - par4EntityLiving.prevTickX) * (double)par6;
        this.field_2106 = par4EntityLiving.prevTickY + (par4EntityLiving.y - par4EntityLiving.prevTickY) * (double)par6;
        this.field_2107 = par4EntityLiving.prevTickZ + (par4EntityLiving.z - par4EntityLiving.prevTickZ) * (double)par6;
    }
}
