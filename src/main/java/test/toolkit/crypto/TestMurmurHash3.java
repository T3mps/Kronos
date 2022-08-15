package test.toolkit.crypto;

import java.util.Random;

import net.acidfrog.kronos.toolkit.crypto.MurmurHash3;

public final class TestMurmurHash3 {
    
    public void testHash32() {
        int length = 100_000;
        int[] hashes = new int[length];
        Random random = new Random();
        
        for (int i = 0; i < length; i++) {
            byte[] data = new byte[16];
            random.nextBytes(data);
            hashes[i] = MurmurHash3.hash32(data);
        }

        // check for collisions
        int collisions = 0;
        for (int i = 0; i < length; i++) {
            for (int j = i + 1; j < length; j++) {
                if (hashes[i] == hashes[j]) {
                    collisions++;
                    System.out.println("collision: " + i + " " + j);
                }
            }
        }

        System.out.println("collisions: " + collisions);
    }

    public static void main(String[] args) {
        TestMurmurHash3 test = new TestMurmurHash3();
        test.testHash32();
    }
}
