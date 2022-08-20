package test.gamigo;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import test.gamigo.ElevevatorFinal.ElevatorMotor.Direction;

public class ElevevatorFinal {

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

        private final Comparator<Request> upComparator = new Comparator<Request>() {
            public int compare(Request u1, Request u2) {
                return u1.floor.compareTo(u2.floor); // ~Math.signum(u1.floor - u2.floor);
            }
        };

        private final Comparator<Request> downComparator = upComparator.reversed();

        private Queue<Request> currentQueue;
        private Queue<Request> upQueue;
        private Queue<Request> downQueue;
        private boolean canSelectFloor = true;

        private final Set<IFloorEventListener> floorReachedEventListeners;
        
        public ElevatorMotor() {
            this.currentFloor = 0;
            this.direction = Direction.UP;
            this.state = State.STOPPED;;
            this.upQueue = new PriorityQueue<Request>();
            this.currentQueue = upQueue;
            this.downQueue = new PriorityQueue<Request>(downComparator);
            this.floorReachedEventListeners = new HashSet<IFloorEventListener>();
        }

        public void step(int deltaSeconds) {
            for (int i = 0; i < deltaSeconds; i++) {
                // state machine
                switch (state) {
                case IDLE:
                    if (currentQueue.isEmpty()) {
                        if (direction == Direction.UP) {
                            currentQueue = upQueue;
                        } else {
                            currentQueue = downQueue;
                        }
                    }
                    if (!currentQueue.isEmpty()) {
                        state = State.MOVING;
                    }
                    break;
                case MOVING:
                    if (currentQueue.isEmpty()) {
                        state = State.STOPPED;
                    } else {
                        Request request = currentQueue.peek();
                        if (request.floor == currentFloor) {
                            currentQueue.poll();
                            state = State.STOPPED;
                            for (IFloorEventListener listener : floorReachedEventListeners) {
                                listener.floorReached(currentFloor);
                            }
                        } else {
                            if (request.floor > currentFloor) {
                                currentFloor++;
                            } else {
                                currentFloor--;
                            }
                        }
                    }
                    break;
                case STOPPED:
                    if (currentQueue.isEmpty()) {
                        state = State.IDLE;
                    } else {
                        state = State.MOVING;
                    }
                    break;
                }
            }
        }

        /*
         * Internal floor selction logic
         */
        private void call(int currentFloor, Direction targetDirection) {
            if (!canSelectFloor) {
                return;
            }

            switch (targetDirection) {
                case UP:
                    upQueue.add(new Request(System.currentTimeMillis(), currentFloor, targetDirection));
                    break;
                case DOWN:
                    downQueue.add(new Request(System.currentTimeMillis(), currentFloor, targetDirection));
                    break;
                default: throw new IllegalArgumentException("Unknown direction: " + targetDirection);
            }
        }

        private void call(int floor) {
            switch (floor - currentFloor) {
                case  0 -> { break; }
                case +1 -> call(floor, Direction.UP);
                case -1 -> call(floor, Direction.DOWN);
                default -> call(floor, floor > currentFloor ? Direction.UP : Direction.DOWN);
            }
        }

        /*
         * States are: IDLE, STOPPED, MOVING
         */

        // Idle state is when the elevator is not moving and ready to accept new requests for 5 seconds
        private void idle() {
            if (currentQueue.isEmpty()) {
                if (currentQueue == upQueue) {
                    currentQueue = downQueue;
                } else {
                    currentQueue = upQueue;
                }
            }
            if (currentQueue.isEmpty()) {
                state = State.IDLE;
                canSelectFloor = true;
                return;
            }

            state = State.MOVING;
        }

        // Move state is when the elevator is moving to a floor
        private int move(int deltaSeconds) {
            if (currentQueue.isEmpty()) {
                if (currentQueue == upQueue) {
                    currentQueue = downQueue;
                } else {
                    currentQueue = upQueue;
                }
            } if (currentQueue.isEmpty()) {
                if (currentQueue == upQueue) {
                    currentQueue = downQueue;
                } else {
                    currentQueue = upQueue;
                }
            }

            var request = currentQueue.peek();
            if (request.floor == currentFloor) {
                state = State.STOPPED;
                return deltaSeconds;
            }

            if (request.floor > currentFloor) {
                currentFloor++;
            } else {
                currentFloor--;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            deltaSeconds--;
            state = State.MOVING;

            return deltaSeconds;
        }

        // Stop state is when the elevator is stopped at a floor
        private void stop() {
            // Guard
            var request = currentQueue.peek();
            if (request.floor == currentFloor) {
                currentQueue.poll();
                for (var listener : floorReachedEventListeners) {
                    listener.floorReached(currentFloor);
                }
            }

            state = State.STOPPED;
        }

        private boolean shouldIdle() {
            return upQueue.isEmpty() && downQueue.isEmpty();
        }

        public void addFloorReachedListener(IFloorEventListener listener) {
            floorReachedEventListeners.add(listener);
        }

        public void removeFloorReachedListener(IFloorEventListener listener) {
            floorReachedEventListeners.remove(listener);
        }

        public void notifyReachedFloor(int floor) {
            for (var listener : floorReachedEventListeners) {
                listener.floorReached(floor);
            }
        }
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
}
