package com.ditomax.mixin;

import com.ditomax.item.BackpackItem;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public class BlockHopperExtractionMixin {

    @Inject(method = "extract(Lnet/minecraft/block/entity/Hopper;Lnet/minecraft/inventory/Inventory;ILnet/minecraft/util/math/Direction;)Z", at = @At("HEAD"), cancellable = true)
    private static void preventFragmentExtractionFromInventory(Hopper hopper, Inventory inventory, int slot, Direction side, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack = inventory.getStack(slot);

        if (!itemStack.isEmpty() && itemStack.getItem() instanceof BackpackItem) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "extract(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/entity/ItemEntity;)Z", at = @At("HEAD"), cancellable = true)
    private static void preventFragmentExtractionFromEntity(Inventory inventory, ItemEntity itemEntity, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack = itemEntity.getStack();

        if (!itemStack.isEmpty() && itemStack.getItem() instanceof BackpackItem) {
            cir.setReturnValue(false);
        }
    }
}