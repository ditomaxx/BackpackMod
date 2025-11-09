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
import net.minecraft.world.World;

public class BackpackInteractions {

    public static void register() {
        UseEntityCallback.EVENT.register((PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) -> {
            if (world.isClient() || hand != Hand.MAIN_HAND) {
                return ActionResult.PASS;
            }

            if (!(entity instanceof PlayerEntity targetPlayer)) {
                return ActionResult.PASS;
            }

            ItemStack backpackStack = findBackpack(targetPlayer);

            if (backpackStack.isEmpty()) {
                return ActionResult.PASS;
            }

            if (player instanceof ServerPlayerEntity serverPlayer) {
                BackpackItem.openBackpack(serverPlayer, backpackStack, targetPlayer);
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });
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