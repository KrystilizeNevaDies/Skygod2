package org.krystilize.skygod.crafting;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.WrittenBookMeta;
import net.minestom.server.network.packet.server.play.OpenBookPacket;
import org.krystilize.skygod.item.Item;
import org.krystilize.skygod.utils.ComponentUtils;

import java.util.*;

public class CraftingGUI {

    public static final ItemStack DEFAULT_CRAFTING_BOOK = ItemStack.builder(Material.WRITTEN_BOOK)
            .displayName(ComponentUtils.text("Click to open the crafting book"))
            .meta(WrittenBookMeta.class, meta -> meta.generation(WrittenBookMeta.WrittenBookGeneration.TATTERED))
            .lore(ComponentUtils.text(""))
            .build();
    private final UUID playerUUID;

    public CraftingGUI(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public void open(Player player) {
        if (!player.getUuid().equals(playerUUID)) {
            throw new IllegalArgumentException("Player UUID does not match");
        }

        player.sendMessage("Opening crafting GUI");

        List<Component> pages = new ArrayList<>();
        for (Map.Entry<CraftingRecipe, Integer> entry : CraftingRecipe.values()
                .stream()
                .map(recipe -> {
                    int craftTimes = CraftingManager.howManyTimesCanCraft(player, recipe);
                    return Map.entry(recipe, craftTimes);
                })
                .sorted((a, b) -> {
                    if (Objects.equals(a.getValue(), b.getValue())) {
                        return a.getKey().result().id().compareTo(b.getKey().result().id());
                    }
                    return b.getValue() - a.getValue();
                })
                .toList()) {

            CraftingRecipe recipe = entry.getKey();
            int timesCanCraft = entry.getValue();
            Component title = Component.text(ComponentUtils.screamingSnakeToClean(recipe.result().id()));
            boolean canCraft = timesCanCraft > 0;
            Component craftableStatus = Component.text()
                    .append(ComponentUtils.text(canCraft ? "x" + timesCanCraft : "Not enough ingredients"))
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.BOLD, true)
                    .build();

            Component craftingButtons = !canCraft ? null : Component.text()
                    .append(Component.text("Craft x1").style(style -> {
                        style.clickEvent(ComponentUtils.runOnClick(player, () -> {
                            CraftingManager.craftRecipe(player, recipe, 1);
                            open(player);
                        }));
                    }))
                    .append(timesCanCraft == 1 ? Component.empty() : Component.newline())
                    .append(timesCanCraft == 1 ? Component.empty() : Component.text("Craft x" + timesCanCraft).style(style -> {
                        style.clickEvent(ComponentUtils.runOnClick(player, () -> {
                            CraftingManager.craftRecipe(player, recipe, timesCanCraft);
                            open(player);
                        }));
                    }))
                    .build();

            var builder = ComponentUtils.buildGrid3x3();

            Style result = Style.style()
                    .hoverEvent(recipe.result().stack().asHoverEvent())
                    .color(recipe.result().color())
                    .build();
            builder.result(result);

            recipe.ingredients().forEach((pos, ingredient) -> {
                int amount = ingredient.getKey();
                Item stack = ingredient.getValue();

                Style style = Style.style()
                        .hoverEvent(stack.stack()
                                .withAmount(amount)
                                .asHoverEvent())
                        .color(stack.color())
                        .build();
                builder.set(pos, style);
            });

            TextComponent.Builder textBuilder = Component.text();
            textBuilder.append(title);
            textBuilder.append(Component.newline());
            textBuilder.append(craftableStatus);
            textBuilder.append(Component.newline());
            textBuilder.append(builder.build());

            if (craftingButtons != null) {
                textBuilder.append(Component.newline());
                textBuilder.append(craftingButtons);
            }
            pages.add(textBuilder.build().compact());
        }

        ItemStack item = ItemStack.builder(Material.WRITTEN_BOOK)
                .meta(WrittenBookMeta.class, meta -> {
                    meta.author("krys");
                    meta.title("test");
                    meta.pages(pages);
                })
                .build();

        ItemStack prev = player.getInventory().getItemInHand(Player.Hand.MAIN);
        player.getInventory().setItemInHand(Player.Hand.MAIN, item);
        OpenBookPacket packet = new OpenBookPacket(Player.Hand.MAIN);
        player.sendPacket(packet);
        player.scheduleNextTick(ignored -> {
            player.getInventory().setItemInHand(Player.Hand.MAIN, prev);
        });
    }
}
