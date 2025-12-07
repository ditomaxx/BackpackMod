package com.ditomax;

import com.ditomax.item.ModItemGroups;
import com.ditomax.item.ModItems;
import com.ditomax.util.BackpackInteractions;
import com.ditomax.util.OpenBackpackReceiver;
import com.ditomax.util.gui.ModScreenHandlers;
import com.ditomax.util.payload.OpenBackpackPayload;
import io.wispforest.accessories.Accessories;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.AccessoryRegistry;
import io.wispforest.accessories.api.menu.AccessoriesSlotGenerator;
import io.wispforest.accessories.compat.config.SlotAmountModifier;
import io.wispforest.accessories.menu.SlotAccessContainer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackpackMod implements ModInitializer {
    public static final String MOD_ID = "backpackmod";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.initialize();
        ModItemGroups.registerItemGroups();
        ModScreenHandlers.initialize();
        BackpackInteractions.register();
        PayloadTypeRegistry.playC2S().register(OpenBackpackPayload.ID, OpenBackpackPayload.CODEC);
        OpenBackpackReceiver.register();

        Accessories.config().clientOptions.showCosmeticAccessories(false);
        Accessories.config().screenOptions.showUnusedSlots(false);
    }
}