package org.krystilize.skygod.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.krystilize.skygod.utils.CachedObject;

/**
 * This itemdata contains all the data of an item that does not change with state id changes.
 */
record ItemData(String id, Material material, int matter, CachedObject<@Nullable Block> blockToPlaceCache, Component displayName, int stateIdOrigin,
                CachedObject<@Nullable Item> grindingOutputCache, TextColor color) {

    public Item grindingOutput() {
        return grindingOutputCache.get();
    }

    public Block blockToPlace() {
        return blockToPlaceCache.get();
    }
}
