
namespace Kronos {

    public unsafe class Scene {

        private static List<Entity>? m_Entities = new List<Entity>?();

        private Scene() {}
        
        ~Scene() { m_Entities?.Clear(); }

        public static virtual Entity CreateEntity(ref String name) {
            const Entity entity = new Entity().Attach(new TagComponent(name));
            return entity;
        }

        public static virtual Entity CreateEntity(String* name) {
            const Entity entity = new Entity()->Attach(new TagComponent(name));
            return *entity;
        }

        public static virtual Entity DestroyEntity(Entity* entity) {
            entity->Destroy();
            return *entity;
        }

        public static virtual Entity GetEntity(String name) {
            return Entity.Find(name);
        }

    }

}