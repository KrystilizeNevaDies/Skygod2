package org.krystilize.skygod.item;

import org.krystilize.skygod.item.Item;

public sealed interface ItemConstants permits Item {
    Item GRASS_BLOCK = Item.from("grass");
    Item PEBBLE = Item.from("pebble");
    Item SHARPENED_PEBBLE = Item.from("sharpened_pebble");
    Item STONE_CHUNK = Item.from("stone_chunk");
    Item DIRT_BLOCK = Item.from("dirt");
    Item TREE_LEAVES = Item.from("tree_leaves");
    Item STICK = Item.from("stick");
    Item CRUMBLING_PICKAXE = Item.from("crumbling_pickaxe");
    Item CRUMBLING_SHOVEL = Item.from("crumbling_shovel");
    Item CRUMBLING_AXE = Item.from("crumbling_axe");
    Item CRUMBLING_HOE = Item.from("crumbling_hoe");
    Item WATER_EXTRACTOR = Item.from("water_extractor");
    Item GRINDSTONE = Item.from("grindstone");
}
