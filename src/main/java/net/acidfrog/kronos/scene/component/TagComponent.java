package net.acidfrog.kronos.scene.component;

import net.acidfrog.kronos.core.lang.UUID;
import net.acidfrog.kronos.scene.ecs.component.AbstractComponent;

public final class TagComponent extends AbstractComponent {
    
    private final String name;
    private final UUID uuid;

    public TagComponent(String name) {
        this.name = name;
        this.uuid = UUID.generate();
    }

    public String getName() {
        return name;
    }

    public UUID getUUID() {
        return uuid;
    }

}
