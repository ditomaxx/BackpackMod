package com.ditomax.util.gui;

import com.ditomax.item.BackpackItem;
import com.ditomax.util.BackpackManager;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.UUID;

public class BackpackScreenHandler extends ScreenHandler {

    private final Inventory backpackInventory;
    private final UUID backpackOwnerUUID;

    // Server constructor
    public BackpackScreenHandler(int syncId, PlayerInventory playerInventory, Inventory backpackInventory, PlayerEntity backpackOwner) {
        super(ModScreenHandlers.BACKPACK_SCREEN_HANDLER, syncId);

        this.backpackInventory = backpackInventory;
        this.backpackOwnerUUID = backpackOwner.getUuid();

        checkSize(backpackInventory, 27);

        // BACKPACK-SLOTS
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(backpackInventory, row * 9 + col, 8 + col * 18, 18 + row * 18));
            }
        }

        // INVENTAR-SLOTS
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, 9 + row * 9 + col, 8 + col * 18, 86 + row * 18));
            }
        }

        // HOTBAR
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 144));
        }
    }

    // Client constructor
    public BackpackScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ModScreenHandlers.BACKPACK_SCREEN_HANDLER, syncId);

        this.backpackInventory = new BackpackInventory();
        this.backpackOwnerUUID = playerInventory.player.getUuid();

        checkSize(this.backpackInventory, 27);

        // BACKPACK-SLOTS
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(backpackInventory, row * 9 + col, 8 + col * 18, 18 + row * 18));
            }
        }

        // INVENTAR-SLOTS
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, 9 + row * 9 + col, 8 + col * 18, 86 + row * 18));
            }
        }

        // HOTBAR
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 144));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        Slot sourceSlot = this.slots.get(slot);

        if (!sourceSlot.hasStack()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getStack();
        ItemStack originalStack = sourceStack.copy();

        if (slot < 27) {
            if (!this.insertItem(sourceStack, 27, 63, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.insertItem(sourceStack, 0, 27, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.setStack(ItemStack.EMPTY);
        } else {
            sourceSlot.markDirty();
        }

        return originalStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        if (this.backpackInventory == null || this.backpackOwnerUUID == null) {
            return false;
        }

        PlayerEntity owner = player.getWorld().getPlayerByUuid(backpackOwnerUUID);
        if (owner == null) {
            return false;
        }
        if (player.getUuid().equals(backpackOwnerUUID) && hasBackpackInInventory(player)) {
            return true;
        }

        boolean ownerHasBackpack = hasBackpackEquipped(owner);

        return ownerHasBackpack && owner.squaredDistanceTo(player) <= 32.0 * 32.0;
    }

    private boolean hasBackpackEquipped(PlayerEntity player) {
        var capability = AccessoriesCapability.get(player);
        if (capability == null) return false;

        for (var container : capability.getContainers().values()) {
            for (int i = 0; i < container.getSize(); i++) {
                ItemStack stack = container.getAccessories().getStack(i);
                if (stack.getItem() instanceof BackpackItem) {
                    return true;
                }
            }
        }

        return false;
    }


    private boolean hasBackpackInInventory(PlayerEntity player) {
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() instanceof BackpackItem) {
                return true;
            }
        }

        for (ItemStack stack : player.getInventory().offHand) {
            if (stack.getItem() instanceof BackpackItem) {
                return true;
            }
        }

        for (ItemStack stack : player.getInventory().armor) {
            if (stack.getItem() instanceof BackpackItem) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if (!player.getWorld().isClient()) {
            this.backpackInventory.markDirty();
        }
        BackpackManager.removeInventory(player.getUuid());
    }
}