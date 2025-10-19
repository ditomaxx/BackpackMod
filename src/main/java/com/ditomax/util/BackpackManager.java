package com.ditomax.util;

import com.ditomax.util.gui.BackpackInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackpackManager {
    private static final Map<UUID, BackpackInventory> openBackpacks = new HashMap<>();

    public static BackpackInventory getOrCreateInventory(PlayerEntity owner, ItemStack backpackStack) {
        UUID ownerUUID = owner.getUuid();

        BackpackInventory inventory = openBackpacks.get(ownerUUID);
        if (inventory == null) {
            inventory = new BackpackInventory(backpackStack);
            openBackpacks.put(ownerUUID, inventory);
        }

        return inventory;
    }
}