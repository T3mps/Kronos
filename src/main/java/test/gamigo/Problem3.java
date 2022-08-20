package test.gamigo;

import java.util.HashMap;
import java.util.Map;

public class Problem3 {

    private enum Direction {
        NORTH, SOUTH, EAST, WEST
    }
    
    public static class Room {

        private static int ID = 0;
        private static final Map<String, Room> ROOMS = new HashMap<String, Room>();

        private final String name;
        private Room north;
        private Room south;
        private Room east;
        private Room west;

        public Room() {
            this("Room " + ID++);
        }

        public Room(String name) {
            this(name, null, null, null, null);
        }

        public Room(String name, Room north, Room south, Room east, Room west) {
            this.name = name;
            this.north = north;
            this.south = south;
            this.east = east;
            this.west = west;
            ROOMS.put(name, this);
        }

        protected void connect(Room room, Direction direction) {
            switch (direction) {
                case NORTH -> setNorth(room);
                case SOUTH -> setSouth(room);
                case EAST  -> setEast(room);
                case WEST  -> setWest(room);
                default    -> throw new IllegalArgumentException("Invalid direction: " + direction);
            }
        }

        public boolean pathExists(String name) {
            return pathExists(ROOMS.get(name));
        }

        public boolean pathExists(Room room) {
            if (room == null) {
                return false;
            }

            if (this == room) {
                return true;
            }

            if (north != null && north.pathExists(room)) {
                return true;
            }

            if (south != null && south.pathExists(room)) {
                return true;
            }

            if (east != null && east.pathExists(room)) {
                return true;
            }

            if (west != null && west.pathExists(room)) {
                return true;
            }

            return false;
        }

        public String getName() {
            return name;
        }

        public Room getNorth() {
            return north;
        }

        public Room setNorth(Room north) {
            this.north = north;
            return this;
        }

        public Room getSouth() {
            return south;
        }

        public Room setSouth(Room south) {
            this.south = south;
            return this;
        }

        public Room getEast() {
            return east;
        }

        public Room setEast(Room east) {
            this.east = east;
            return this;
        }

        public Room getWest() {
            return west;
        }

        public Room setWest(Room west) {
            this.west = west;
            return this;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((east == null) ? 0 : east.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((north == null) ? 0 : north.hashCode());
            result = prime * result + ((south == null) ? 0 : south.hashCode());
            result = prime * result + ((west == null) ? 0 : west.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Room other = (Room) obj;
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
            if (north == null) {
                if (other.north != null) {
                    return false;
                }
            } else if (!north.equals(other.north)) {
                return false;
            }
            if (south == null) {
                if (other.south != null) {
                    return false;
                }
            } else if (!south.equals(other.south)) {
                return false;
            }
            if (east == null) {
                if (other.east != null) {
                    return false;
                }
            } else if (!east.equals(other.east)) {
                return false;
            }
            if (west == null) {
                if (other.west != null) {
                    return false;
                }
            } else if (!west.equals(other.west)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Room [name=");
            builder.append(name);
            builder.append(", north=");
            builder.append(north == null ? "null" : north.getName());
            builder.append(", south=");
            builder.append(south == null ? "null" : south.getName());
            builder.append(", east=");
            builder.append(east == null ? "null" : east.getName());
            builder.append(", west=");
            builder.append(west == null ? "null" : west.getName());
            builder.append("]");
            return builder.toString();
        }
    }

    public static void main(String[] args) {
        var rm1 = new Room("Room 1");
        var rm2 = new Room("Room 2");
        var rm3 = new Room("Room 3");
        var rm4 = new Room("Room 4");
        var rm5 = new Room("Room 5");
        var rm6 = new Room("Room 6");
        var rm7 = new Room("Room 7");
        var rm8 = new Room("Room 8");
        var rm9 = new Room("Room 9");

        rm1.connect(rm2, Direction.EAST);
        rm1.connect(rm8, Direction.NORTH);
        rm8.connect(rm9, Direction.EAST);
        rm2.connect(rm3, Direction.SOUTH);
        rm3.connect(rm4, Direction.EAST);
        rm4.connect(rm5, Direction.EAST);
        rm4.connect(rm6, Direction.NORTH);
        rm6.connect(rm7, Direction.EAST);

        System.out.println(rm1.pathExists("Room 7"));
    }
}
