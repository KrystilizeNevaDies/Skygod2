package org.krystilize.skygod.utils;

import net.minestom.server.instance.InstanceContainer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SkygodHubInstance extends InstanceContainer {

    public static @NotNull SkygodHubInstance create() {
        return new SkygodHubInstance();
    }

    private SkygodHubInstance() {
        super(new UUID(0, 0), DimensionTypeUtils.FULLBRIGHT);
    }
}
