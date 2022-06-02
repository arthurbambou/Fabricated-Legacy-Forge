package net.minecraftforge.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;

public class ForgeInternalHandler {
    public ForgeInternalHandler() {
    }

    @ForgeSubscribe(
            priority = EventPriority.HIGHEST
    )
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.world.isClient) {
            if (event.entity.getPersistentID() == null) {
                event.entity.generatePersistentID();
            } else {
                ForgeChunkManager.loadEntity(event.entity);
            }
        }

        Entity entity = event.entity;
        if (entity.getClass().equals(ItemEntity.class)) {
            ItemStack item = ((ItemEntity)entity).field_23087;
            if (item != null && item.getItem().hasCustomEntity(item)) {
                Entity newEntity = item.getItem().createEntity(event.world, entity, item);
                if (newEntity != null) {
                    entity.remove();
                    event.setCanceled(true);
                    event.world.spawnEntity(newEntity);
                }
            }
        }

    }

    @ForgeSubscribe(
            priority = EventPriority.HIGHEST
    )
    public void onDimensionLoad(Load event) {
        ForgeChunkManager.loadWorld(event.world);
    }

    @ForgeSubscribe(
            priority = EventPriority.HIGHEST
    )
    public void onDimensionSave(Save event) {
        ForgeChunkManager.saveWorld(event.world);
    }
}
