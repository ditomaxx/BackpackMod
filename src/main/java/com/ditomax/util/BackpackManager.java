package com.ditomax.util;

import com.ditomax.util.gui.BackpackInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BackpackManager {
    public static final Map<UUID, BackpackInventory> openBackpacks = new ConcurrentHashMap<>();

    public static BackpackInventory getOrCreateInventory(PlayerEntity owner, ItemStack backpackStack) {
        UUID ownerUUID = owner.getUuid();

        return openBackpacks.computeIfAbsent(ownerUUID, uuid -> new BackpackInventory(backpackStack));
    }

    public static void removeInventory(UUID ownerUUID) {
        BackpackInventory inventory = openBackpacks.remove(ownerUUID);
        if (inventory != null) {
            inventory.markDirty();
        }
    }
}