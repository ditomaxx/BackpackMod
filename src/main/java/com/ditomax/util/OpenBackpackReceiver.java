package com.ditomax.util;

import com.ditomax.item.BackpackItem;
import com.ditomax.util.payload.OpenBackpackPayload;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class OpenBackpackReceiver {

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(
                OpenBackpackPayload.ID,
                (payload, context) -> context.server().execute(() -> {
                    ServerPlayerEntity player = context.player();
                    ItemStack backpackStack = findBackpack(player);

                    if (backpackStack != null && !backpackStack.isEmpty()) {
                        BackpackItem.openBackpack(player, backpackStack);
                    } else {
                        Text text = Text.literal("§cDu hast kein Backpack ausgerüstet!");
                        context.player().sendMessage(text, true);
                    }
                })
        );
    }

    private static ItemStack findBackpack(ServerPlayerEntity player) {
        var trinketBackpack = TrinketsApi.getTrinketComponent(player)
                .map(comp -> {
                    var equipped = comp.getEquipped(stack -> stack.getItem() instanceof BackpackItem);
                    return equipped.isEmpty() ? ItemStack.EMPTY : equipped.getFirst().getRight();
                })
                .orElse(ItemStack.EMPTY);

        if (!trinketBackpack.isEmpty()) {
            return trinketBackpack;
        }

        return ItemStack.EMPTY;
    }
}