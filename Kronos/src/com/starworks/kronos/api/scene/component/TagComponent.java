package com.starworks.kronos.api.scene.component;

import com.starworks.kronos.inferno.Entity;

public class TagComponent {
    
    private String tag;
    private Entity entity;

    public TagComponent(Entity entity) {
        this.tag = "";
        this.entity = entity;
    }

    public TagComponent(String tag) {
        this.tag = tag;
    }

    public String tag() { return tag; }
    
    public void setTag(String tag) { this.tag = tag; }
 
    public Entity entity() { return entity; }   
}