package com.ditomax.util;

import com.ditomax.item.BackpackItem;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BackpackInteractions {

    // Definiere die Backpack Bounding Box (relativ zum Spieler)
    private static final double BACKPACK_MIN_Y = 0.3;   // Untere Grenze (auf dem Rücken)
    private static final double BACKPACK_MAX_Y = 1.2;   // Obere Grenze (Schulterbereich)
    private static final double BACKPACK_MIN_Z = -0.4;  // Hinter dem Spieler (Rücken)
    private static final double BACKPACK_MAX_Z = -0.1;  // Nicht zu weit hinten
    private static final double BACKPACK_WIDTH = 0.4;   // Breite des Backpacks

    public static void register() {
        UseEntityCallback.EVENT.register((PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) -> {
            if (world.isClient() || hand != Hand.MAIN_HAND) {
                return ActionResult.PASS;
            }

            if (!(entity instanceof PlayerEntity targetPlayer)) {
                return ActionResult.PASS;
            }

            // Prüfe ob der Spieler ein Backpack trägt
            ItemStack backpackStack = findBackpack(targetPlayer);
            if (backpackStack.isEmpty()) {
                return ActionResult.PASS;
            }

            // Prüfe ob der Hit-Punkt auf dem Rücken (Backpack-Position) ist
            Vec3d hitPos = hitResult.getPos();
            if (!isHitOnBackpack(targetPlayer, hitPos)) {
                return ActionResult.PASS;
            }

            // Öffne das Backpack
            if (player instanceof ServerPlayerEntity serverPlayer) {
                BackpackItem.openBackpack(serverPlayer, backpackStack, targetPlayer);
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });
    }

    /**
     * Prüft ob die Hit-Position auf dem Backpack des Spielers liegt
     */
    private static boolean isHitOnBackpack(PlayerEntity player, Vec3d hitPos) {
        // Konvertiere Hit-Position in Spieler-relative Koordinaten
        Vec3d playerPos = player.getPos();
        Vec3d relativeHit = hitPos.subtract(playerPos);

        // Rotiere den Hit-Punkt basierend auf der Spieler-Rotation
        // So dass wir in "Spieler-lokalen" Koordinaten arbeiten
        float yaw = player.getYaw() * ((float)Math.PI / 180F);
        double cos = Math.cos(yaw);
        double sin = Math.sin(yaw);

        double localX = relativeHit.x * cos + relativeHit.z * sin;
        double localY = relativeHit.y;
        double localZ = -relativeHit.x * sin + relativeHit.z * cos;

        // Prüfe ob der Hit im Backpack-Bereich liegt
        // X: Links/Rechts (-BACKPACK_WIDTH bis +BACKPACK_WIDTH)
        // Y: Höhe (BACKPACK_MIN_Y bis BACKPACK_MAX_Y)
        // Z: Vorne/Hinten (BACKPACK_MIN_Z bis BACKPACK_MAX_Z, negativ = Rücken)

        boolean inXRange = Math.abs(localX) <= BACKPACK_WIDTH;
        boolean inYRange = localY >= BACKPACK_MIN_Y && localY <= BACKPACK_MAX_Y;
        boolean inZRange = localZ >= BACKPACK_MIN_Z && localZ <= BACKPACK_MAX_Z;

        return inXRange && inYRange && inZRange;
    }

    private static ItemStack findBackpack(PlayerEntity player) {
        var capability = AccessoriesCapability.get(player);
        if (capability == null) return ItemStack.EMPTY;

        for (var container : capability.getContainers().values()) {
            for (int i = 0; i < container.getSize(); i++) {
                ItemStack stack = container.getAccessories().getStack(i);
                if (stack.getItem() instanceof BackpackItem) {
                    return stack;
                }
            }
        }

        return ItemStack.EMPTY;
    }
}