package net.minecraftforge.event.entity.player;

import java.util.ArrayList;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

@Cancelable
public class PlayerDropsEvent extends LivingDropsEvent {
    public final PlayerEntity entityPlayer;

    public PlayerDropsEvent(PlayerEntity entity, DamageSource source, ArrayList<ItemEntity> drops, boolean recentlyHit) {
        super(
                entity,
                source,
                drops,
                source.getAttacker() instanceof PlayerEntity ? EnchantmentHelper.method_4655((PlayerEntity)source.getAttacker()) : 0,
                recentlyHit,
                0
        );
        this.entityPlayer = entity;
    }
}
