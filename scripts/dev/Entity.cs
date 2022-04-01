
namespace Kronos {

    public unsafe class Entity {

        // getters and setters
        public Bag<Component> m_Components { get; private set; }

        public String* m_Name { get; private set; }

        // constructor
        Entity() { m_Components = new Bag<Component>(); }

        ~Entity() { m_Components.Clear(); m_Name = null; }
        
        // methods
        public Entity Attach(ref Component component) {
            m_Components.Add(component);
            return this;
        }

        public Entity Detach(ref Component component) {
            m_Components.Remove(component);
            return this;
        }

        public Entity Destroy() {
            m_Components.Clear();
            return this;
        }

        

    }

}