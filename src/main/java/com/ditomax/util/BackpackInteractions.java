package com.ditomax.util;

import com.ditomax.item.BackpackItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;

public class BackpackInteractions {

    public static void register() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient() || hand != Hand.MAIN_HAND) {
                return ActionResult.PASS;
            }

            if (!(entity instanceof PlayerEntity targetPlayer)) {
                return ActionResult.PASS;
            }

            boolean hasBackpack = TrinketsApi.getTrinketComponent(targetPlayer)
                    .map(comp -> comp.isEquipped(stack -> stack.getItem() instanceof BackpackItem))
                    .orElse(false);

            if (!hasBackpack) {
                return ActionResult.PASS;
            }

            ItemStack backpackStack = TrinketsApi.getTrinketComponent(targetPlayer)
                    .map(trinkets -> trinkets.getAllEquipped().stream()
                            .map(Pair::getRight)
                            .filter(stack -> stack.getItem() instanceof BackpackItem)
                            .findFirst()
                            .orElse(ItemStack.EMPTY))
                    .orElse(ItemStack.EMPTY);

            if (!backpackStack.isEmpty() && player instanceof ServerPlayerEntity serverPlayer) {
                BackpackItem.openBackpack(serverPlayer, backpackStack, targetPlayer);
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });
    }
}