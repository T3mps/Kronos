package test.gamigo;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import test.gamigo.Ele.ElevatorMotor.*;

public class Ele {

    public static class ElevatorController extends ElevatorControllerBase {

        public ElevatorController(ElevatorMotor motor) {
            super(motor);
            this.motor.addFloorReachedListener(f -> {
                System.out.println("Elevator reached floor " + f);
            });
        }

        public void update(int dt) {
            motor.step(dt);
        }

        @Override
        public void summonButtonPushed(int floor, Direction direction) {
            motor.call(floor, direction);
        }

        @Override
        public void floorButtonPushed(int floor) {
            motor.call(floor);
            motor.move(1);
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
    
        public static enum Direction {
            UP, DOWN
        }
    
        public static enum State {
            MOVING, STOPPED, IDLE
        }
    
        private int currentFloor;
        private Direction direction;
        private State state;

        public final Comparator<Request> upComparator = new Comparator<Request>() {
            @Override
            public int compare(Request u1, Request u2) {
                return u1.floor.compareTo(u2.floor); // ~Math.signum(u1.floor - u2.floor);
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
        }

        public void addFloorReachedListener(IFloorEventListener listener) {
            floorReachedEventListeners.add(listener);
        }

        public void step(int deltaSeconds) {
            for (int i = 0; i < deltaSeconds; i++) {
                move(deltaSeconds);
            }
        }

        public void idle() {
            System.out.println("Elevator is waiting");
        }

        private void call(int floor, Direction direction) {
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

            System.out.println("Elevator called to floor " + floor + " going " + direction);
        }

        private void call(int floor) {
            switch (floor - currentFloor) {
                case 0:
                    break;
                case 1:
                    call(floor, Direction.UP);
                    break;
                case -1:
                    call(floor, Direction.DOWN);
                    break;
                default:
                    call(floor, floor > currentFloor ? Direction.UP : Direction.DOWN);
                    break;
            }
        }

        private int move(int deltaSeconds) {
            if (deltaSeconds <= 0) {
                deltaSeconds = 0;
                return deltaSeconds;
            }
            if (currentQueue.isEmpty()) {
                if (currentQueue == upQueue) {
                    currentQueue = downQueue;
                } else {
                    currentQueue = upQueue;
                }
            }
            if (!currentQueue.isEmpty()) {
                state = State.MOVING;
                direction = currentQueue.peek().direction;
                try {
                    Thread.sleep(500);
                    System.out.println("Elevator moving " + direction + " to floor " + currentQueue.peek().floor);
                    int multiplier = Math.abs(currentQueue.peek().floor - currentFloor);
                    Thread.sleep(1000 * multiplier);
                    deltaSeconds -= multiplier;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Request request = currentQueue.poll();
                currentFloor = request.floor;
                state = State.STOPPED;

                for (var listener : floorReachedEventListeners) {
                    listener.floorReached(currentFloor);
                }
            }

            return deltaSeconds;
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
        controller.summonButtonPushed(3, Direction.UP);
        controller.summonButtonPushed(5, Direction.UP);
        controller.summonButtonPushed(2, Direction.DOWN);
        controller.summonButtonPushed(1, Direction.UP);

        for (int i = 0; i < 100; i++) {
            controller.update(1);
            if (i == 1) {
                controller.floorButtonPushed(5);
            }
        }
    }
}
