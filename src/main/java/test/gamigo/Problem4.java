package test.gamigo;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import test.gamigo.Problem4.ElevatorMotor.Direction;

public class Problem4 {

    /*
     * Design an elevator controller for a building of ten floors (including the ground floor).
     */
    public static class ElevatorController extends ElevatorControllerBase {

        private int time;

        public ElevatorController(ElevatorMotor motor) {
            super(motor);
            this.motor.addFloorReachedListener(f -> {
                System.out.println("Elevator reached floor " + f);
            });
            this.time = 0;
        }

        public void update(float dt) {
            time += dt;
            motor.step(dt);
        }

        @Override
        public void summonButtonPushed(int floor, Direction direction) {
            motor.call(floor, direction);
        }

        @Override
        public void floorButtonPushed(int floor) {
            motor.call(floor);
            motor.move();
        }
    }

    /*
     * Simple listener interface for listening for when the elevator arrives at a floor.
     */
    @FunctionalInterface
    public interface IFloorEventListener {

        public abstract void floorReached(int floor);
    }

    public static void main(String[] args) {
        ElevatorMotor motor = new ElevatorMotor();
        ElevatorController controller = new ElevatorController(motor);
        motor.call(3, Direction.UP);
        motor.call(5, Direction.UP);
        motor.call(1, Direction.DOWN);
        motor.call(2, Direction.DOWN);

        for (int i = 0; i < 100; i++) {
            controller.update(1);
        }
    }

    public static class ElevatorMotor {

        public class Request {

            public long time;
            public Integer floor;
            public Direction direction;
    
            public Request(long time, Integer floor, Direction direction) {
                this.time = time;
                this.floor = floor;
                this.direction = direction;
            }

            @Override
            public String toString() {
                return "Request [time=" + time + ", floor=" + floor + ", direction=" + direction + "]";
            }
        }
    
        public enum Direction {
            UP, DOWN
        }
    
        public enum State {
            MOVING, STOPPED
        }
    
        private int currentFloor;
        private Direction direction;
        private State state;
        
        public final Comparator<Request> upComparator = new Comparator<Request>() {
            public int compare(Request u1, Request u2) {
                return u1.floor.compareTo(u2.floor);
            }
        };
        public final Comparator<Request> downComparator = upComparator.reversed();
    
        private Queue<Request> currentQueue;
        private Queue<Request> upQueue;
        private Queue<Request> downQueue;
        private final Set<IFloorEventListener> floorReachedEventListeners;

        public ElevatorMotor() {
            this.currentFloor = 0;
            this.direction = Direction.UP;
            this.state = State.STOPPED;
            this.upQueue = new PriorityQueue<Request>(upComparator);
            this.currentQueue = upQueue;
            this.downQueue = new PriorityQueue<Request>(downComparator);
            this.floorReachedEventListeners = new HashSet<IFloorEventListener>();
            floorReachedEventListeners.add(f -> {
                System.out.println("Elevator reached floor " + f);
            });
        }

        public void call(int floor, Direction direction) {
            switch (direction) {
                case UP:
                    upQueue.add(new Request(System.currentTimeMillis(), floor, direction));
                    break;
                case DOWN:
                    downQueue.add(new Request(System.currentTimeMillis(), floor, direction));
                    break;
                default:
                    break;
            }
        }

        public void call(int floor) {
            if (floor > currentFloor) {
                call(floor, Direction.UP);
            } else if (floor < currentFloor) {
                call(floor, Direction.DOWN);
            }
        }

        public void move() {
            if (currentQueue.isEmpty()) {
                if (currentQueue == upQueue) {
                    currentQueue = downQueue;
                } else {
                    currentQueue = upQueue;
                }
            }
            if (!currentQueue.isEmpty()) {
                Request request = currentQueue.poll();
                currentFloor = request.floor;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Advances the simulation by deltaSeconds seconds
        public void step(double deltaSeconds) {
            for (int i = 0; i < deltaSeconds; i++) {
                move();
            }
        }
        
        public void addFloorReachedListener(IFloorEventListener listener) {
            floorReachedEventListeners.add(listener);
        }

        public void removeFloorReachedListener(IFloorEventListener listener) {
            floorReachedEventListeners.remove(listener);
        }

        public void notifyReachedFloor(int floor) {
            floorReachedEventListeners.forEach(listener -> listener.floorReached(floor));
        }

        public final Direction getCurrentDirection() {
            return direction;
        }

        public void setCurrentDirection(Direction direction) {
            this.direction = direction;
        }

        public final int getCurrentFloor() {
            return currentFloor;
        }

        public State getState() {
            return state;
        }
    }

    public static abstract class ElevatorControllerBase {

        public static final int NUM_FLOORS = 10;

        protected ElevatorMotor motor;

        public ElevatorControllerBase(ElevatorMotor motor) {
            this.motor = motor;
        }

        // called when an up or down button was pushed on a floor
        public abstract void summonButtonPushed(int floor, ElevatorMotor.Direction direction);

        // called when a button for a floor is pushed inside the car
        public abstract void floorButtonPushed(int floor);
    }
}
