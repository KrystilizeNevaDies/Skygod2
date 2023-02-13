package org.krystilize.skygod.block;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ItemFrameMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

record WorkbenchHandler() implements BlockHandler {
    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("skygod:workbench");
    }

    private static final Tag<Integer> ENTITY_ID = Tag.Integer("skygod:workbench:entity_id");

    @Override
    public boolean onInteract(@NotNull Interaction interaction) {
        Player player = interaction.getPlayer();
        Point position = interaction.getBlockPosition();
        Instance instance = interaction.getInstance();
        NBTCompound nbt = interaction.getBlock().nbt();

        Integer entityId = interaction.getBlock().getTag(ENTITY_ID);
        if (entityId != null) {
            Entity entity = Entity.getEntity(entityId);
            if (entity != null) {
                handleEntityInteraction(player, entity);
                return true;
            }
        }

        Entity displayEntity = new Entity(EntityType.ITEM_FRAME);
        displayEntity.setInstance(instance, position).thenRun(() -> handleEntityInteraction(player, displayEntity));
        instance.setBlock(position, interaction.getBlock().withTag(ENTITY_ID, displayEntity.getEntityId()));
        return true;
    }

    private void handleEntityInteraction(Player player, Entity entity) {

    }
}
