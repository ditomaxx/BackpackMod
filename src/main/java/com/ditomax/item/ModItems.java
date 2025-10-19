package com.ditomax.item;

import com.ditomax.BackpackMod;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static Item BACKPACK = new BackpackItem();

    public static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(BackpackMod.MOD_ID, name), item);
    }

    public static void registerItems() {
        registerItem("backpack", BACKPACK);
    }
}
