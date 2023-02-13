package org.krystilize.skygod.loot;

import com.moandjiezana.toml.Toml;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.krystilize.skygod.block.Blocks;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

class BlockLootRegistry {
    private static final Map<String, Loot.Generator> registry;

    public static @Nullable Loot.Generator get(Block block) {
        return registry.get(block.namespace().toString());
    }

    public static @NotNull Loot.Generator getOrGenerate(Block block) {
        Loot.Generator loot = get(block);
        return loot != null ? loot : Loot.Generator.of(Loot.none());
    }

    static {
        Map<Block, Loot.Generator> map = new HashMap<>();

        // TODO: Move this into a config
        Toml toml = new Toml().read(new File("blockloot.toml"));

        TomlLootReader.read(toml).forEach((id, generator) -> {
            Block block = Blocks.from(id);
            map.put(block, generator);
        });

        registry = map.entrySet()
                .stream()
                .map(entry -> Map.entry(entry.getKey().namespace().toString(), entry.getValue()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    static Collection<Loot.Generator> getAll() {
        return registry.values();
    }
}
