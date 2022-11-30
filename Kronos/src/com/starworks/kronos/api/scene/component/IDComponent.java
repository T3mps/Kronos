package com.starworks.kronos.api.scene.component;

import java.util.UUID;

public class IDComponent {
    
    private UUID uuid;

    public IDComponent() {
        this.uuid = UUID.randomUUID();
    }

    public IDComponent(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID uuid() { return uuid; }
}
