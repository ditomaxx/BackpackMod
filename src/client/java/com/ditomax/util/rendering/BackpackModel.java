package com.ditomax.util.rendering;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class BackpackModel extends Model {

    // Walking/Running Animation Constants - Verbesserte Werte für mehr Realismus
    private static final float WALK_BOUNCE_INTENSITY = 0.035F;    // Etwas reduziert für natürlicheren Bounce
    private static final float WALK_SWAY_INTENSITY = 0.025F;      // Subtileres Schwanken
    private static final float WALK_TILT_INTENSITY = 0.022F;      // Leicht erhöht für bessere Sichtbarkeit
    private static final float WALK_ROLL_INTENSITY = 0.018F;      // Mehr Roll für Gewichtseffekt

    // Speed Multipliers
    private static final float SPRINT_MULTIPLIER = 1.6F;          // Etwas reduziert für bessere Balance
    private static final float CROUCH_MULTIPLIER = 0.5F;          // Noch langsamere Animationen beim Schleichen

    // Idle Animation Constants
    private static final float BREATHING_INTENSITY = 0.012F;      // Subtilere Atmung
    private static final float BREATHING_SPEED = 0.067F;
    private static final float IDLE_SWAY = 0.008F;                // NEU: Minimales Schwanken im Stand

    // Gliding Animation Constants
    private static final float GLIDE_TILT_INTENSITY = 1.35F;      // Etwas mehr für aerodynamischen Look
    private static final float GLIDE_SWAY_INTENSITY = 0.06F;
    private static final float GLIDE_FLUTTER = 0.025F;            // NEU: Flattern durch Luftwiderstand

    // Swimming Constants - NEU
    private static final float SWIM_WAVE_INTENSITY = 0.035F;      // Wellen-Bewegung
    private static final float SWIM_BOBBING = 0.025F;             // Auf-und-Ab im Wasser

    // Crouch Constants - NEU
    private static final float CROUCH_FORWARD_TILT = 0.12F;       // Vorwärtsneigung
    private static final float CROUCH_LOWER = 2.5F;               // Tiefere Position

    // Sprint Constants - NEU
    private static final float SPRINT_LEAN = 0.08F;               // Extra Vorwärtsneigung
    private static final float SPRINT_BOUNCE_EXTRA = 0.015F;      // Zusätzlicher Bounce

    // Movement Threshold
    private static final float MOVEMENT_THRESHOLD = 0.01F;

    // Smooth Transitions - NEU
    private static final float SMOOTHING_FACTOR = 0.2F;           // Trägheit für weiche Übergänge

    private final ModelPart bb_main;

    // State tracking für smooth transitions - NEU
    private float lastPitch = 0.0F;
    private float lastYaw = 0.0F;
    private float lastRoll = 0.0F;
    private float lastY = 24.0F;

    public BackpackModel(ModelPart root, Function<Identifier, RenderLayer> layerFactory) {
        super(root, layerFactory);
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

    /**
     * Updates the backpack model's animation based on the player's render state.
     * This method is called from the FeatureRenderer to animate the backpack.
     */
    public void setAngles(PlayerEntityRenderState state) {
        // Reset to default position
        resetToDefaultPose();

        // Determine movement state and apply appropriate multiplier
        float speedMultiplier = getSpeedMultiplier(state);
        boolean isSprinting = state.limbAmplitudeMultiplier > 0.6F;

        // Apply walking/running animations if player is moving
        if (state.limbAmplitudeMultiplier > MOVEMENT_THRESHOLD) {
            applyWalkingAnimation(state.limbFrequency, state.limbAmplitudeMultiplier, speedMultiplier, isSprinting);
        } else {
            // Apply idle breathing animation when standing still
            applyIdleAnimation(state.age);
        }

        // Apply pose-specific adjustments
        if (state.pose == EntityPose.SWIMMING) {
            applySwimmingAdjustment(state);
        } else if (state.pose == EntityPose.GLIDING) {
            applyGlidingAdjustment(state);
        }

        // Apply smooth transitions für flüssigere Animationen
        applySmoothTransitions();
    }

    /**
     * Resets the backpack to its default position and rotation.
     */
    private void resetToDefaultPose() {
        this.bb_main.pitch = 0.0F;
        this.bb_main.yaw = 0.0F;
        this.bb_main.roll = 0.0F;
        this.bb_main.pivotY = 24.0F;
    }

    /**
     * Calculates the speed multiplier based on player state.
     * Sprinting = increased animations, Crouching = reduced animations
     */
    private float getSpeedMultiplier(PlayerEntityRenderState state) {
        if (state.pose == EntityPose.CROUCHING) {
            return CROUCH_MULTIPLIER;
        }
        // Sprint detection: limbAmplitudeMultiplier > 0.6 indicates running
        return state.limbAmplitudeMultiplier > 0.6F ? SPRINT_MULTIPLIER : 1.0F;
    }

    /**
     * Applies realistic walking animation with multiple movement components.
     * Verbessert mit Sprint-Effekten und sekundärem Bounce.
     */
    private void applyWalkingAnimation(float limbFrequency, float limbAmplitude, float speedMultiplier, boolean isSprinting) {
        // Primary walking cycle frequency
        float walkCycle = limbFrequency * 0.6662F;

        // 1. VERTICAL BOUNCE - Backpack bounces up and down with each step
        float bounceAmount = MathHelper.sin(walkCycle) * limbAmplitude;
        float bounce = bounceAmount * WALK_BOUNCE_INTENSITY * speedMultiplier;
        this.bb_main.pivotY += bounce * 12.0F;

        // 2. FORWARD/BACKWARD TILT (Pitch) - Backpack tilts with leg movement
        float tiltAmount = MathHelper.sin(walkCycle) * limbAmplitude;
        this.bb_main.pitch = tiltAmount * WALK_TILT_INTENSITY * speedMultiplier;

        // 3. SIDE-TO-SIDE SWAY (Yaw) - Backpack sways horizontally
        float swayAmount = MathHelper.cos(walkCycle * 0.5F) * limbAmplitude;
        this.bb_main.yaw = swayAmount * WALK_SWAY_INTENSITY * speedMultiplier;

        // 4. ROLLING MOTION (Roll) - Backpack rolls with weight shifts
        float rollAmount = MathHelper.sin(walkCycle * 0.5F) * limbAmplitude;
        this.bb_main.roll = rollAmount * WALK_ROLL_INTENSITY * speedMultiplier;

        // 5. SECONDARY BOUNCE - Adds a second harmonic for more realistic motion
        float secondaryBounce = MathHelper.sin(limbFrequency * 1.3324F) * limbAmplitude * 0.25F;
        this.bb_main.pivotY += secondaryBounce * WALK_BOUNCE_INTENSITY * 6.0F * speedMultiplier;

        // 6. SPRINT EFFECTS - Zusätzliche Effekte beim Sprinten
        if (isSprinting) {
            // Extra Vorwärtsneigung
            this.bb_main.pitch += SPRINT_LEAN;

            // Stärkerer, schnellerer Bounce
            float sprintBounce = MathHelper.sin(walkCycle * 1.5F) * limbAmplitude * SPRINT_BOUNCE_EXTRA;
            this.bb_main.pivotY += sprintBounce * 8.0F;

            // Intensiveres Schwanken
            float sprintSway = MathHelper.sin(limbFrequency * 1.3F) * limbAmplitude * 0.015F;
            this.bb_main.yaw += sprintSway;
        }
    }

    /**
     * Applies subtle breathing animation when player is standing still.
     * Verbessert mit minimalem Idle-Schwanken für mehr Lebendigkeit.
     */
    private void applyIdleAnimation(float age) {
        // Atmung
        float breathing = MathHelper.sin(age * BREATHING_SPEED) * BREATHING_INTENSITY;
        this.bb_main.pitch = breathing;
        this.bb_main.pivotY += breathing * 6.0F;

        // Minimales Idle-Schwanken für natürlicheren Look
        float idleSway = MathHelper.sin(age * 0.03F) * IDLE_SWAY;
        this.bb_main.yaw += idleSway;

        // Sehr subtile Roll-Bewegung
        float idleRoll = MathHelper.cos(age * 0.04F) * IDLE_SWAY * 0.5F;
        this.bb_main.roll += idleRoll;
    }

    /**
     * Adjusts backpack position when player is swimming.
     * Verbessert mit Wasserwiderstand-Effekten und Wellen-Bewegung.
     */
    private void applySwimmingAdjustment(PlayerEntityRenderState state) {
        // NUR subtile Wellen-Bewegung, keine große Pitch-Änderung mehr
        // da der Körper bereits horizontal ist durch body.rotate()

        // Wellen-Bewegung durch Wasserwiderstand
        float swimWave = MathHelper.sin(state.age * 0.3F) * SWIM_WAVE_INTENSITY;
        this.bb_main.pitch += swimWave * 0.3F;  // Reduziert!
        this.bb_main.yaw += swimWave * 0.5F;

        // Auf-und-Ab-Bewegung im Wasser
        float bobbing = MathHelper.cos(state.age * 0.2F) * SWIM_BOBBING;
        this.bb_main.pivotY += bobbing * 8.0F;

        // Leichte Roll-Bewegung durch Wasserbewegung
        float waterRoll = MathHelper.sin(state.age * 0.18F) * 0.02F;
        this.bb_main.roll += waterRoll;
    }

    /**
     * Adjusts backpack position when player is gliding with elytra.
     * Verbessert mit Luftwiderstand und Flattern-Effekten.
     */
    private void applyGlidingAdjustment(PlayerEntityRenderState state) {
        // Aerodynamische Position
        this.bb_main.pitch += GLIDE_TILT_INTENSITY;

        // Gliding Progress für smoothe Übergänge
        float glidingProgress = state.getGlidingProgress();

        // Luftwiderstand-Schwanken
        float sway = MathHelper.sin(state.age * 0.25F) * GLIDE_SWAY_INTENSITY * glidingProgress;
        this.bb_main.yaw += sway;

        // Flattern durch Windwiderstand
        float flutter = MathHelper.sin(state.age * 0.8F) * GLIDE_FLUTTER * glidingProgress;
        this.bb_main.roll += flutter;
        this.bb_main.pitch += flutter * 0.3F;

        // Höhenänderungs-Effekt
        float altitude = MathHelper.cos(state.age * 0.12F) * 0.02F * glidingProgress;
        this.bb_main.pivotY += altitude * 6.0F;
    }

    /**
     * NEU: Applies smooth transitions between animation states für flüssigere Bewegungen.
     * Verwendet Interpolation um ruckartige Übergänge zu vermeiden.
     */
    private void applySmoothTransitions() {
        // Speichere aktuelle Zielwerte
        float targetPitch = this.bb_main.pitch;
        float targetYaw = this.bb_main.yaw;
        float targetRoll = this.bb_main.roll;
        float targetY = this.bb_main.pivotY;

        // Interpoliere zwischen alten und neuen Werten
        this.bb_main.pitch = MathHelper.lerp(SMOOTHING_FACTOR, lastPitch, targetPitch);
        this.bb_main.yaw = MathHelper.lerp(SMOOTHING_FACTOR, lastYaw, targetYaw);
        this.bb_main.roll = MathHelper.lerp(SMOOTHING_FACTOR, lastRoll, targetRoll);
        this.bb_main.pivotY = MathHelper.lerp(SMOOTHING_FACTOR, lastY, targetY);

        // Update last values für nächsten Frame
        lastPitch = this.bb_main.pitch;
        lastYaw = this.bb_main.yaw;
        lastRoll = this.bb_main.roll;
        lastY = this.bb_main.pivotY;
    }
}