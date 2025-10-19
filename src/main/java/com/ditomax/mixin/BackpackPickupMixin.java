package com.ditomax.mixin;

import com.ditomax.item.BackpackItem;
import dev.emi.trinkets.api.TrinketsApi;
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

    @Unique
    private boolean hasBackpack(PlayerEntity player) {
        boolean hasBackpack = TrinketsApi.getTrinketComponent(player)
                .map(comp -> comp.isEquipped(stack -> stack.getItem() instanceof BackpackItem))
                .orElse(false);

        if (hasBackpack) return true;

        return getBackpackCount(player) > 0;
    }
}