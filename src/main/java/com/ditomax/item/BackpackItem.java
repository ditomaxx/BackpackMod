package com.ditomax.item;

import com.ditomax.util.BackpackManager;
import com.ditomax.util.gui.BackpackInventory;
import com.ditomax.util.gui.BackpackScreenHandler;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
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

    public static void openBackpack(ServerPlayerEntity serverPlayer, ItemStack backpackStack, PlayerEntity owner) {
        BackpackInventory backpackInventory = BackpackManager.getOrCreateInventory(owner, backpackStack);

        serverPlayer.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (syncId, playerInventory, player) ->
                        new BackpackScreenHandler(syncId, playerInventory, backpackInventory, owner),
                Text.literal(owner.getName().getString() + "´s Backpack")));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            ItemStack backpackStack;

            boolean isEquipped = TrinketsApi.getTrinketComponent(user)
                    .map(comp -> comp.isEquipped(stack -> stack.getItem() instanceof BackpackItem))
                    .orElse(false);

            if (isEquipped) {
                backpackStack = TrinketsApi.getTrinketComponent(user)
                        .map(trinkets -> trinkets.getAllEquipped().stream()
                                .map(Pair::getRight)
                                .filter(stack -> stack.getItem() instanceof BackpackItem)
                                .findFirst()
                                .orElse(ItemStack.EMPTY))
                        .orElse(ItemStack.EMPTY);
            } else {
                backpackStack = user.getStackInHand(hand);
            }

            if (backpackStack != null && !backpackStack.isEmpty()) {
                openBackpack(serverPlayer, backpackStack, user);
                return TypedActionResult.success(user.getStackInHand(hand));
            }
        }

        return TypedActionResult.consume(user.getStackInHand(hand));
    }
}