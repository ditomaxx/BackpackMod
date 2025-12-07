package com.ditomax.util.gui;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

/**
 * Payload-Objekt für die Übertragung der Owner-UUID zum Client
 * Wird beim Öffnen des Backpack-Screens verwendet
 */
public record BackpackScreenData(UUID ownerUUID) {
    public static final Identifier ID = Identifier.of("backpackmod", "backpack_data");

    public static final PacketCodec<RegistryByteBuf, BackpackScreenData> CODEC =
            PacketCodec.tuple(
                    Uuids.PACKET_CODEC, BackpackScreenData::ownerUUID,
                    BackpackScreenData::new
            );
}