package com.ditomax.mixin;

import com.ditomax.item.BackpackItem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class BackpackInventoryMixin {

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    private void preventInsert(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof BackpackItem) {
            if (!(((Slot) (Object) this).inventory instanceof PlayerInventory)) {
                cir.setReturnValue(false);
            }
        }
    }
}