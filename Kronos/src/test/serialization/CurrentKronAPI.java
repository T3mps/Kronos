package test.serialization;

import com.starworks.kronos.serialization.old.Serializable;
import com.starworks.kronos.toolkit.internal.memory.MemoryBlock;

public class CurrentKronAPI {
    
    public static void main(String[] args) {
        byte[] bytes = new byte[1024];
        MemoryBlock block = new MemoryBlock(bytes);

        var player1 = new Player("Temps", 12, 32);
        player1.serialize(block);

        var player2 = new Player();
        player2.deserialize(block);

        System.out.println(player1);
        System.out.println(player2);
    }

    public static class Player implements Serializable {
        
        public String name;
        public int x;
        public int y;
        public float health;
        public boolean isAlive;
        public Item[] inventory;

        public Player() {
            this.name = "";
            this.x = 0;
            this.y = 0;
            this.health = 0;
            this.isAlive = false;
            this.inventory = new Item[0];
        }

        public Player(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.health = 100;
            this.isAlive = true;
            this.inventory = new Item[] { new Weapon("Great Sword +2", 14), new Armor("Shield +1", 3) };
        }

        @Override
        public void serialize(MemoryBlock block) {
            block.writeString(name);
            block.writeInt(x);
            block.writeInt(y);
            block.writeFloat(health);
            block.writeBoolean(isAlive);
            block.writeInt(inventory.length);
            for (Item item : inventory) {
                item.serialize(block);
            }
        }

        @Override
        public Player deserialize(MemoryBlock block) {
            block.resetPointer();
            this.name = block.readString();
            this.x = block.readInt();
            this.y = block.readInt();
            this.health = block.readFloat();
            this.isAlive = block.readBoolean();
            int size = block.readInt();
            this.inventory = new Item[size];
            
            for (int i = 0; i < size; i++) {
                Item item;
                int id = block.readInt();
                String name = block.readString();

                switch (id) {
                    case 0:
                        item = new Weapon(name, block.readInt());
                        break;
                    case 1:
                        item = new Armor(name, block.readInt());
                        break;
                    default:
                        throw new RuntimeException("Unknown item type: " + id);
                }

                this.inventory[i] = item;
            }

            return this;
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

    public abstract static class Item implements Serializable {

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

        @Override
        public void serialize(MemoryBlock block) {
            block.writeInt(id);
            block.writeString(name);
        }

        @Override
        public Item deserialize(MemoryBlock block) {
            this.id = block.readInt();
            this.name = block.readString();
            return this;
        }
    }
    
    public static class Weapon extends Item {
        
        public int damage;
        
        public Weapon(String name, int damage) {
            super(0, name);
            this.damage = damage;
        }

        @Override
        public void serialize(MemoryBlock block) {
            super.serialize(block);
            block.writeInt(damage);
        }

        @Override
        public Weapon deserialize(MemoryBlock block) {
            super.deserialize(block);
            damage = block.readInt();
            return this;
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
        public void serialize(MemoryBlock block) {
            super.serialize(block);
            block.writeInt(defense);
        }

        @Override
        public Armor deserialize(MemoryBlock block) {
            super.deserialize(block);
            defense = block.readInt();
            return this;
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
