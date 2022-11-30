package test;

import com.starworks.kronos.logging.Logger;
import com.starworks.kronos.logging.LoggerFactory;

public class Test {
    
    private static class Foo {

        public int a;

        public Foo() {
            a = 10;
        }

        @Override
        public String toString() {
            return "Foo{" + "a=" + a + '}';
        }
    }

    public static void main(String[] args) {
        Logger logger = LoggerFactory.get(Test.class);
        Foo foo = new Foo();

        logger.info("This foo object {0} is amazing!", foo, foo, foo);
    }
}
