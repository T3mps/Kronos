package net.acidfrog.kronos.test.tests;

import net.acidfrog.kronos.mathk.Mathk;
import net.acidfrog.kronos.mathk.Matrix2k;
import net.acidfrog.kronos.mathk.Vector2k;

public class MathfTest {
    
    static final int iterations = 128 * 128;

    static void randomTest0To1() {
        System.out.println("Random 0 to 1:");
        for (int i = 0; i < iterations; i++) {
            System.out.println(Mathk.random());
        }
    }

    static void randomTest0ToN(int n) {
        System.out.println("Random 0 to " + n + ":");
        for (int i = 0; i < iterations; i++) {
            System.out.println(Mathk.random(n));
        }
    }
    
    static void randomTestNToM(int n, int m) {
        System.out.println("Random " + n + " to " + m + ":");
        for (int i = 0; i < iterations; i++) {
            System.out.println(Mathk.random(n, m));
        }
    }

    static void randomTestBoolean() {
        System.out.println("Random boolean:");
        for (int i = 0; i < iterations; i++) {
            System.out.println(Mathk.randomBoolean());
        }
    }

    static void randomTestSign() {
        System.out.println("Random sign:");
        for (int i = 0; i < iterations; i++) {
            System.out.println(Mathk.randomSign());
        }
    }

    static void randomTestRadians() {
        System.out.println("Random radians:");
        for (int i = 0; i < iterations; i++) {
            System.out.println(Mathk.randomRadians());
        }
    }

    static void randomTestDegrees() {
        System.out.println("Random degrees:");
        for (int i = 0; i < iterations; i++) {
            System.out.println(Mathk.randomDegrees());
        }
    }

    static void trigonometryTestSine() {
        System.out.println("Sine:");
        for (int i = 0; i < iterations; i++) {
            float rad = Mathk.randomRadians();
            System.out.println("sin(" + rad + ") = " + Mathk.sin(rad));
        }
    }

    static void trigonometryTestCosine() {
        System.out.println("Cosine:");
        for (int i = 0; i < iterations; i++) {
            float rad = Mathk.randomRadians();
            System.out.println("cos(" + rad + ") = " + Mathk.cos(rad));
        }
    }

    static void trigonometryTestTangent() {
        System.out.println("Tangent:");
        for (int i = 0; i < iterations; i++) {
            float rad = Mathk.randomRadians();
            System.out.println("tan(" + rad + ") = " + Mathk.tan(rad));
        }
    }

    static void trigonometryTestATan2() {
        System.out.println("aTan2:");
        for (int i = 0; i < iterations; i++) {
            float y = Mathk.random(iterations / 2);
            float x = Mathk.random(iterations / 2);
            System.out.println("atan2(" + y + ", " + x + ") = " + Mathk.atan2(y, x));
        }
    }

    static void functionsTestFMA() {
        System.out.println("FMA:");
        for (int i = 0; i < iterations; i++) {
            float a = Mathk.random(iterations / 2);
            float b = Mathk.random(iterations / 2);
            float c = Mathk.random(iterations / 2);
            System.out.println("fma(" + a + ", " + b + ", " + c + ") = " + Mathk.fma(a, b, c));
        }
    }

    static void functionsTestNextPowerOfTwo() {
        System.out.println("Next power of two:");
        for (int i = 0; i < iterations; i++) {
            int n = Mathk.random(iterations);
            System.out.println("nextPowerOfTwo(" + n + ") = " + Mathk.nextPowerOfTwo(n));
        }
    }

    static void functionsTestIsPowerOfTwo() {
        System.out.println("Is power of two:");
        for (int i = 0; i < iterations; i++) {
            int n = Mathk.random(iterations);
            System.out.println("isPowerOfTwo(" + n + ") = " + Mathk.isPowerOfTwo(n));
        }
    }

    static void functionsTestMin() {
        System.out.println("Min:");
        for (int i = 0; i < iterations; i++) {
            float a = Mathk.random(iterations / 2);
            float b = Mathk.random(iterations / 2);
            System.out.println("min(" + a + ", " + b + ") = " + Mathk.min(a, b));
        }
    }

    static void functionsTestMax() {
        System.out.println("Max:");
        for (int i = 0; i < iterations; i++) {
            float a = Mathk.random(iterations / 2);
            float b = Mathk.random(iterations / 2);
            System.out.println("max(" + a + ", " + b + ") = " + Mathk.max(a, b));
        }
    }

    static void functionsTestClamp() {
        System.out.println("Clamp:");
        for (int i = 0; i < iterations; i++) {
            float a = Mathk.random(iterations / 4);
            float b = Mathk.random(iterations / 2) - a;
            float c = Mathk.random(iterations * 2) + b;
            System.out.println("clamp(" + a + ", " + b + ", " + c + ") = " + Mathk.clamp(a, b, c));
        }
    }

    static void functionsTestMap() {
        System.out.println("Map:");
        for (int i = 0; i < iterations; i++) {
            float inRangeStart = Mathk.random(iterations / 2);
            float inRangeEnd = Mathk.random(iterations) + inRangeStart;
            float outRangeStart = Mathk.random(iterations * 2) + inRangeEnd;
            float outRangeEnd = Mathk.random(iterations * 4) + outRangeStart;
            float value = inRangeStart + Mathk.random(inRangeStart / 4);
            System.out.println("map(" + inRangeStart + ", " + inRangeEnd + ", " + outRangeStart + ", " + outRangeEnd + ", " + value + ") = " + Mathk.map(inRangeStart, inRangeEnd, outRangeStart, outRangeEnd, value));
        }
    }

    static void functionsTestLerp() {
        System.out.println("Lerp:");
        float a = Mathk.random(10) + Mathk.E;
        float b = iterations;
        float delta = 1 / b;
        
        for (int i = 0; i < iterations; i++) {
            float d = i * delta;
            System.out.println("lerp(" + a + ", " + b + ", " + d + ") = " + Mathk.lerp(a, b, d));
        }
    }

    static void functionsTestApproach() {
        System.out.println("Approach:");
        float a = Mathk.random(10) + Mathk.E;
        float b = iterations;
        float delta = 1 / b;
        
        for (int i = 0; i < iterations; i++) {
            float d = i * delta;
            System.out.println("approach(" + a + ", " + b + ", " + d + ") = " + Mathk.approach(a, b, d));
        }
    }

    static void functionsTestLerpAngle() {
        System.out.println("Lerp:");
        float a = Mathk.randomRadians();
        float b = iterations / Mathk.PI;
        float delta = Mathk.PI / b;
        
        for (int i = 0; i < iterations; i++) {
            float d = i * delta;
            System.out.println("lerpAngle(" + a + ", " + b + ", " + d + ") = " + Mathk.lerpAngle(a, b, d));
        }
    }

    static void functionsTestSqrt() {
        System.out.println("Sqrt:");
        for (int i = 0; i < iterations; i++) {
            float n = Mathk.random(iterations);
            System.out.println("sqrt(" + n + ") = " + Mathk.sqrt(n));
        }
    }

    static void functionsTestInvsqrt() {
        System.out.println("Inv sqrt:");
        for (int i = 0; i < iterations; i++) {
            float n = Mathk.random(iterations);
            System.out.println("invsqrt(" + n + ") = " + Mathk.invsqrt(n));
        }
    }

    static void functionsTestRotateVector2AroundPoint() {
        System.out.println("Rotate vector 2 around point:");

        for (int i = 0; i < iterations; i++) {
            float x = Mathk.random(iterations);
            float y = Mathk.random(iterations);
            float ox = 0f;
            float oy = 0f;
            float angle = Mathk.randomRadians();
            System.out.println("rotateVector2AroundPoint(<" + x + ", " + y + ">, " + angle + ", <" + ox + ", " + oy +">) = " + Mathk.rotate(new Vector2k(x, y), angle, new Vector2k(ox, oy)));
        }
    }

    static void functionsTestFFT() {
        System.out.println("FFT:");
        float[] data = new float[iterations];

        for (int i = 0; i < iterations; i++) data[i] = Mathk.random(iterations);
        int offset = Mathk.random(4);
        Matrix2k transform = new Matrix2k(Mathk.fft(data), offset);
        System.out.println("fft(" + data[offset + 0] + ", " + data[offset + 1] + ", " + data[offset + 2] + ", " + data[offset + 3] + ") = " + transform);
    }
    
    static void functionsTestTripleProduct() {
        System.out.println("Triple product:");
        for (int i = 0; i < iterations; i++) {
            float x1 = Mathk.random(iterations);
            float y1 = Mathk.random(iterations);
            float x2 = Mathk.random(iterations);
            float y2 = Mathk.random(iterations);
            float x3 = Mathk.random(iterations);
            float y3 = Mathk.random(iterations);
            System.out.println("tripleProduct(<" + x1 + ", " + y1 + ">, <" + x2 + ", " + y2 + ">, <" + x3 + ", " + y3 + ">) = " + Mathk.tripleProduct(new Vector2k(x1, y1), new Vector2k(x2, y2), new Vector2k(x3, y3)));
        }
    }

    public static void main(String[] args) {
        // randomTest0To1();
        // randomTest0ToN(10);
        // randomTestNToM(10, 20);
        // randomTestBoolean();
        // randomTestSign();
        // randomTestRadians();
        // randomTestDegrees();
        // trigonometryTestSine();
        // trigonometryTestCosine();
        // trigonometryTestTangent();
        // trigonometryTestATan2();
        // functionsTestFMA();
        // functionsTestNextPowerOfTwo();
        // functionsTestIsPowerOfTwo();
        // functionsTestMin();
        // functionsTestMax();
        // functionsTestClamp();
        // functionsTestMap();
        // functionsTestLerp();
        // functionsTestApproach();
        // functionsTestLerpAngle();
        // functionsTestSqrt();
        // functionsTestInvsqrt();
        // functionsTestRotateVector2AroundPoint();
        // functionsTestFFT();
        // functionsTestTripleProduct();
    }

}
