package com.ditomax.util.payload;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OpenBackpackPayload() implements CustomPayload {

    public static final Id<OpenBackpackPayload> ID = new Id<>(
            Identifier.of("backpackmod", "backpack_open")
    );

    public static final PacketCodec<PacketByteBuf, OpenBackpackPayload> CODEC =
            PacketCodec.unit(new OpenBackpackPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
