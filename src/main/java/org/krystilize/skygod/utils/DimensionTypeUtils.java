package org.krystilize.skygod.utils;

import net.minestom.server.MinecraftServer;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;

public class DimensionTypeUtils {
    public static final DimensionType FULLBRIGHT;

    static {
        FULLBRIGHT = DimensionType.builder(NamespaceID.from("skygod:fullbright"))
                .ambientLight(2.0f)
                .build();
        MinecraftServer.getDimensionTypeManager().addDimension(FULLBRIGHT);
    }
}
