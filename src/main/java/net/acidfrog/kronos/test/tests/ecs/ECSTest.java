package net.acidfrog.kronos.test.tests.ecs;

import net.acidfrog.kronos.mathk.Mathk;
import net.acidfrog.kronos.mathk.Vector2k;
import net.acidfrog.kronos.physics.geometry.Transform;
import net.acidfrog.kronos.physics.world.body.Rigidbody;
import net.acidfrog.kronos.scene.ecs.Registry;
import net.acidfrog.kronos.scene.ecs.Entity;
import net.acidfrog.kronos.scene.ecs.EntityListener;
import net.acidfrog.kronos.scene.ecs.Family;
import net.acidfrog.kronos.scene.ecs.component.Component;
import net.acidfrog.kronos.scene.ecs.signal.SignalListener;
import net.acidfrog.kronos.scene.ecs.system.IteratingSystem;

public class ECSTest {

    static final Registry engine = new Registry();

    public static void main(String[] args) {
        EntityListener physicsListener = new EntityListener() {
            @Override
            public void onEntityAdd(Entity entity) {
                System.out.println("Entity " + entity.getUUID() + " added to physics world");
            }

            @Override
            public void onEntityRemove(Entity entity) {
                System.out.println("Entity " + entity.getUUID() + " removed from physics world");
            }
        };

        EntityListener renderListener = new EntityListener() {
            @Override
            public void onEntityAdd(Entity entity) {
                System.out.println("Entity " + entity.getUUID() + " added to render list");
            }

            @Override
            public void onEntityRemove(Entity entity) {
                System.out.println("Entity " + entity.getUUID() + " removed from render list");
            }
        };

        SignalListener<Entity> componentListener = new SignalListener<Entity>() {
            @Override
            public void receive(Entity entity) {
                System.out.println("Component added on Entity " + entity.getUUID());
            }
        };

        Family physicsFamily = Family.define(TransformComponent.class, RigidbodyComponent.class);
        Family renderFamily = Family.define(TransformComponent.class, RenderComponent.class);

        engine.bind(new PhysicsSystem());
        engine.bind(new RenderSystem());

        engine.register(physicsListener, physicsFamily);
        engine.register(renderListener, renderFamily);

        for (int i = 0; i < 10; i++) {
            Entity entity = new Entity();
            entity.onComponentAdd.register(componentListener);
            entity.add(new TransformComponent());
            if (Mathk.randomBoolean()) entity.add(new RigidbodyComponent(null));
            if (Mathk.randomBoolean()) entity.add(new RenderComponent(Mathk.random(100)));
            engine.add(entity);
        }

        for (int i = 0; i < 32; i++) engine.update(0.016f);
        
        engine.dispose();
    }

    static class PhysicsSystem extends IteratingSystem {

        public PhysicsSystem() {
            super(Family.define(TransformComponent.class, RigidbodyComponent.class));
        }

        @Override
        protected void process(Entity entity, float dt) { 
            TransformComponent transform = entity.get(TransformComponent.class);
            RigidbodyComponent rigidbody = entity.get(RigidbodyComponent.class);
        }

    }

    static class RenderSystem extends IteratingSystem {

        public RenderSystem() {
            super(Family.define(TransformComponent.class, RenderComponent.class));
        }

        @Override
        protected void process(Entity entity, float dt) {
            TransformComponent transform = entity.get(TransformComponent.class);
            RenderComponent render = entity.get(RenderComponent.class);

            System.out.println("RenderSystem: " + transform.getPosition() + " " + render.getZIndex());
        }

    }

    static class TransformComponent extends Component {

        private Transform transform;
        private Vector2k scale;
    
        public TransformComponent() {
            this(new Vector2k(0f), 0f, new Vector2k(1f, 1f));
        }
    
        public TransformComponent(Vector2k position) {
            this(position, 0f, new Vector2k(1f, 1f));
        }
    
        public TransformComponent(Vector2k position, float rotation) {
            this(position, rotation, new Vector2k(1f, 1f));
        }
    
        public TransformComponent(Vector2k position, float rotation, float scale) {
            this.transform = new Transform(position, rotation);
            this.scale = new Vector2k(scale);
        }
    
        public TransformComponent(Vector2k position, float rotation, Vector2k scale) {
            this.transform = new Transform(position, rotation);
            this.scale = scale;
        }
    
        public Vector2k getPosition() {
            return transform.getPosition();
        }
    
        public void setPosition(Vector2k position) {
            transform.setPosition(position);
        }
        
        public void setPosition(float x, float y) {
            transform.setPosition(x, y);
        }
    
        public float getRotation() {
            return transform.getRotation().getRadians();
        }
    
        public void setRotation(float rotation) {
            transform.setRotation(rotation);
        }
    
        public Vector2k getScale() {
            return scale;
        }
    
        public void setScale(Vector2k scale) {
            this.scale = scale;
        }
    
        @Override
        public int compareTo(Component o) {
            return 0;
        }
    
    }
    
    static class RigidbodyComponent extends Component {

        private Rigidbody body;
    
        public RigidbodyComponent(Rigidbody body) {
            this.body = body;
        }
    
        public Rigidbody getBody() {
            return body;
        }
    
        @Override
        public int compareTo(Component o) {
            return 0;
        }
        
    }

    static class RenderComponent extends Component {

        private int zIndex;
    
        public RenderComponent(int zIndex) {
            this.zIndex = zIndex;
        }
    
        public int getZIndex() {
            return zIndex;
        }
    
        @Override
        public int compareTo(Component o) {
            return Integer.compare(zIndex, ((RenderComponent) o).zIndex);
        }
        
    }
    
}
