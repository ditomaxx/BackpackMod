package com.ditomax.util;

import com.ditomax.item.BackpackItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

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



            // WICHTIG: Nutze BackpackManager um DASSELBE Objekt zu bekommen
            SharedBackpackInventory sharedInventory = BackpackManager.getOrCreateInventory(targetPlayer);

            player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                    (syncId, playerInventory, playerEntity) ->
                            new BackpackScreenHandler(syncId, playerInventory, sharedInventory, targetPlayer),
                    Text.literal(targetPlayer.getName().getString() + "'s Backpack")
            ));

            return ActionResult.SUCCESS;
        });
    }
}
}