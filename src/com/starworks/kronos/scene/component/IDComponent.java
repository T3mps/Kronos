package com.starworks.kronos.scene.component;

import java.util.UUID;

public class IDComponent {
    
    private UUID m_uuid;

    public IDComponent() {
        this.m_uuid = UUID.randomUUID();
    }

    public IDComponent(UUID uuid) {
        this.m_uuid = uuid;
    }

    public UUID uuid() { return m_uuid; }
}
