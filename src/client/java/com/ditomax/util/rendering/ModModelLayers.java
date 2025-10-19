package com.ditomax.util.rendering;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModModelLayers {

    public static final EntityModelLayer BACKPACK = new EntityModelLayer(
            Identifier.of("backpackmod", "backpack"),
            "main"
    );
}