package com.ditomax.util.rendering;

import com.ditomax.item.BackpackItem;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BackpackFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {

    private static final Identifier TEXTURE = Identifier.of("backpackmod", "textures/entity/backpack_entity.png");
    private static final double MAX_RENDER_DISTANCE_SQ = 64.0 * 64.0;
    private final BackpackModel backpackModel;

    public BackpackFeatureRenderer(
            FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context,
            ModelPart backpackModelPart) {
        super(context);
        this.backpackModel = new BackpackModel(backpackModelPart, RenderLayer::getEntityCutoutNoCull);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                       PlayerEntityRenderState state, float limbAngle, float limbDistance) {

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) {
            return;
        }

        PlayerEntity player = client.world.getPlayers().stream()
                .filter(p -> p.getId() == state.id)
                .findFirst()
                .orElse(null);

        if (player == null) {
            return;
        }

        AccessoriesCapability cap = AccessoriesCapability.get(player);
        if (cap == null || !cap.isEquipped(stack -> stack.getItem() instanceof BackpackItem)) {
            return;
        }

        if (player == client.player && client.options.getPerspective().isFirstPerson()) {
            return;
        }

        double distanceSq = player.squaredDistanceTo(client.gameRenderer.getCamera().getPos());
        if (distanceSq > MAX_RENDER_DISTANCE_SQ) {
            return;
        }

        matrices.push();

        try {
            PlayerEntityModel model = this.getContextModel();

            model.body.rotate(matrices);

            matrices.translate(0.0, -0.45, 0.125);

            if (state.pose == EntityPose.SWIMMING) {
                matrices.translate(0.0, 0.0, 0.08);
            }

            matrices.scale(0.8F, 0.8F, 0.8F);

            if (state.baby) {
                matrices.scale(0.625F, 0.625F, 0.625F);
                matrices.translate(0.0, 1.2, 0.0);
            }

            this.backpackModel.setAngles(state);

            var vertexConsumer = vertexConsumers.getBuffer(
                    RenderLayer.getEntityCutoutNoCull(TEXTURE)
            );

            this.backpackModel.render(
                    matrices,
                    vertexConsumer,
                    light,
                    OverlayTexture.DEFAULT_UV,
                    0xFFFFFFFF
            );

        } finally {
            matrices.pop();
        }
    }
}