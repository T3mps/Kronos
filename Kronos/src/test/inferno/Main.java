package test.inferno;

import com.starworks.kronos.inferno.Registry;
import com.starworks.kronos.inferno.Scheduler;

public class Main {

    public static void main(String[] args) {
        try (Registry registry = new Registry()) {
            for (int i = 0; i < 25_000_000; i++) {
                registry.create(new Position(Math.max(1, Math.random() * i), Math.max(1, Math.random() * i)), new Velocity(Math.max(1, Math.random() * i), Math.max(1, Math.random() * i)));
            }

            Runnable system = () -> {
                registry.view(Position.class, Velocity.class).stream().forEach(view -> {
                    Position position = view.component1();
                    Velocity velocity = view.component2();

                    position.x += velocity.x;
                    position.y += velocity.y;
                    System.out.printf("Entity %d moved with %s to %s\n", view.entity().getID(), velocity, position);
                });
            };

            Scheduler scheduler = registry.createScheduler();
            scheduler.schedule(system);

            for (;;) {
                scheduler.update();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static class Position {

        protected double x, y;

        protected Position(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Position { ");
            sb.append("x=").append(x).append(", ");
            sb.append("y=").append(y).append("} ");
            return sb.toString();
        }
    }

    protected record Velocity(double x, double y) {
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Velocity { ");
            sb.append("x=").append(x).append(", ");
            sb.append("y=").append(y).append("} ");
            return sb.toString();
        }
    }
}
