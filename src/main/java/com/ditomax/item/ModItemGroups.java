package com.ditomax.item;

import com.ditomax.BackpackMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup BACKPACK_ITEMS = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(BackpackMod.MOD_ID, "backpack_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.BACKPACK))
                    .displayName(Text.translatable("itemgroup.backpackmod.backpack_items"))
                    .entries(((displayContext, entries) ->
                            entries.add(ModItems.BACKPACK))).build());

    public static void registerItemGroups() {
        BackpackMod.LOGGER.info("Registering Item Groups for " + BackpackMod.MOD_ID);
    }
}
