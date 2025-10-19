package com.ditomax.util.gui;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class BackpackInventory implements Inventory {
    private final ItemStack backpackStack;
    private final DefaultedList<ItemStack> items;

    //Constructor für Server
    public BackpackInventory(ItemStack backpackStack) {
        this.backpackStack = backpackStack;
        this.items = DefaultedList.ofSize(27, ItemStack.EMPTY);

        ContainerComponent container = backpackStack.getOrDefault(
                DataComponentTypes.CONTAINER,
                ContainerComponent.DEFAULT
        );

        List<ItemStack> stackList = container.stream().toList();
        for (int i = 0; i < Math.min(stackList.size(), 27); i++) {
            this.items.set(i, stackList.get(i));
        }
    }

    // Constructor für Client
    public BackpackInventory() {
        this.backpackStack = ItemStack.EMPTY;
        this.items = DefaultedList.ofSize(27, ItemStack.EMPTY);
    }

    @Override
    public int size() {
        return 27;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(items, slot, amount);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = Inventories.removeStack(items, slot);
        markDirty();
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        markDirty();
    }

    @Override
    public void markDirty() {
        if (!backpackStack.isEmpty()) {
            backpackStack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(items));
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        items.clear();
        markDirty();
    }
}