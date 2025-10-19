package com.ditomax.util;

import com.ditomax.BackpackModClient;
import com.ditomax.util.payload.OpenBackpackPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ModKeybinds {

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (BackpackModClient.OPEN_BACKPACK_KEY.wasPressed()) {
                ClientPlayNetworking.send(new OpenBackpackPayload());
            }
        });
    }
}
