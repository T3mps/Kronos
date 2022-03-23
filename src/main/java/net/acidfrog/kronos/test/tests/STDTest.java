package net.acidfrog.kronos.test.tests;

import net.acidfrog.kronos.core.lang.Std;

public class STDTest {
	
	public static void tupleTest() {
		Std.Pair<String, Integer> p = new Std.Pair<String, Integer>("Hello", 42);
		System.out.println(p.getPart1() + " " + p.getPart2());
		Std.Triple<String, Integer, Double> t = new Std.Triple<String, Integer, Double>("Hello", 42, 3.14);
		System.out.println(t.getPart1() + " " + t.getPart2() + " " + t.getPart3());
		Std.Quad<String, Integer, Double, Float> q4 = new Std.Quad<String, Integer, Double, Float>("Hello", 42, 3.14, 3.14f);
		System.out.println(q4.getPart1() + " " + q4.getPart2() + " " + q4.getPart3() + " " + q4.getPart4());
		Std.Quint<String, Integer, Double, Float, Long> q5 = new Std.Quint<String, Integer, Double, Float, Long>("Hello", 42, 3.14, 3.14f, 42L);
		System.out.println(q5.getPart1() + " " + q5.getPart2() + " " + q5.getPart3() + " " + q5.getPart4() + " " + q5.getPart5());
	}

	public static void booleansTest() {
		System.out.println(Std.Booleans.and(Std.Booleans.ON , Std.Booleans.NO,  Std.Booleans.FALSE));
		System.out.println(Std.Booleans.or( Std.Booleans.OFF, Std.Booleans.YES, Std.Booleans.FALSE));
		System.out.println(Std.Booleans.xor(Std.Booleans.OFF, Std.Booleans.NO,  Std.Booleans.TRUE ));

		boolean bool0 = true;
		byte bool1 = 0;
		short bool2 = 2;
		char bool3 = 1;
		int bool4 = 101;
		long bool5 = 1001;
		float bool6 = 1.1f;
		double bool7 = 1.11;

		System.out.println(Std.Booleans.toBoolean(bool1));
		System.out.println(Std.Booleans.toBoolean(bool2));
		System.out.println(Std.Booleans.toBoolean(bool3));
		System.out.println(Std.Booleans.toBoolean(bool4));
		System.out.println(Std.Booleans.toBoolean(bool5));
		System.out.println(Std.Booleans.toBoolean(bool6));
		System.out.println(Std.Booleans.toBoolean(bool7));

		System.out.println(Std.Booleans.toByte(bool0));
		System.out.println(Std.Booleans.toShort(bool0));
		System.out.println(Std.Booleans.toChar(bool0));
		System.out.println(Std.Booleans.toInt(bool0));
		System.out.println(Std.Booleans.toLong(bool0));
		System.out.println(Std.Booleans.toFloat(bool0));
		System.out.println(Std.Booleans.toDouble(bool0));
	}

	public static void main(String[] args) {
		// tupleTest();
		// booleansTest();
		for (int i = 0; i < 256; i++) {
			System.out.println(Std.Strings.Generator.randomName() + " from " + Std.Strings.Generator.randomTownName());
		}
		
	}

}
