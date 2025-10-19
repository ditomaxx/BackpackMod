package com.ditomax;

import com.ditomax.util.BackpackScreen;
import com.ditomax.util.ModKeybinds;
import com.ditomax.util.gui.ModScreenHandlers;
import com.ditomax.util.rendering.BackpackFeatureRenderer;
import com.ditomax.util.rendering.BackpackModel;
import com.ditomax.util.rendering.ModModelLayers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;

public class BackpackModClient implements ClientModInitializer {

    public static KeyBinding OPEN_BACKPACK_KEY;

    @Override
    public void onInitializeClient() {
        OPEN_BACKPACK_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.backpackmod.open_backpack",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "category.backpack.controls"
        ));

        HandledScreens.register(ModScreenHandlers.BACKPACK_SCREEN_HANDLER, BackpackScreen::new);
        ModKeybinds.register();

        EntityModelLayerRegistry.registerModelLayer(
                ModModelLayers.BACKPACK,
                BackpackModel::getTexturedModelData
        );

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(
                (entityType, entityRenderer, registrationHelper, context) -> {
                    if (entityRenderer instanceof PlayerEntityRenderer) {
                        BackpackModel model = new BackpackModel(
                                context.getPart(ModModelLayers.BACKPACK)
                        );

                        @SuppressWarnings("unchecked")
                        FeatureRendererContext<PlayerEntity, PlayerEntityModel<PlayerEntity>> playerContext =
                                (FeatureRendererContext<PlayerEntity, PlayerEntityModel<PlayerEntity>>) entityRenderer;

                        registrationHelper.register(
                                new BackpackFeatureRenderer(playerContext, model)
                        );
                    }
                }
        );
    }
}