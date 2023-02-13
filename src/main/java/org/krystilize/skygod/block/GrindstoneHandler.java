package org.krystilize.skygod.block;

import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.krystilize.skygod.events.ItemGrindEvent;
import org.krystilize.skygod.item.Item;

record GrindstoneHandler() implements BlockHandler {

    @Override
    public boolean onInteract(@NotNull Interaction interaction) {
        Player player = interaction.getPlayer();
        Player.Hand hand = interaction.getHand();

        ItemStack stack = player.getItemInHand(hand);
        Item item = Item.fromItemStack(stack);

        if (item == null) {
            return true;
        }

        Item grindingOutput = item.grindingOutput();

        if (grindingOutput == null) {
            return true;
        }

        if (stack.amount() == 1) {
            player.setItemInHand(hand, grindingOutput.stack());
            return false;
        }

        player.setItemInHand(hand, stack.withAmount(stack.amount() - 1));
        player.getInventory().addItemStack(grindingOutput.stack());
        return false;
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("skygod:grindstone");
    }
}
