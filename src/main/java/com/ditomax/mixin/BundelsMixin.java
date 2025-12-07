package com.ditomax.mixin;

import com.ditomax.util.ModTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleItem.class)
public class BundelsMixin {

    // Verhindert das Hinzufügen von Items zum Bundle
    @Inject(method = "onClicked", at = @At("HEAD"), cancellable = true)
    private void preventFragmentItemsInBundle(
            ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {

        // Prüfe ob das Item den Tag hat
        if (otherStack.isIn(ModTags.Items.BACKPACK_ITEMS)) {
            // Verhindere das Hinzufügen
            cir.setReturnValue(false);
        }
    }

    // Verhindert auch das Entnehmen in die andere Richtung
    @Inject(method = "onStackClicked", at = @At("HEAD"), cancellable = true)
    private void preventFragmentStackClick(
            ItemStack stack,
            Slot slot,
            ClickType clickType,
            PlayerEntity player,
            CallbackInfoReturnable<Boolean> cir) {

        ItemStack slotStack = slot.getStack();
        if (slotStack.isIn(ModTags.Items.BACKPACK_ITEMS)) {
            cir.setReturnValue(false);
        }
    }
}