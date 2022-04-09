
namespace Kronos {

    public class ScriptableEntity : Entity {

        ScriptableEntity() {
            m_Components = new Bag<Component>();
        }

        ~ScriptableEntity() {
            m_Components.Clear();
            m_Name = null;
        }

        public virtual void OnEnable();

        public virtual void OnDisable();

        public virtual void OnDestroy();

        public virtual void Update();

        public virtual void PhysicsUpdate();

        public virtual void OnCollisionEnter(ref Collision collision);

        public virtual void OnCollisionExit(ref Collision collision);

        public virtual void OnCollisionStay(ref Collision collision);

        public virtual void OnTriggerEnter(ref Collision collision);

        public virtual void OnTriggerExit(ref Collision collision);

        public virtual void OnTriggerStay(ref Collision collision);

    }

}