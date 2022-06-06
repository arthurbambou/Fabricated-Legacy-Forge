package fr.catcore.fabricatedforge.mixin.forgefml.entity.passive;

import cpw.mods.fml.common.registry.VillagerRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Tradable;
import net.minecraft.entity.data.Trader;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collections;
import java.util.Random;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends PassiveEntity implements Tradable, Trader {
    @Shadow public abstract int profession();

    @Shadow
    public static void method_3107(TraderOfferList traderOfferList, int i, Random random, float f) {
    }

    @Shadow
    public static void method_3110(TraderOfferList traderOfferList, int i, Random random, float f) {
    }

    @Shadow private TraderOfferList offers;

    @Shadow private float field_5397;

    @Shadow protected abstract float method_4566(float f);

    public VillagerEntityMixin(World world) {
        super(world);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public String method_2473() {
        switch(this.profession()) {
            case 0:
                return "/mob/villager/farmer.png";
            case 1:
                return "/mob/villager/librarian.png";
            case 2:
                return "/mob/villager/priest.png";
            case 3:
                return "/mob/villager/smith.png";
            case 4:
                return "/mob/villager/butcher.png";
            default:
                return VillagerRegistry.getVillagerSkin(this.profession(), super.method_2473());
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private void method_3111(int par1) {
        if (this.offers != null) {
            this.field_5397 = MathHelper.sqrt((float)this.offers.size()) * 0.2F;
        } else {
            this.field_5397 = 0.0F;
        }

        TraderOfferList var2 = new TraderOfferList();
        VillagerRegistry.manageVillagerTrades(var2, (VillagerEntity)(Object) this, this.profession(), this.random);
        switch(this.profession()) {
            case 0:
                method_3107(var2, Item.WHEAT.id, this.random, this.method_4566(0.9F));
                method_3107(var2, Block.WOOL.id, this.random, this.method_4566(0.5F));
                method_3107(var2, Item.CHICKEN.id, this.random, this.method_4566(0.5F));
                method_3107(var2, Item.FISH_COOKED.id, this.random, this.method_4566(0.4F));
                method_3110(var2, Item.BREAD.id, this.random, this.method_4566(0.9F));
                method_3110(var2, Item.MELON.id, this.random, this.method_4566(0.3F));
                method_3110(var2, Item.APPLE.id, this.random, this.method_4566(0.3F));
                method_3110(var2, Item.COOKIE.id, this.random, this.method_4566(0.3F));
                method_3110(var2, Item.SHEARS.id, this.random, this.method_4566(0.3F));
                method_3110(var2, Item.FLINT_AND_STEEL.id, this.random, this.method_4566(0.3F));
                method_3110(var2, Item.CHICKEN_COOKED.id, this.random, this.method_4566(0.3F));
                method_3110(var2, Item.ARROW.id, this.random, this.method_4566(0.5F));
                if (this.random.nextFloat() < this.method_4566(0.5F)) {
                    var2.add(
                            new TradeOffer(
                                    new ItemStack(Block.GRAVEL_BLOCK, 10), new ItemStack(Item.EMERALD), new ItemStack(Item.FLINT.id, 4 + this.random.nextInt(2), 0)
                            )
                    );
                }
                break;
            case 1:
                method_3107(var2, Item.PAPER.id, this.random, this.method_4566(0.8F));
                method_3107(var2, Item.BOOK.id, this.random, this.method_4566(0.8F));
                method_3107(var2, Item.WRITTEN_BOOK.id, this.random, this.method_4566(0.3F));
                method_3110(var2, Block.BOOKSHELF.id, this.random, this.method_4566(0.8F));
                method_3110(var2, Block.GLASS_BLOCK.id, this.random, this.method_4566(0.2F));
                method_3110(var2, Item.COMPASS.id, this.random, this.method_4566(0.2F));
                method_3110(var2, Item.CLOCK.id, this.random, this.method_4566(0.2F));
                break;
            case 2:
                method_3110(var2, Item.EYE_OF_ENDER.id, this.random, this.method_4566(0.3F));
                method_3110(var2, Item.XP_BOTTLE.id, this.random, this.method_4566(0.2F));
                method_3110(var2, Item.REDSTONE.id, this.random, this.method_4566(0.4F));
                method_3110(var2, Block.GLOWSTONE.id, this.random, this.method_4566(0.3F));
                int[] var3 = new int[]{
                        Item.IRON_SWORD.id,
                        Item.DIAMOND_SWORD.id,
                        Item.field_4266.id,
                        Item.field_4270.id,
                        Item.IRON_AXE.id,
                        Item.DIAMOND_AXE.id,
                        Item.IRON_PICKAXE.id,
                        Item.DIAMOND_PICKAXE.id
                };

                for(int var7 : var3) {
                    if (this.random.nextFloat() < this.method_4566(0.05F)) {
                        var2.add(
                                new TradeOffer(
                                        new ItemStack(var7, 1, 0),
                                        new ItemStack(Item.EMERALD, 2 + this.random.nextInt(3), 0),
                                        EnchantmentHelper.method_3522(this.random, new ItemStack(var7, 1, 0), 5 + this.random.nextInt(15))
                                )
                        );
                    }
                }
                break;
            case 3:
                method_3107(var2, Item.COAL.id, this.random, this.method_4566(0.7F));
                method_3107(var2, Item.IRON_INGOT.id, this.random, this.method_4566(0.5F));
                method_3107(var2, Item.GOLD_INGOT.id, this.random, this.method_4566(0.5F));
                method_3107(var2, Item.DIAMOND.id, this.random, this.method_4566(0.5F));
                method_3110(var2, Item.IRON_SWORD.id, this.random, this.method_4566(0.5F));
                method_3110(var2, Item.DIAMOND_SWORD.id, this.random, this.method_4566(0.5F));
                method_3110(var2, Item.IRON_AXE.id, this.random, this.method_4566(0.3F));
                method_3110(var2, Item.DIAMOND_AXE.id, this.random, this.method_4566(0.3F));
                method_3110(var2, Item.IRON_PICKAXE.id, this.random, this.method_4566(0.5F));
                method_3110(var2, Item.DIAMOND_PICKAXE.id, this.random, this.method_4566(0.5F));
                method_3110(var2, Item.IRON_SHOVEL.id, this.random, this.method_4566(0.2F));
                method_3110(var2, Item.DIAMOND_SHOVEL.id, this.random, this.method_4566(0.2F));
                method_3110(var2, Item.IRON_HOE.id, this.random, this.method_4566(0.2F));
                method_3110(var2, Item.DIAMOND_HOE.id, this.random, this.method_4566(0.2F));
                method_3110(var2, Item.field_4268.id, this.random, this.method_4566(0.2F));
                method_3110(var2, Item.field_4272.id, this.random, this.method_4566(0.2F));
                method_3110(var2, Item.field_4265.id, this.random, this.method_4566(0.2F));
                method_3110(var2, Item.field_4269.id, this.random, this.method_4566(0.2F));
                method_3110(var2, Item.field_4266.id, this.random, this.method_4566(0.2F));
                method_3110(var2, Item.field_4270.id, this.random, this.method_4566(0.2F));
                method_3110(var2, Item.field_4267.id, this.random, this.method_4566(0.2F));
                method_3110(var2, Item.field_4271.id, this.random, this.method_4566(0.2F));
                method_3110(var2, Item.field_4264.id, this.random, this.method_4566(0.1F));
                method_3110(var2, Item.field_4234.id, this.random, this.method_4566(0.1F));
                method_3110(var2, Item.field_4262.id, this.random, this.method_4566(0.1F));
                method_3110(var2, Item.field_4263.id, this.random, this.method_4566(0.1F));
                break;
            case 4:
                method_3107(var2, Item.COAL.id, this.random, this.method_4566(0.7F));
                method_3107(var2, Item.PORKCHOP.id, this.random, this.method_4566(0.5F));
                method_3107(var2, Item.MEAT.id, this.random, this.method_4566(0.5F));
                method_3110(var2, Item.SADDLE.id, this.random, this.method_4566(0.1F));
                method_3110(var2, Item.field_4231.id, this.random, this.method_4566(0.3F));
                method_3110(var2, Item.field_4233.id, this.random, this.method_4566(0.3F));
                method_3110(var2, Item.field_4230.id, this.random, this.method_4566(0.3F));
                method_3110(var2, Item.field_4232.id, this.random, this.method_4566(0.3F));
                method_3110(var2, Item.COOKED_PORKCHOP.id, this.random, this.method_4566(0.3F));
                method_3110(var2, Item.STEAK.id, this.random, this.method_4566(0.3F));
        }

        if (var2.isEmpty()) {
            method_3107(var2, Item.GOLD_INGOT.id, this.random, 1.0F);
        }

        Collections.shuffle(var2);
        if (this.offers == null) {
            this.offers = new TraderOfferList();
        }

        for(int var8 = 0; var8 < par1 && var8 < var2.size(); ++var8) {
            this.offers.method_3562((TradeOffer)var2.get(var8));
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_4475() {
        VillagerRegistry.applyRandomTrade((VillagerEntity)(Object) this, this.world.random);
    }
}
