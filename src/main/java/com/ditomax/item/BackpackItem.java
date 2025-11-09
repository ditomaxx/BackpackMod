package com.ditomax.item;

import com.ditomax.util.BackpackManager;
import com.ditomax.util.gui.BackpackInventory;
import com.ditomax.util.gui.BackpackScreenHandler;
import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.DropRule;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class BackpackItem extends AccessoryItem {

    public BackpackItem(Settings properties) {
        super(properties);
    }

    public static void openBackpack(ServerPlayerEntity serverPlayer, ItemStack backpackStack, PlayerEntity owner) {
        BackpackInventory backpackInventory = BackpackManager.getOrCreateInventory(owner, backpackStack);

        serverPlayer.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (syncId, playerInventory, player) ->
                        new BackpackScreenHandler(syncId, playerInventory, backpackInventory, owner),
                Text.literal(owner.getName().getString() + "´s Backpack")));
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {

            openBackpack(serverPlayer, user.getStackInHand(hand), user);
            return ActionResult.SUCCESS;
        }

        return ActionResult.CONSUME;
    }

    @Override
    public boolean canEquipFromUse(ItemStack stack) {
        return false;
    }

    @Override
    public DropRule getDropRule(ItemStack stack, SlotReference reference, DamageSource source) {
        return DropRule.KEEP;
    }
}