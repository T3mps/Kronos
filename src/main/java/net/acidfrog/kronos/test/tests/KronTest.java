package net.acidfrog.kronos.test.tests;

import java.io.IOException;

import net.acidfrog.kronos.core.io.serialization.Kron;
import net.acidfrog.kronos.core.io.serialization.KronException;
import net.acidfrog.kronos.core.io.serialization.Serializable;

public class KronTest {

    @Serializable
    public static class Player {

        transient int x = 32;
        transient int y = 45;
        transient int z = 67;

        boolean inCombat = false;
        transient boolean isDead = false;

    }

    @Serializable
    public static class Enemy {

        transient int x = 48;
        transient int y = 60;
        transient int z = 67;

        boolean attackingPlayer = false;
        transient boolean isDead = true;

    }

    @Serializable
    public static class World {

        transient int[] entityData = new int[] { 0x01, 0x02 };
        transient int width = 100;
        transient int height = 100;
        transient boolean isDay = true;

    }

    public static void main(String[] args) throws KronException, IOException, IllegalArgumentException, IllegalAccessException {
        Player player = new Player();
        Enemy enemy = new Enemy();
        World world = new World();
        String name = "game_data";

        Kron kron = new Kron(name);
        kron.serializeToFile(name, player, enemy, world);

        // int[] data = new int[32768];
        // for (int i = 0; i < data.length; i++) data[i] = Mathf.random(i);

        // KronDatabase database = new KronDatabase("Database");

        // KronArray array = KronArray.create("RandomIntegers", data);
        // KronField field = KronField.create("TheAnswer", 42);
        // KronString string = KronString.create("Name", "Ethan Temprovich");

        // KronObject object = new KronObject("Object");

        // object.addArray(array);
        // object.addField(field);
        // object.addString(string);

        // database.addObject(object);
        
        // byte[] stream = new byte[database.size()];
        // database.getBytes(stream, 0);
        // database.serializeToFile("test2");
    }
    
}
