package net.acidfrog.kronos.test.tests.ecs;

import java.util.List;

import net.acidfrog.kronos.core.Config;
import net.acidfrog.kronos.mathk.Vector3f;
import net.acidfrog.kronos.scene.ecs.Component;
import net.acidfrog.kronos.scene.ecs.Engine;
import net.acidfrog.kronos.scene.ecs.Entity;
import net.acidfrog.kronos.scene.ecs.Family;

public class ECSTest {

    static Engine engine = new Engine();

    public static void main(String[] args) {
        Entity test1 = new Entity()
        .addComponent(new TransformComponent())
        .addComponent(new RenderComponent());

        Entity test2 = new Entity()
        .addComponent(new TransformComponent())
        .addComponent(new RenderComponent())
        .addComponent(new TestComponent());

        engine.addEntity(test1);
        engine.addEntity(test2);

        float dt = 0.1f;

        List<Entity> entities = engine.getView(Family.define(TransformComponent.class, RenderComponent.class));
        for (Entity e : entities) {
            TransformComponent t = e.getComponent(TransformComponent.class);
            RenderComponent r = e.getComponent(RenderComponent.class);
            t.x += dt;
            r.color.x += dt;
        }

        entities = engine.getView(Family.define(TestComponent.class));
        for (Entity e : entities) {
            TestComponent t = e.getComponent(TestComponent.class);
            t.doSomething();
        }

        System.out.println(Config.OPERATING_SYSTEM);
    }

    static class TransformComponent extends Component {

        public float x;
        public float y;

        public TransformComponent() {
            super();
        }
    }

    static class RenderComponent extends Component {

        public Vector3f color = new Vector3f(0, 0, 0);

        public RenderComponent() {
            super();
        }
    }

    static class TestComponent extends Component {

        public TestComponent() {
            super();
        }

        public void doSomething() {
            System.out.println("Doing something");
        }
    }
    
}
