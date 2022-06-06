package fr.catcore.fabricatedforge.mixininterface;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public interface IEntity {
    public NbtCompound getEntityData();

    public boolean shouldRiderSit();

    public ItemStack getPickedResult(BlockHitResult target);

    public UUID getPersistentID();

    public void generatePersistentID();

    // Non Forge APIs
    void captureDrops(boolean captureDrops);
    boolean captureDrops();

    ArrayList<ItemEntity> getCapturedDrops();

    // Non Forge APIs
    Random getRandom();
}
