package com.ditomax.item;

import com.ditomax.BackpackMod;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public class ModItems {

    public static final Item BACKPACK = registerItem("backpack", settings -> new BackpackItem(settings
            .maxCount(1)
            .rarity(Rarity.RARE)
            .fireproof()
            .component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true))));

    public static void initialize() {
        BackpackMod.LOGGER.info("Registering Mod Items for " + BackpackMod.MOD_ID);
    }

    private static Item registerItem(String name, Function<Item.Settings, Item> function) {
        return Registry.register(Registries.ITEM, Identifier.of(BackpackMod.MOD_ID, name),
                function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(BackpackMod.MOD_ID, name)))));
    }
}
