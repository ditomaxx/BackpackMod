package com.ditomax.util.gui;

import com.ditomax.item.BackpackItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class BackpackScreenHandler extends ScreenHandler {

    private final Inventory backpackInventory;
    private final PlayerEntity backpackOwner;

    // Server constructor
    public BackpackScreenHandler(int syncId, PlayerInventory playerInventory, Inventory backpackInventory, PlayerEntity backpackOwner) {
        super(ModScreenHandlers.BACKPACK_SCREEN_HANDLER, syncId);

        this.backpackInventory = backpackInventory;
        this.backpackOwner = backpackOwner;

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
                this.addSlot(new Slot(playerInventory, 9 + row * 9 + col, 8 + col * 18, 85 + row * 18));
            }
        }

        // HOTBAR
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 143));
        }
    }

    // Client constructor
    public BackpackScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ModScreenHandlers.BACKPACK_SCREEN_HANDLER, syncId);

        this.backpackInventory = new BackpackInventory();
        this.backpackOwner = playerInventory.player;

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
                this.addSlot(new Slot(playerInventory, 9 + row * 9 + col, 8 + col * 18, 85 + row * 18));
            }
        }

        // HOTBAR
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 143));
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
        if (this.backpackInventory == null || this.backpackOwner == null) {
            return false;
        }

        // Prüfe ob der Owner noch ein Backpack trägt
        boolean hasBackpack = TrinketsApi.getTrinketComponent(backpackOwner)
                .map(comp -> comp.isEquipped(stack -> stack.getItem() instanceof BackpackItem))
                .orElse(false);

        return hasBackpack || hasBackpack(player);
    }

    private boolean hasBackpack(PlayerEntity player) {
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
        // Finale Speicherung
        if (!player.getWorld().isClient()) {
            this.backpackInventory.markDirty();
        }
    }
}