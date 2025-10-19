package com.ditomax;

import com.ditomax.item.ModItemGroups;
import com.ditomax.item.ModItems;
import com.ditomax.util.OpenBackpackReceiver;
import com.ditomax.util.gui.ModScreenHandlers;
import com.ditomax.util.payload.OpenBackpackPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackpackMod implements ModInitializer {
    public static final String MOD_ID = "backpackmod";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.registerItems();
        ModItemGroups.registerItemGroups();
        ModScreenHandlers.initialize();

        PayloadTypeRegistry.playC2S().register(OpenBackpackPayload.ID, OpenBackpackPayload.CODEC);
        OpenBackpackReceiver.register();
    }
}