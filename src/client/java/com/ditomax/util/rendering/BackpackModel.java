package com.ditomax.util.rendering;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BackpackModel extends EntityModel<PlayerEntity> {

    private static final float WALK_BOUNCE_INTENSITY = 0.04F;    // Wie stark wippt der Rucksack
    private static final float WALK_SWAY_INTENSITY = 0.03F;      // Seitliches Schwingen
    private static final float WALK_TILT_INTENSITY = 0.02F;      // Vor/Zurück kippen
    private static final float SPRINT_MULTIPLIER = 1.5F;         // Verstärkung beim Sprinten
    private static final float BREATHING_INTENSITY = 0.02F;      // Atmung im Stand

    private static final float JUMP_ASCENT_INTENSITY = 0.2F;     // Wie stark beim Hochspringen
    private static final float JUMP_FALL_INTENSITY = 0.1F;       // Wie stark beim Fallen

    private final ModelPart root;
    private final ModelPart bb_main;

    public BackpackModel(ModelPart root) {
        this.root = root;
        this.bb_main = root.getChild("bb_main");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData bb_main = modelPartData.addChild(
                "bb_main",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-6.0F, -14.0F, -1.0F, 12.0F, 14.0F, 5.0F, new Dilation(0.0F))
                        .uv(0, 19).cuboid(-5.0F, -8.0F, 4.0F, 10.0F, 7.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F)
        );

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(PlayerEntity entity, float limbAngle, float limbDistance, float ageInTicks, float netHeadYaw, float headPitch) {
        this.bb_main.pitch = 0.0F;
        this.bb_main.yaw = 0.0F;
        this.bb_main.roll = 0.0F;
        this.bb_main.pivotY = 24.0F;

        if (limbDistance > 0.01F) {
            float speedMultiplier = limbDistance > 0.6F ? SPRINT_MULTIPLIER : 1.0F;

            // 1. AUF/AB BEWEGUNG (Bounce)
            // Der Rucksack wippt auf und ab beim Laufen
            float bounceAmount = MathHelper.sin(limbAngle * 0.6662F) * limbDistance;
            float bounce = bounceAmount * WALK_BOUNCE_INTENSITY * speedMultiplier;
            this.bb_main.pivotY += bounce * 10.0F; // Multipliziert mit 10 für sichtbare Bewegung

            // 2. VOR/ZURÜCK KIPPEN (Pitch)
            // Rucksack kippt nach vorne beim Vorwärtsgehen des Beins
            float tiltAmount = MathHelper.sin(limbAngle * 0.6662F) * limbDistance;
            this.bb_main.pitch = tiltAmount * WALK_TILT_INTENSITY * speedMultiplier;

            // 3. SEITLICHES SCHWINGEN (Yaw)
            // Rucksack schwingt leicht von Seite zu Seite
            float swayAmount = MathHelper.cos(limbAngle * 0.6662F) * limbDistance;
            this.bb_main.yaw = swayAmount * WALK_SWAY_INTENSITY * speedMultiplier;

            // 4. SEITLICHES ROLLEN (Roll) - für extra Realismus
            // Rucksack rollt leicht beim Gewichtswechsel
            float rollAmount = MathHelper.sin(limbAngle * 0.6662F * 0.5F) * limbDistance;
            this.bb_main.roll = rollAmount * WALK_SWAY_INTENSITY * 0.5F * speedMultiplier;

            // 5. VERTIKALE OSZILLATION (zweite Harmonische für Realismus)
            // Fügt eine zusätzliche, schnellere Auf/Ab-Bewegung hinzu
            float secondaryBounce = MathHelper.sin(limbAngle * 1.3324F) * limbDistance * 0.3F;
            this.bb_main.pivotY += secondaryBounce * WALK_BOUNCE_INTENSITY * 5.0F * speedMultiplier;
        } else {
            // Subtile Atmungs-Animation wenn der Spieler still steht
            float breathing = MathHelper.sin(ageInTicks * 0.067F) * BREATHING_INTENSITY;
            this.bb_main.pitch = breathing;
            this.bb_main.pivotY += breathing * 5.0F;
        }

        if (!entity.isOnGround()) {
            double verticalVelocity = entity.getVelocity().y;

            // AUFSTIEG (Springen nach oben)
            if (verticalVelocity > 0.1) {
                // Rucksack bleibt zurück beim Hochspringen (Trägheit)
                float jumpDelay = (float) verticalVelocity * JUMP_ASCENT_INTENSITY;
                this.bb_main.pivotY += jumpDelay * 8.0F;  // Stark nach "unten" bewegen (visuell bleibt er zurück)
            }
            // FALL (Fallen nach unten)
            else if (verticalVelocity < -0.1) {
                // Rucksack "schwebt" nach oben beim Fallen (Luftwiderstand)
                float fallFloat = (float) Math.abs(verticalVelocity) * JUMP_FALL_INTENSITY;
                this.bb_main.pivotY -= fallFloat * 6.0F;  // Nach oben bewegen
            }
            // SCHWEBEPHASE (am höchsten Punkt)
            else {
                float floatOscillation = MathHelper.sin(ageInTicks * 0.3F) * 0.15F;
                this.bb_main.pivotY -= floatOscillation * 4.0F;
            }
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        bb_main.render(matrices, vertices, light, overlay, color);
    }
}