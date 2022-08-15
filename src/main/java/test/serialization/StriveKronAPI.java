package test.serialization;

import net.acidfrog.kronos.serialization.Kron;

public class StriveKronAPI {
    
    public static void main(String[] args) {
        Kron kron = new Kron(); // create a new kron instance

        kron.defineNextMemoryLayout(Player.class); // define the memory layout for the target class to be serialized
    }

    public static class Player {
        
        public boolean isAlive;
        public byte state;
        public short level;
        public int x;
        public int y;
        public float health;
        public double mana;
        public long xp;

        public boolean[] states;
        public byte[] offsets;
        public short[] levels;
        public int[] xs;
        public int[] ys;
        public long[] xpPool;
        public float[] milestones;
        public double[] manaPool;

        public transient String name;
        public transient Item[] inventory;

        public Player(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.health = 100;
            this.isAlive = true;
            this.inventory = new Item[] { new Weapon("Great Sword +2", 14), new Armor("Shield +1", 3) };
            this.state = -1;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Player: ").append(name).append("\n");
            sb.append("X: ").append(x).append("\n");
            sb.append("Y: ").append(y).append("\n");
            sb.append("Health: ").append(health).append("\n");
            sb.append("Is Alive: ").append(isAlive).append("\n");
            sb.append("Inventory: ").append("\n");
            for (Item item : inventory) {
                sb.append("\t").append(item).append("\n");
            }
            return sb.toString();
        }
    }

    public abstract static class Item {

        public int id;
        public String name;

        public Item() {
            this.id = -1;
            this.name = "";
        }
        
        public Item(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
    
    public static class Weapon extends Item {
        
        public int damage;
        
        public Weapon(String name, int damage) {
            super(0, name);
            this.damage = damage;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Item(").append(id).append(") ").append(name);
            sb.append("\s|\sDamage: ").append(damage);
            return sb.toString();
        }
    }

    public static class Armor extends Item {
        
        public int defense;
        
        public Armor(String name, int defense) {
            super(1, name);
            this.defense = defense;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Item(").append(id).append(") ").append(name);
            sb.append("\s\s\s\s\s\s|\sDefense: ").append(defense);
            return sb.toString();
        }
    }
}
