package com.ditomax.item;

import com.ditomax.util.gui.BackpackInventory;
import com.ditomax.util.gui.BackpackScreenHandler;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class BackpackItem extends TrinketItem {

    public BackpackItem() {
        super(new Settings()
                .maxCount(1)
                .rarity(Rarity.RARE)
                .fireproof()
                .component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true)));
    }

    public static void openBackpack(ServerPlayerEntity serverPlayer, ItemStack backpackStack) {
        BackpackInventory backpackInventory = new BackpackInventory(backpackStack);

        serverPlayer.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (syncId, playerInventory, player) ->
                        new BackpackScreenHandler(syncId, playerInventory, backpackInventory, player),
                Text.literal("Backpack")));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            openBackpack(serverPlayer, stack);
            return TypedActionResult.success(stack);
        }

        return TypedActionResult.consume(stack);
    }
}