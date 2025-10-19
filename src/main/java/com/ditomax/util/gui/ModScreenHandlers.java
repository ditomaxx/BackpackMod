package com.ditomax.util.gui;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {

    public static ScreenHandlerType<BackpackScreenHandler> BACKPACK_SCREEN_HANDLER;

    public static void initialize() {
        BACKPACK_SCREEN_HANDLER = Registry.register(
                Registries.SCREEN_HANDLER,
                Identifier.of("backpackmod", "backpack"),
                new ScreenHandlerType<>(BackpackScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
        );
    }
}
