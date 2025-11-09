package com.ditomax.util;

import com.ditomax.item.BackpackItem;
import com.ditomax.util.payload.OpenBackpackPayload;
import io.wispforest.accessories.api.AccessoriesCapability;
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
                        BackpackItem.openBackpack(player, backpackStack, player);
                    } else {
                        Text text = Text.literal("§cDu hast kein Backpack ausgerüstet!");
                        context.player().sendMessage(text, true);
                    }
                })
        );
    }

    private static ItemStack findBackpack(ServerPlayerEntity player) {
        var capability = AccessoriesCapability.get(player);
        if (capability != null) {
            for (var container : capability.getContainers().values()) {
                for (int i = 0; i < container.getSize(); i++) {
                    ItemStack stack = container.getAccessories().getStack(i);
                    if (stack.getItem() instanceof BackpackItem) {
                        return stack;
                    }
                }
            }
        }

        return ItemStack.EMPTY;
    }
}