package net.acidfrog.kronos.physics.world.body;

/**
 * Respresent a material.
 * 
 * @author Ethan Temprovich
 */
public record Material(float density, float restitution, float staticFriction, float dynamicFriction) {

    public static final Material m_Default         =   new Material(0.5f, 0.5f, 0.5f, 0.7f);

    public static final Material m_Rock            =   new Material(0.6f, 0.10f, 0.4f, 0.40f);
    public static final Material m_Wood            =   new Material(0.3f, 0.20f, 0.3f, 0.30f);
    public static final Material m_Metal           =   new Material(1.2f, 0.05f, 0.7f, 0.70f);
    public static final Material m_Rubber          =   new Material(0.3f, 0.80f, 0.2f, 0.15f);
    public static final Material m_ExtremeRubber   =   new Material(0.3f, 0.95f, 0.2f, 0.05f);
    public static final Material m_Feather         =   new Material(0.1f, 0.20f, 0.3f, 0.30f);
    public static final Material m_Static          =   new Material(0.0f, 0.40f, 0.0f, 0.00f);
    
    public static Material[] values() {
        return new Material[] {
            m_Default,
            m_Rock,
            m_Wood,
            m_Metal,
            m_Rubber,
            m_ExtremeRubber,
            m_Feather,
            m_Static
        };
    }

}
