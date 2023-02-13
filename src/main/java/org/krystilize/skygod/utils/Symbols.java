package org.krystilize.skygod.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public interface Symbols {
    interface ItemBox {
        TextComponent VERTICAL = Component.text('\u2502');
        TextComponent HORIZONTAL = Component.text('\u2500');
        TextComponent BR_CORNER = Component.text('\u2518');
        TextComponent BL_CORNER = Component.text('\u2514');
        TextComponent TL_CORNER = Component.text('\u250C');
        TextComponent TR_CORNER = Component.text('\u2510');
        TextComponent TOP_FORK = Component.text('\u252C');
        TextComponent RIGHT_FORK = Component.text('\u2524');
        TextComponent BOTTOM_FORK = Component.text('\u2534');
        TextComponent LEFT_FORK = Component.text('\u251C');

        TextComponent FULL_BLOCK = Component.text('\u2588');
        TextComponent FULL_FORK = Component.text('\u253C');


        TextComponent ARROW = Component.text('\u2192');
    }
}
