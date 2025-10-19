package com.ditomax.util.rendering;

import com.ditomax.item.BackpackItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BackpackFeatureRenderer extends FeatureRenderer<PlayerEntity, PlayerEntityModel<PlayerEntity>> {

    private static final Identifier TEXTURE = Identifier.of("backpackmod", "textures/entity/backpack_entity.png");
    private static final double MAX_RENDER_DISTANCE_SQ = 32.0 * 32.0;

    private final BackpackModel backpackModel;

    public BackpackFeatureRenderer(
            FeatureRendererContext<PlayerEntity, PlayerEntityModel<PlayerEntity>> context,
            BackpackModel model) {
        super(context);
        this.backpackModel = model;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                       PlayerEntity entity, float limbAngle, float limbDistance, float tickDelta,
                       float animationProgress, float headYaw, float headPitch) {

        boolean hasBackpack = TrinketsApi.getTrinketComponent(entity)
                .map(comp -> comp.isEquipped(stack -> stack.getItem() instanceof BackpackItem))
                .orElse(false);

        if (!hasBackpack) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();

        if (entity == client.player && client.options.getPerspective().isFirstPerson()) {
            return;
        }

        if (entity.squaredDistanceTo(client.gameRenderer.getCamera().getPos()) > MAX_RENDER_DISTANCE_SQ) {
            return;
        }

        matrices.push();

        try {
            PlayerEntityModel<PlayerEntity> playerModel = this.getContextModel();
            playerModel.body.rotate(matrices);

            matrices.translate(0.0, -0.45, 0.125);
            matrices.scale(0.8F, 0.8F, 0.8F);

            if (playerModel.child) {
                matrices.scale(0.625F, 0.625F, 0.625F);
                matrices.translate(0.0, 1.2, 0.0);
            }

            playerModel.copyStateTo(this.backpackModel);
            this.backpackModel.setAngles(entity, limbAngle, limbDistance,
                    animationProgress, headYaw, headPitch);

            var vertexConsumer = vertexConsumers.getBuffer(
                    RenderLayer.getEntityCutoutNoCull(TEXTURE)
            );

            this.backpackModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);

        } finally {
            matrices.pop();
        }
    }
}