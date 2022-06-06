package fr.catcore.fabricatedforge.mixin.forgefml.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.HashMap;
import java.util.Map;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public static Map method_3528(int par0, ItemStack par1ItemStack) {
        Item var2 = par1ItemStack.getItem();
        HashMap var3 = null;

        for(Enchantment var7 : Enchantment.ALL_ENCHANTMENTS) {
            if (var7 != null && var7.canEnchantItem(par1ItemStack)) {
                for(int var8 = var7.getMinimumLevel(); var8 <= var7.getMaximumLevel(); ++var8) {
                    if (par0 >= var7.getMinimumPower(var8) && par0 <= var7.getMaximumPower(var8)) {
                        if (var3 == null) {
                            var3 = new HashMap();
                        }

                        var3.put(var7.id, new EnchantmentLevelEntry(var7, var8));
                    }
                }
            }
        }

        return var3;
    }
}
