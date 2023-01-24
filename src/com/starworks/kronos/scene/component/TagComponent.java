package com.starworks.kronos.scene.component;

public class TagComponent {
    
    private String m_tag;

    public TagComponent() {
        this.m_tag = "";
    }

    public TagComponent(String tag) {
        this.m_tag = tag;
    }

    public String tag() { return m_tag; }
    
    public void setTag(String tag) { this.m_tag = tag; }
}