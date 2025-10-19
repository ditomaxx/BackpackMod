package com.ditomax.util;

import com.ditomax.BackpackMod;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {

    public static class Items {
        public static final TagKey<Item> BACKPACK_ITEMS = createTag("backpack_items");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(BackpackMod.MOD_ID, name));
        }
    }
}
