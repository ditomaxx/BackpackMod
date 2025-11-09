package com.ditomax.mixin;

import com.ditomax.item.BackpackItem;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class BackpackPickupMixin {

    @Unique
    private static int getBackpackCount(PlayerEntity player) {
        int count = getBackpackCountWithoutCursor(player);

        if (player.currentScreenHandler != null) {
            ItemStack cursor = player.currentScreenHandler.getCursorStack();
            if (cursor.getItem() instanceof BackpackItem) {
                count += cursor.getCount();
            }
        }

        return count;
    }

    @Unique
    private static int getBackpackCountWithoutCursor(PlayerEntity player) {
        int count = 0;

        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() instanceof BackpackItem) {
                count += stack.getCount();
            }
        }

        for (ItemStack stack : player.getInventory().offHand) {
            if (stack.getItem() instanceof BackpackItem) {
                count += stack.getCount();
            }
        }

        for (ItemStack stack : player.getInventory().armor) {
            if (stack.getItem() instanceof BackpackItem) {
                count += stack.getCount();
            }
        }

        return count;
    }

    @Shadow
    public abstract ItemStack getStack();

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    public void onBackpackPickup(PlayerEntity player, CallbackInfo ci) {
        if (player.getWorld().isClient()) return;

        ItemStack itemStack = this.getStack();
        Item item = itemStack.getItem();

        if (item instanceof BackpackItem) {
            if (player.getAbilities().creativeMode) {
                return;
            }

            if (hasBackpack(player)) {
                player.sendMessage(Text.literal("§cDu kannst nicht mehr als ein Backpack tragen!"), true);
                ci.cancel();
            }
        }
    }


    // Hilfsmethode: Prüft ob Spieler ein Backpack in Accessory-Slots trägt
    private boolean hasBackpack(PlayerEntity player) {
        var capability = AccessoriesCapability.get(player);
        if (capability == null) return false;

        for (var container : capability.getContainers().values()) {
            for (int i = 0; i < container.getSize(); i++) {
                ItemStack stack = container.getAccessories().getStack(i);
                if (stack.getItem() instanceof BackpackItem) {
                    return true;
                }
            }
        }

        return getBackpackCount(player) > 0;
    }
}