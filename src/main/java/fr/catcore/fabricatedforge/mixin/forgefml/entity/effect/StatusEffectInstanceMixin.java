package fr.catcore.fabricatedforge.mixin.forgefml.entity.effect;

import fr.catcore.fabricatedforge.mixininterface.IStatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(StatusEffectInstance.class)
public class StatusEffectInstanceMixin implements IStatusEffectInstance {

    @Unique
    private List<ItemStack> curativeItems;

    @Inject(method = "<init>(III)V", at = @At("RETURN"))
    private void fmlCtr(int duration, int amplifier, int par3, CallbackInfo ci) {
        this.curativeItems = new ArrayList();
        this.curativeItems.add(new ItemStack(Item.MILK_BUCKET));
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/effect/StatusEffectInstance;)V", at = @At("RETURN"))
    private void fmlCtr(StatusEffectInstance par1PotionEffect, CallbackInfo ci) {
        this.curativeItems = par1PotionEffect.getCurativeItems();
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return this.curativeItems;
    }

    @Override
    public boolean isCurativeItem(ItemStack stack) {
        boolean found = false;

        for(ItemStack curativeItem : this.curativeItems) {
            if (curativeItem.equalsIgnoreNbt(stack)) {
                found = true;
            }
        }

        return found;
    }

    @Override
    public void setCurativeItems(List<ItemStack> curativeItems) {
        this.curativeItems = curativeItems;
    }

    @Override
    public void addCurativeItem(ItemStack stack) {
        boolean found = false;

        for(ItemStack curativeItem : this.curativeItems) {
            if (curativeItem.equalsIgnoreNbt(stack)) {
                found = true;
            }
        }

        if (!found) {
            this.curativeItems.add(stack);
        }

    }
}
