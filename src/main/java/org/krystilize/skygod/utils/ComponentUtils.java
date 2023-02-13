package org.krystilize.skygod.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.ServerProcess;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static org.krystilize.skygod.utils.Symbols.ItemBox.*;

public class ComponentUtils {
    public static final Style STANDARD_STYLE = Style.style()
            .decorations(Set.of(TextDecoration.values()), false)
            .build();

    public static TextComponent text(Object... text) {
        var builder = Component.text();
        for (Object o : text) {
            builder.append(text(o));
        }
        return builder.build();
    }

    public static TextComponent text(Object text) {
        return Component.text(Objects.toString(text), STANDARD_STYLE);
    }

    private static final Map<Player, Map<Integer, Runnable>> CLICK_EVENT_HANDLERS =
            Collections.synchronizedMap(new WeakHashMap<>());

    public static ClickEvent runOnClick(Player player, Runnable runnable) {
        Map<Integer, Runnable> runnables = CLICK_EVENT_HANDLERS.computeIfAbsent(player, k -> new HashMap<>());

        int i = 0;
        while (runnables.containsKey(i)) {
            i++;
        }

        runnables.put(i, runnable);
        return ClickEvent.runCommand("/runOnClick " + i);
    }

    public static void init(ServerProcess process) {
        Command command = new Command("runOnClick");
        command.setCondition((sender, string) -> string != null);
        command.addSyntax((sender, context) -> {
            Player player = (Player) sender;
            int id = context.get("id");

            Map<Integer, Runnable> runnables = CLICK_EVENT_HANDLERS.get(player);

            if (runnables == null) {
                sender.sendMessage("No runnables registered for this player");
                return;
            }

            Runnable runnable = runnables.get(id);

            if (runnable == null) {
                sender.sendMessage("No runnable registered for this id");
                return;
            }

            runnables.remove(id);
            runnable.run();
        }, ArgumentType.Integer("id"));
        process.command().register(command);
    }


    private static final TextColor HIDDEN_IN_BOOK = TextColor.color(0xFDF8EC);
    private static final Component MIDDLE_SPACING = Component.text('\u258E').color(HIDDEN_IN_BOOK);
    private static final Component VERTICAL_MIDDLE = VERTICAL.append(MIDDLE_SPACING);
    private static final Component TOP_ROW_3x3 = Component.text().append(TL_CORNER, HORIZONTAL, TOP_FORK, HORIZONTAL, TOP_FORK, HORIZONTAL, TR_CORNER).build();
    private static final Component MIDDLE_ROW_3x3 = Component.text().append(LEFT_FORK, HORIZONTAL, FULL_FORK, HORIZONTAL, FULL_FORK, HORIZONTAL, RIGHT_FORK).build();
    private static final Component BOTTOM_ROW_3x3 = Component.text().append(BL_CORNER, HORIZONTAL, BOTTOM_FORK, HORIZONTAL, BOTTOM_FORK, HORIZONTAL, BR_CORNER).build();
    private static final Component ARROW_ABOVE = Component.text()
            .append(Component.space(), ARROW.color(HIDDEN_IN_BOOK), Component.space())
            .append(TL_CORNER, HORIZONTAL, TR_CORNER).build();
    private static final Component ARROW_BEFORE = Component.text().append(Component.space(), ARROW, Component.space()).build();
    private static final Component ARROW_BELOW = Component.text()
            .append(Component.space(), ARROW.color(HIDDEN_IN_BOOK), Component.space())
            .append(BL_CORNER, HORIZONTAL, BR_CORNER).build();

    private static final int RESULT_SLOT = 9;

    /**
     * Creates a grid 3x3 based off of the given styles.
     *
     * @param stylesList the list of styles, spread by row then column, and with a final element for the output recipe.
     * @return the grid as a component
     */
    public static Component createGrid3x3(List<Style> stylesList) {
        var builder = Component.text();

        Style[] styles = new Style[10];

        for (int i = 0; i < stylesList.size(); i++) {
            styles[i] = Objects.requireNonNullElse(stylesList.get(i), STANDARD_STYLE.color(HIDDEN_IN_BOOK));
        }

        builder.append(TOP_ROW_3x3, Component.newline());
        builder.append(VERTICAL_MIDDLE,
                FULL_BLOCK.style(styles[0]), VERTICAL_MIDDLE,
                FULL_BLOCK.style(styles[1]), VERTICAL_MIDDLE,
                FULL_BLOCK.style(styles[2]), VERTICAL, Component.newline());
        builder.append(MIDDLE_ROW_3x3, ARROW_ABOVE, Component.newline());

        builder.append(VERTICAL_MIDDLE,
                FULL_BLOCK.style(styles[3]), VERTICAL_MIDDLE,
                FULL_BLOCK.style(styles[4]), VERTICAL_MIDDLE,
                FULL_BLOCK.style(styles[5]), VERTICAL);
        builder.append(ARROW_BEFORE);
        builder.append(VERTICAL_MIDDLE, FULL_BLOCK.style(styles[9]), VERTICAL);
        builder.append(Component.newline());

        builder.append(MIDDLE_ROW_3x3, ARROW_BELOW, Component.newline());
        builder.append(VERTICAL_MIDDLE,
                FULL_BLOCK.style(styles[6]), VERTICAL_MIDDLE,
                FULL_BLOCK.style(styles[7]), VERTICAL_MIDDLE,
                FULL_BLOCK.style(styles[8]), VERTICAL, Component.newline());
        builder.append(BOTTOM_ROW_3x3, Component.newline());

        return builder.build();
    }

    public static Grid3x3Builder buildGrid3x3() {
        return new Grid3x3Builder();
    }

    public static String screamingSnakeToClean(String string) {
        return Arrays.stream(string.split("_", -1))
                .map(String::toLowerCase)
                .map(str -> {
                    // Convert first letter to upper case
                    return str.substring(0, 1).toUpperCase() + str.substring(1);
                })
                .collect(Collectors.joining(" "));
    }

    public static class Grid3x3Builder {
        Style[] styles = new Style[10];

        public Grid3x3Builder() {
        }

        public Grid3x3Builder set(int index, Style style) {
            styles[index] = style;
            return this;
        }

        public Grid3x3Builder set(int row, int column, Style style) {
            if (row < 0 || row > 2 || column < 0 || column > 2) {
                throw new IllegalArgumentException("Row and column must be between 0 and 2");
            }
            styles[row * 3 + column] = style;
            return this;
        }

        public Grid3x3Builder result(Style style) {
            styles[RESULT_SLOT] = style;
            return this;
        }

        public Component build() {
            return createGrid3x3(Arrays.asList(styles));
        }
    }
}
