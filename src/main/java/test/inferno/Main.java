package test.inferno;

import net.acidfrog.kronos.inferno.Registry;
import net.acidfrog.kronos.inferno.Scheduler;

public class Main {

    public static void main(String[] args) {
        Registry registry = new Registry();

        registry.create(new Position(0, 0), new Velocity(1, 4));
        registry.create(new Position(12, 17), new Velocity(1, 1));
        registry.create(new Position(-1, -4), new Velocity(3, 1));

        Runnable system = () -> {
            registry.view(Position.class, Velocity.class).forEach(view -> {
                        Position position = view.component1();
                        Velocity velocity = view.component2();

                        position.x += velocity.x;
                        position.y += velocity.y;
                        System.out.printf("Entity %d moved with %s to %s\n", view.entity().getID() - 16383, velocity, position);
                    });
        };


        Scheduler scheduler = registry.createScheduler();
        scheduler.schedule(system);
        scheduler.update(3);
    }

    static class Position {

        double x, y;

        public Position(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Position { " + "x=" + x + ", y=" + y + " } ";
        }
    }

    record Velocity(double x, double y) {
        
        @Override
        public String toString() {
            return "Velocity { " + "x=" + x + ", y=" + y + " } ";
        }
    }
}
