package org.krystilize.skygod.events;

import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.event.trait.PlayerInstanceEvent;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.krystilize.skygod.item.Item;

/**
 * This event is called when a player uses a grindstone on an item.
 */
public class ItemGrindEvent implements PlayerInstanceEvent {

    private final Player player;
    private final Player.Hand hand;
    private Item item;
    private boolean itemChanged = false;

    public ItemGrindEvent(Player player, Player.Hand hand, Item item) {
        this.player = player;
        this.hand = hand;
        this.item = item;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    public Player.Hand getHand() {
        return hand;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        itemChanged = true;
    }

    public boolean isItemChanged() {
        return itemChanged;
    }
}
