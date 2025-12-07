package com.ditomax.util.gui;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {

    public static ScreenHandlerType<BackpackScreenHandler> BACKPACK_SCREEN_HANDLER;

    public static void initialize() {
        // ✅ ExtendedScreenHandlerType mit PacketCodec für UUID-Übertragung
        BACKPACK_SCREEN_HANDLER = Registry.register(
                Registries.SCREEN_HANDLER,
                Identifier.of("backpackmod", "backpack"),
                new ExtendedScreenHandlerType<>(
                        // Client-Constructor mit übertragenem Data-Objekt
                        (syncId, playerInventory, data) ->
                                new BackpackScreenHandler(syncId, playerInventory, data.ownerUUID()),
                        BackpackScreenData.CODEC
                )
        );
    }
}