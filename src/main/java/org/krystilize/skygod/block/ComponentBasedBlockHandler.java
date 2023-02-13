package org.krystilize.skygod.block;

import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public record ComponentBasedBlockHandler(List<BlockHandler> handlers) implements BlockHandler {

    public ComponentBasedBlockHandler {
        handlers = List.copyOf(handlers);
    }

    public ComponentBasedBlockHandler(BlockHandler... handlers) {
        this(List.of(handlers));
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("skygod:component_based_block_handler");
    }

    @Override
    public boolean isTickable() {
        return handlers().stream().anyMatch(BlockHandler::isTickable);
    }

    @Override
    public void tick(@NotNull Tick tick) {
        handlers().forEach(handler -> handler.tick(tick));
    }

    @Override
    public boolean onInteract(@NotNull Interaction interaction) {
        return handlers().stream().map(handler -> handler.onInteract(interaction))
                .reduce(false, (a, b) -> a || b);
    }

    @Override
    public void onDestroy(@NotNull Destroy destroy) {
        handlers().forEach(handler -> handler.onDestroy(destroy));
    }

    @Override
    public void onPlace(@NotNull Placement placement) {
        handlers().forEach(handler -> handler.onPlace(placement));
    }

    @Override
    public void onTouch(@NotNull Touch touch) {
        handlers().forEach(handler -> handler.onTouch(touch));
    }
}
