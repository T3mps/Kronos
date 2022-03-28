package net.acidfrog.kronos.core.lang;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import net.acidfrog.kronos.core.lang.logger.Logger;
import net.acidfrog.kronos.mathk.Mathk;

/**
 * The 'Standard Library' for Kronos. Contains inner$classes
 * that operate on JDK classes (such as String), and tuples.
 * 
 * @author Ethan Temprovich
 */
public final class Std {

	public static class Pair<T, U> implements Comparable<Pair<T, U>> {

		public T p1;
		public U p2;

		public Pair(T p1, U p2) {
			this.p1 = p1;
			this.p2 = p2;
		}

		public Pair() {
			this.p1 = null;
			this.p2 = null;
		}

		public T getPart1() { return p1; }

		public void setPart1(T p1) { this.p1 = p1; }

		public U getPart2() { return p2; }
		
		public void setPart2(U p2) { this.p2 = p2; }

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Pair [p1=");
			builder.append(p1);
			builder.append(", p2=");
			builder.append(p2);
			builder.append("]");
			return builder.toString();
		}

		@Override
		public int compareTo(Pair<T, U> o) {
			if (o.getPart1().equals(this.getPart1()) && o.getPart2().equals(this.getPart2())) return 0;
			return 1;
		}

	}

	public static class Triple<T, U, P> implements Comparable<Triple<T, U, P>> {

		public T p1;
		public U p2;
		public P p3;

		public Triple(T p1, U p2, P p3) {
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
		}

		public Triple() {
			this.p1 = null;
			this.p2 = null;
			this.p3 = null;
		}

		public T getPart1() { return p1; }

		public void setPart1(T p1) { this.p1 = p1; }

		public U getPart2() { return p2; }

		public void setPart2(U p2) { this.p2 = p2; }

		public P getPart3() { return p3; }

		public void setPart3(P p3) { this.p3 = p3; }

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Triple [p1=");
			builder.append(p1);
			builder.append(", p2=");
			builder.append(p2);
			builder.append(", p3=");
			builder.append(p3);
			builder.append("]");
			return builder.toString();
		}

		@Override
		public int compareTo(Triple<T, U, P> o) {
			if (o.getPart1().equals(this.getPart1()) && o.getPart2().equals(this.getPart2()) &&
				o.getPart3().equals(this.getPart3())) return 0;
			return 1;
		}
	
	}
	
	public static class Quad<T, U, P, L>  implements Comparable<Quad<T, U, P, L>> {

		public T p1;
		public U p2;
		public P p3;
		public L p4;

		public Quad(T p1, U p2, P p3, L p4) {
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
			this.p4 = p4;
		}

		public Quad() {
			this.p1 = null;
			this.p2 = null;
			this.p3 = null;
			this.p4 = null;
		}

		public T getPart1() { return p1; }

		public void setPart1(T p1) { this.p1 = p1; }

		public U getPart2() { return p2; }

		public void setPart2(U p2) { this.p2 = p2; }

		public P getPart3() { return p3; }

		public void setPart3(P p3) { this.p3 = p3; }

		public L getPart4() { return p4; }

		public void setPart4(L p4) { this.p4 = p4; }

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Quad [p1=");
			builder.append(p1);
			builder.append(", p2=");
			builder.append(p2);
			builder.append(", p3=");
			builder.append(p3);
			builder.append(", p4=");
			builder.append(p4);
			builder.append("]");
			return builder.toString();
		}

		@Override
		public int compareTo(Quad<T, U, P, L> o) {
			if (o.getPart1().equals(this.getPart1()) && o.getPart2().equals(this.getPart2()) &&
				o.getPart3().equals(this.getPart3()) && o.getPart4().equals(this.getPart4())) return 0;
			return 1;
		}

	}

	public static class Quint<T, U, P, L, E> implements Comparable<Quint<T, U, P, L, E>> {

		public T p1;
		public U p2;
		public P p3;
		public L p4;
		public E p5;

		public Quint(T p1, U p2, P p3, L p4, E p5) {
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
			this.p4 = p4;
			this.p5 = p5;
		}

		public Quint() {
			this.p1 = null;
			this.p2 = null;
			this.p3 = null;
			this.p4 = null;
			this.p5 = null;
		}

		public T getPart1() { return p1; }

		public void setPart1(T p1) { this.p1 = p1; }

		public U getPart2() { return p2; }

		public void setPart2(U p2) { this.p2 = p2; }

		public P getPart3() { return p3; }

		public void setPart3(P p3) { this.p3 = p3; }

		public L getPart4() { return p4; }

		public void setPart4(L p4) { this.p4 = p4; }

		public E getPart5() { return p5; }

		public void setPart5(E p5) { this.p5 = p5; }

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Quint [p1=");
			builder.append(p1);
			builder.append(", p2=");
			builder.append(p2);
			builder.append(", p3=");
			builder.append(p3);
			builder.append(", p4=");
			builder.append(p4);
			builder.append(", p5=");
			builder.append(p5);
			builder.append("]");
			return builder.toString();
		}

		@Override
		public int compareTo(Quint<T, U, P, L, E> o) {
			if (o.getPart1().equals(this.getPart1()) && o.getPart2().equals(this.getPart2()) &&
				o.getPart3().equals(this.getPart3()) && o.getPart4().equals(this.getPart4()) &&
				o.getPart5().equals(this.getPart5())) return 0;
			return 1;
		}

	}

	public static class Booleans {
		
		public static final boolean ON = true;
		public static final boolean OFF = false;

		public static final boolean YES = true;
		public static final boolean NO = false;

		public static final boolean TRUE = true;
		public static final boolean FALSE = false;

		public static boolean and(boolean... bools) {
			for (boolean b : bools) if (!b) return false;
			return true;
		}

		public static boolean or(boolean... bools) {
			for (boolean b : bools) if (b) return true;
			return false;
		}

		public static boolean xor(boolean... bools) {
			boolean result = false;
			for (boolean b : bools) result ^= b;
			return result;
		}

		public static boolean toBoolean(byte value) { return value != 0; }

		public static boolean toBoolean(short value) { return value != 0; }

		public static boolean toBoolean(char value) { return value != 0; }

		public static boolean toBoolean(int value) { return value != 0; }

		public static boolean toBoolean(long value) { return value != 0; }

		public static boolean toBoolean(float value) { return value != 0; }

		public static boolean toBoolean(double value) { return value != 0; }
		
		public static boolean toBoolean(String value) {
			if (value == null) return false;
			value = value.toLowerCase().trim();

			return value.equalsIgnoreCase("true") ||
				   value.equalsIgnoreCase("yes")  ||
				   value.equalsIgnoreCase("on")   ||
				   value.equalsIgnoreCase("1")    ||
				   value.equalsIgnoreCase("y");
		}

		public static byte toByte(boolean value) { return (byte) (value ? 1 : 0); }
		
		public static short toShort(boolean value) { return (short) (value ? 1 : 0); }
		
		public static char toChar(boolean value) { return (char) (value ? 1 : 0); }

		public static int toInt(boolean value) { return value ? 1 : 0; }

		public static long toLong(boolean value) { return value ? 1 : 0; }

		public static float toFloat(boolean value) { return value ? 1 : 0; }

		public static double toDouble(boolean value) { return value ? 1 : 0; }

		public static String toString(boolean value) { return value ? "true" : "false"; }

	}

	public static class Strings {

		public static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz".toCharArray();

		public static final String SPACE = " ";

		public static final String EMPTY = "";

		public static String chomp(String str) {
			nullCheck(str);
			if (isEmpty(str)) return str;

			int last = str.length() - 1;
			
			if (str.charAt(last) == '\n') {
				if (last > 0 && str.charAt(last - 1) == '\r') {
					last--;
				}
			} else if (str.charAt(last) != '\r') {
				last++;
			}

			return str.substring(0, last);
		}

		public static String chop(String str) {
			nullCheck(str);
			if (isEmpty(str)) return str;
			if (str.length() < 2) return EMPTY;

			int last = str.length() - 1;
			
			String result = str.substring(0, last);
			char lastChar = str.charAt(last);

			if (lastChar == '\n') {
				if (last > 0 && str.charAt(last - 1) == '\r') result = str.substring(0, last - 1);
			}
			return result;
		}

		public static boolean containsWhitespace(String str) {
			nullCheck(str);
			if (isEmpty(str)) return false;
			for (int i = 0; i < str.length(); i++) if (Character.isWhitespace(str.charAt(i))) return true;
			return false;
		}

		public static String deleteWhitespace(final String str) {
			if (isEmpty(str)) return str;

			final char[] result = new char[str.length()];
			int charIndex = 0;
			
			for (int i = 0; i < str.length(); i++) {
				if (str.charAt(i) == ' ' || str.charAt(i) == '\t') continue;
				result[charIndex++] = str.charAt(i);
			}
			
			if (charIndex == str.length()) return str;
			if (charIndex == 0) return EMPTY;

			return new String(result, 0, charIndex);
		}

		public static boolean startsWith(String str, String prefix) {
			nullCheck(str);
			nullCheck(prefix);
			if (isEmpty(str) || isEmpty(prefix)) return false;
			if (str.substring(0, prefix.length()).equals(prefix)) return true;
			return false;
		}

		public static boolean endsWith(String str, String suffix) {
			nullCheck(str);
			nullCheck(suffix);
			if (isEmpty(str) || isEmpty(suffix)) return false;
			if (str.substring(str.length() - suffix.length()).equals(suffix)) return true;
			return false;
		}

		public static String joinWords(char delimiter, char[]... arr) {
			StringBuilder sb = new StringBuilder(new String(arr[0]) + delimiter);
			for (int i = 1; i < arr.length; i++) sb.append(new String(arr[i]) + (i < arr.length - 1 ? delimiter : ""));
			return sb.toString();
		}

		public static <T> String join(T[] arr, String delimiter) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < arr.length; i++) sb.append(arr[i] + (i < arr.length - 1 ? delimiter : ""));
			return sb.toString();
		}

		public static String join(List<String> list, String delimiter) {
			if (list == null) return null;
			if (list.size() == 0) return EMPTY;
			if (list.size() == 1) return list.get(0);

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < list.size(); i++) {
				if (i > 0) sb.append(delimiter);
				sb.append(list.get(i));
			}
			return sb.toString();
		}

		public static String join(boolean[] array, String delimiter) {
			if (array == null) return null;
			if (array.length == 0) return EMPTY;
			if (array.length == 1) return String.valueOf(array[0]);

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; i++) {
				if (i > 0) sb.append(delimiter);
				sb.append(array[i]);
			}
			return sb.toString();
		}
		
		public static String join(byte[] array, String delimiter) {
			if (array == null) return null;
			if (array.length == 0) return EMPTY;
			if (array.length == 1) return String.valueOf(array[0]);

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; i++) {
				if (i > 0) sb.append(delimiter);
				sb.append(array[i]);
			}
			return sb.toString();
		}

		public static String join(char[] array, String delimiter) {
			if (array == null) return null;
			if (array.length == 0) return EMPTY;
			if (array.length == 1) return String.valueOf(array[0]);

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; i++) {
				if (i > 0) sb.append(delimiter);
				sb.append(array[i]);
			}
			return sb.toString();
		}
		
		public static String join(short[] array, String delimiter) {
			if (array == null) return null;
			if (array.length == 0) return EMPTY;
			if (array.length == 1) return String.valueOf(array[0]);

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; i++) {
				if (i > 0) sb.append(delimiter);
				sb.append(array[i]);
			}
			return sb.toString();
		}

		public static String join(int[] array, String delimiter) {
			if (array == null) return null;
			if (array.length == 0) return EMPTY;
			if (array.length == 1) return String.valueOf(array[0]);

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; i++) {
				if (i > 0) sb.append(delimiter);
				sb.append(array[i]);
			}
			return sb.toString();
		}

		public static String join(long[] array, String delimiter) {
			if (array == null) return null;
			if (array.length == 0) return EMPTY;
			if (array.length == 1) return String.valueOf(array[0]);

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; i++) {
				if (i > 0) sb.append(delimiter);
				sb.append(array[i]);
			}
			return sb.toString();
		}

		public static String join(float[] array, String delimiter) {
			if (array == null) return null;
			if (array.length == 0) return EMPTY;
			if (array.length == 1) return String.valueOf(array[0]);

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; i++) {
				if (i > 0) sb.append(delimiter);
				sb.append(array[i]);
			}
			return sb.toString();
		}

		public static String join(double[] array, String delimiter) {
			if (array == null) return null;
			if (array.length == 0) return EMPTY;
			if (array.length == 1) return String.valueOf(array[0]);

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; i++) {
				if (i > 0) sb.append(delimiter);
				sb.append(array[i]);
			}
			return sb.toString();
		}

		public static String join(String[] array, String delimiter) {
			if (array == null) return null;
			if (array.length == 0) return EMPTY;
			if (array.length == 1) return array[0];

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; i++) {
				if (i > 0) sb.append(delimiter);
				sb.append(array[i]);
			}
			return sb.toString();
		}

		public static int fuzzyDistance(String str1, String str2) {
			if (str1 == null || str2 == null) return -1;
			if (str1.equals(str2)) return 0;
			if (str1.length() == 0 || str2.length() == 0) return -1;

			int[][] distance = new int[str1.length() + 1][str2.length() + 1];
			
			for (int i = 0; i <= str1.length(); i++) distance[i][0] = i;
			for (int i = 0; i <= str2.length(); i++) distance[0][i] = i;

			for (int i = 1; i <= str1.length(); i++) {
				for (int j = 1; j <= str2.length(); j++) {
					int cost = (str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1;
					distance[i][j] = Mathk.min(Mathk.min(distance[i - 1][j] + 1, distance[i][j - 1] + 1), distance[i - 1][j - 1] + cost);
				}
			}

			return distance[str1.length()][str2.length()];
		}

		/**
		 * Returns absolute distance of two strings no matter character position
		 * in the two string.
		 * 
		 * @author Ethan Temprovich
		 */
		public static int temprovichDistance(String referenceString, String comparisonString) {
			if (referenceString == null || comparisonString == null) return -1;
			if (referenceString.equals(comparisonString)) return 0;
			if (referenceString.length() == 0 || comparisonString.length() == 0) return -1;

			int dif, dist = 0;

			for (int i = 0; i < referenceString.length(); i++) {
				dif = 0;

				for (int j = 0; j < comparisonString.length(); j++) {
					if (referenceString.charAt(i) != comparisonString.charAt(j)) dif++;
					else continue;
				}

				if (dif == comparisonString.length()) dist++;
			}

			return dist == 0 ? dist + Mathk.abs(referenceString.length() - comparisonString.length()) : dist;
		}

		public static String alphabetize(String str) {
			if (str == null) return null;
			if (str.length() == 0) return EMPTY;
			return new String(Arrays.sort(str.toCharArray()));
		}

		public static String repeat(String str, int times) {
			if (str == null) return null;
			if (times <= 0) return EMPTY;
			if (times == 1) return str;

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < times; i++) sb.append(str);
			return sb.toString();
		}

		public static String replace(String str, String oldStr, String newStr) {
			if (str == null) return null;
			if (oldStr == null || newStr == null) return str;
			if (oldStr.equals(newStr)) return str;
			if (str.equals(oldStr)) return newStr;

			StringBuilder sb = new StringBuilder();
			int index = 0;
			int oldIndex = 0;
			while ((index = str.indexOf(oldStr, oldIndex)) != -1) {
				sb.append(str.substring(oldIndex, index));
				sb.append(newStr);
				oldIndex = index + oldStr.length();
			}
			sb.append(str.substring(oldIndex));
			return sb.toString();
		}

		public static String replace(String str, char oldChar, char newChar) {
			if (str == null) return null;
			if (str.length() == 0) return str;
			if (oldChar == newChar) return str;

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				if (c == oldChar) sb.append(newChar);
				else sb.append(c);
			}
			return sb.toString();
		}

		public static String remove(String str, int index) {
			if (str == null) return null;
			if (index < 0 || index >= str.length()) return str;

			final char[] chars = str.toCharArray();
			int pos = 0;

			for (int i = 0; i < chars.length; i++) if (i != index) chars[pos++] = chars[i];

			return new String(chars, 0, pos);
		}

		public static String remove(String str, char remove) {
			if (str == null) return null;
			if (isEmpty(str) || str.indexOf(remove) == -1) return str;

			int pos = 0;
			final char[] chars = str.toCharArray();

			for (int i = 0; i < chars.length; i++) if (chars[i] != remove) chars[pos++] = chars[i];

			return new String(chars, 0, pos);
		}

		public static String remove(String str, char... remove) {
			for (char c : remove) str = remove(str, c);
			return str;
		}

		public static String prepend(String str, String prefix) {
			if (str == null) return null;
			if (prefix == null) return str;
			if (str.length() == 0) return prefix;
			if (prefix.length() == 0) return str;
			if (startsWith(str, prefix)) return str;

			StringBuilder sb = new StringBuilder();
			sb.append(prefix);
			sb.append(str);
			return sb.toString();
		}

		public static String append(String str, String suffix) {
			if (str == null) return null;
			if (suffix == null) return str;
			if (str.length() == 0) return suffix;
			if (suffix.length() == 0) return str;
			if (endsWith(str, suffix)) return str;

			StringBuilder sb = new StringBuilder();
			sb.append(str);
			sb.append(suffix);
			return sb.toString();
		}

		public static String reverse(String str) {
			if (str == null) return null;

			return new StringBuilder(str).reverse().toString();
		}
		
		public static String truncate(String str, final int maxWidth) {
			if (maxWidth < 0) {
				Logger.instance.logError("maxWidth must be greater than or equal to 0");
				return str;
			}

			if (str == null) return null;

			if (str.length() > maxWidth) {
				final int ix = Mathk.min(maxWidth, str.length());
				return str.substring(0, ix);
			}

			return str.substring(0);
		}

		public static String[] arrayOf(int length) {
			String[] str = new String[length];
			for (int i = 0; i < length; i++) str[i] = "";
			return str;
		}

		public static boolean isEmpty(String str) {
			return str == null || str.isEmpty();
		}

		public static boolean nullCheck(String str) {
			if (str == null) {
				Logger.instance.logWarn("null string detected, replaced with empty string");
				str = EMPTY;
				return false;
			}
			return true;
		}

		public static String clone(String str) {
			if (str != null) return str.substring(0);
			return "";
		}

		public static class Generator {

			private static final String[] phonetics = { "a",  "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
											  			"n",  "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
											  			"ph", "th", "ch", "sh", "wh", "qu", "ck", "gh", "ng", "er", "ea",
														"ou", "ow", "oy", "oi", "am", "em", "en", "an", "on", "un", "in",
										  		 	  };

			private static final int[] vowelIndex = { 0, 4, 8, 14, 20 };

			private static final int[] consonantIndex = { 1, 2, 3, 5, 6, 7, 9, 10, 11, 12, 13, 15, 16, 17, 18, 19, 21, 22,
														23, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34
														};

			private static final String[] appends = { "fall", "ton", "thon", "dale" };

			/**
			 * Returns a random person name.
			 */
			public static String randomName() {
				StringBuilder sb = new StringBuilder();

				sb.append(phonetics[consonantIndex[Mathk.random(consonantIndex.length - 7)]]);
				sb.append(phonetics[vowelIndex[Mathk.random(vowelIndex.length - 1)]]);
				String next = phonetics[Mathk.random(phonetics.length - 5)];
				sb.append(next);
				
				for (int i = 0; i < vowelIndex.length; i++) {
					if (vowelIndex[i] == next.charAt(next.length() - 1) || ((next.length() > 1) ? vowelIndex[i] == next.charAt(next.length() - 2) : true)) {
						sb.append(phonetics[consonantIndex[Mathk.random(consonantIndex.length - 1)]]);
						break;
					}
					if (i == vowelIndex.length - 1) sb.append(phonetics[vowelIndex[Mathk.random(vowelIndex.length - 1)]]);
				}
				

				int iterations = Mathk.randomSign();

				do {
					for (int i = 0; i < vowelIndex.length; i++) {
						if (vowelIndex[i] == next.charAt(next.length() - 1) || ((next.length() > 1) ? vowelIndex[i] == next.charAt(next.length() - 2) : true)) {
							sb.append(phonetics[consonantIndex[Mathk.random(consonantIndex.length - 1)]]);
							break;
						}
						if (i == vowelIndex.length - 1) sb.append(phonetics[vowelIndex[Mathk.random(vowelIndex.length - 1)]]);
					}
					iterations = Mathk.randomSign();
				} while (iterations == 1);

				String result = sb.toString();
				return result.substring(0, 1).toUpperCase() + result.substring(1);
			}
			
			/**
			 * Returns a randomized town name.
			 */
			public static String randomTownName() {
				StringBuilder sb = new StringBuilder();

				sb.append(phonetics[Mathk.random(phonetics.length - 1)]);
				
				for (int i = 0; i < vowelIndex.length; i++) {
					if (sb.toString().contains(phonetics[vowelIndex[i]])) {
						sb.append(phonetics[consonantIndex[Mathk.random(consonantIndex.length - 1)]]);
						break;
					}
					if (i == vowelIndex.length - 1) sb.append(phonetics[vowelIndex[Mathk.random(vowelIndex.length - 1)]]);
				}

				int iterations = Mathk.random(5);

				do {			
					for (int i = 0; i < vowelIndex.length; i++) {
						if (sb.toString().contains(phonetics[vowelIndex[i]])) {
							sb.append(phonetics[Mathk.random(phonetics.length - 1)]);
							break;
						}
						if (i == vowelIndex.length - 1) sb.append(phonetics[vowelIndex[Mathk.random(vowelIndex.length - 1)]]);
					}
				} while(--iterations > 0);

				if (sb.length() == 3 && Mathk.randomSign() == 1) sb.append(appends[Mathk.random(appends.length - 1)]);
				else if (sb.length() == 4 && Mathk.random(3) == 1) sb.append(appends[Mathk.random(appends.length - 1)]);

				String result = sb.toString();
				return result.substring(0, 1).toUpperCase() + result.substring(1);
			}
		}

        public static int compare(String s1, String s2) {
			int d = temprovichDistance(s1, s2);
			return Mathk.signum(d);
        }
	}

	public static class Arrays {

		public static final boolean[] EMPTY_BOOLEAN_ARRAY = { };

		public static final byte[] EMPTY_BYTE_ARRAY = { };

		public static final char[] EMPTY_CHAR_ARRAY = { };

		public static final short[] EMPTY_SHORT_ARRAY = { };

		public static final int[] EMPTY_INT_ARRAY = { };

		public static final long[] EMPTY_LONG_ARRAY = { };

		public static final float[] EMPTY_FLOAT_ARRAY = { };

		public static final double[] EMPTY_DOUBLE_ARRAY = { };

		public static final String[] EMPTY_STRING_ARRAY = { };

		public static final int INVALID_INDEX = -1;

		public static void printArray(final byte[] array) {
			for (int i = 0; i < array.length; i++) {
				System.out.print(array[i] + ((i == array.length - 1) ? "" : ", "));
			}
			System.out.println();
		}

		public static void printArray(final short[] array) {
			for (int i = 0; i < array.length; i++) {
				System.out.print(array[i] + ((i == array.length - 1) ? "" : ", "));
			}
			System.out.println();
		}

		public static void printArray(final char[] array) {
			for (int i = 0; i < array.length; i++) {
				System.out.print(array[i] + ((i == array.length - 1) ? "" : ", "));
			}
			System.out.println();
		}

		public static void printArray(final int[] array) {
			for (int i = 0; i < array.length; i++) {
				System.out.print(array[i] + ((i == array.length - 1) ? "" : ", "));
			}
			System.out.println();
		}

		public static void printArray(final long[] array) {
			for (int i = 0; i < array.length; i++) {
				System.out.print(array[i] + ((i == array.length - 1) ? "" : ", "));
			}
			System.out.println();
		}

		public static void printArray(final float[] array) {
			for (int i = 0; i < array.length; i++) {
				System.out.print(array[i] + ((i == array.length - 1) ? "" : ", "));
			}
			System.out.println();
		}

		public static void printArray(final double[] array) {
			for (int i = 0; i < array.length; i++) {
				System.out.print(array[i] + ((i == array.length - 1) ? "" : ", "));
			}
			System.out.println();
		}

		public static <T> void printArray(final T[] array) {
			for (int i = 0; i < array.length; i++) {
				System.out.print(array[i].toString() + ((i == array.length - 1) ? "" : ", "));
			}
			System.out.println();
		}

		public static byte[] sort(final byte[] array) {
			return quicksort(array, 0, array.length - 1);
		}

		public static short[] sort(final short[] array) {
			return quicksort(array, 0, array.length - 1);
		}

		public static char[] sort(final char[] array) {
			return quicksort(array, 0, array.length - 1);
		}

		public static int[] sort(final int[] array) {
			return quicksort(array, 0, array.length - 1);
		}

		public static long[] sort(final long[] array) {
			return quicksort(array, 0, array.length - 1);
		}

		public static float[] sort(final float[] array) {
			return quicksort(array, 0, array.length - 1);
		}

		public static double[] sort(final double[] array) {
			return quicksort(array, 0, array.length - 1);
		}

		public static <T extends Comparable<T>> T[] sort(final T[] array) {
			return quicksort(array, 0, array.length - 1);
		}

		private static byte[] quicksort(byte[] array, int from, int to) {
			if (from >= to) return array;

			int lp = from;
			int rp = to;
			byte pivot = array[(from + to) / 2];

			while (lp <= rp) {
				while (array[lp] < pivot) lp++;
				while (array[rp] > pivot) rp--;

				if (lp <= rp) {
					byte temp = array[lp];
					array[lp] = array[rp];
					array[rp] = temp;
					lp++;
					rp--;
				}
			}

			if (from < rp) quicksort(array, from, rp);
			if (to   > lp) quicksort(array, lp, to);

			return array;
		}

		private static short[] quicksort(short[] array, int from, int to) {
			if (from >= to) return array;

			int lp = from;
			int rp = to;
			short pivot = array[(from + to) / 2];

			while (lp <= rp) {
				while (array[lp] < pivot) lp++;
				while (array[rp] > pivot) rp--;

				if (lp <= rp) {
					short temp = array[lp];
					array[lp] = array[rp];
					array[rp] = temp;
					lp++;
					rp--;
				}
			}

			if (from < rp) quicksort(array, from, rp);
			if (to   > lp) quicksort(array, lp, to);

			return array;
		}

		private static char[] quicksort(char[] array, int from, int to) {
			if (from >= to) return array;

			int lp = from;
			int rp = to;
			char pivot = array[(from + to) / 2];

			while (lp <= rp) {
				while (array[lp] < pivot) lp++;
				while (array[rp] > pivot) rp--;

				if (lp <= rp) {
					char temp = array[lp];
					array[lp] = array[rp];
					array[rp] = temp;
					lp++;
					rp--;
				}
			}

			if (from < rp) quicksort(array, from, rp);
			if (to   > lp) quicksort(array, lp, to);

			return array;
		}

		private static int[] quicksort(int[] array, int from, int to) {
			if (from >= to) return array;

			int lp = from;
			int rp = to;
			int pivot = array[(from + to) / 2];

			while (lp <= rp) {
				while (array[lp] < pivot) lp++;
				while (array[rp] > pivot) rp--;

				if (lp <= rp) {
					int temp = array[lp];
					array[lp] = array[rp];
					array[rp] = temp;
					lp++;
					rp--;
				}
			}

			if (from < rp) quicksort(array, from, rp);
			if (to   > lp) quicksort(array, lp, to);

			return array;
		}

		private static long[] quicksort(long[] array, int from, int to) {
			if (from >= to) return array;

			int lp = from;
			int rp = to;
			long pivot = array[(from + to) / 2];

			while (lp <= rp) {
				while (array[lp] < pivot) lp++;
				while (array[rp] > pivot) rp--;

				if (lp <= rp) {
					long temp = array[lp];
					array[lp] = array[rp];
					array[rp] = temp;
					lp++;
					rp--;
				}
			}

			if (from < rp) quicksort(array, from, rp);
			if (to   > lp) quicksort(array, lp, to);

			return array;
		}

		private static float[] quicksort(float[] array, int from, int to) {
			if (from >= to) return array;

			int lp = from;
			int rp = to;
			float pivot = array[(from + to) / 2];

			while (lp <= rp) {
				while (array[lp] < pivot) lp++;
				while (array[rp] > pivot) rp--;

				if (lp <= rp) {
					float temp = array[lp];
					array[lp] = array[rp];
					array[rp] = temp;
					lp++;
					rp--;
				}
			}

			if (from < rp) quicksort(array, from, rp);
			if (to   > lp) quicksort(array, lp, to);

			return array;
		}

		private static double[] quicksort(double[] array, int from, int to) {
			if (from >= to) return array;

			int lp = from;
			int rp = to;
			double pivot = array[(from + to) / 2];

			while (lp <= rp) {
				while (array[lp] < pivot) lp++;
				while (array[rp] > pivot) rp--;

				if (lp <= rp) {
					double temp = array[lp];
					array[lp] = array[rp];
					array[rp] = temp;
					lp++;
					rp--;
				}
			}

			if (from < rp) quicksort(array, from, rp);
			if (to   > lp) quicksort(array, lp, to);

			return array;
		}

		private static <T extends Comparable<T>> T[] quicksort(T[] array, int from, int to) {
			if (from >= to) return array;

			int lp = from;
			int rp = to;
			T pivot = array[(from + to) / 2];

			while (lp <= rp) {
				while (array[lp].compareTo(pivot) < 0) lp++;
				while (array[rp].compareTo(pivot) > 0) rp--;

				if (lp <= rp) {
					T temp = array[lp];
					array[lp] = array[rp];
					array[rp] = temp;
					lp++;
					rp--;
				}
			}

			if (from < rp) quicksort(array, from, rp);
			if (to   > lp) quicksort(array, lp, to);

			return array;
		} 

		public static boolean[] add(final boolean[] array, final boolean element) {
			final boolean[] newArray = (boolean[]) copyArrayGrow1(array, Boolean.TYPE);
			newArray[newArray.length - 1] = element;
			return newArray;
		}

		public static byte[] add(final byte[] array, final byte element) {
			final byte[] newArray = (byte[]) copyArrayGrow1(array, Byte.TYPE);
			newArray[newArray.length - 1] = element;
			return newArray;
		}

		public static char[] add(final char[] array, final char element) {
			final char[] newArray = (char[]) copyArrayGrow1(array, Character.TYPE);
			newArray[newArray.length - 1] = element;
			return newArray;
		}

		public static short[] add(final short[] array, final short element) {
			final short[] newArray = (short[]) copyArrayGrow1(array, Short.TYPE);
			newArray[newArray.length - 1] = element;
			return newArray;
		}

		public static int[] add(final int[] array, final int element) {
			final int[] newArray = (int[]) copyArrayGrow1(array, Integer.TYPE);
			newArray[newArray.length - 1] = element;
			return newArray;
		}

		public static long[] add(final long[] array, final long element) {
			final long[] newArray = (long[]) copyArrayGrow1(array, Long.TYPE);
			newArray[newArray.length - 1] = element;
			return newArray;
		}

		public static float[] add(final float[] array, final float element) {
			final float[] newArray = (float[]) copyArrayGrow1(array, Float.TYPE);
			newArray[newArray.length - 1] = element;
			return newArray;
		}

		public static double[] add(final double[] array, final double element) {
			final double[] newArray = (double[]) copyArrayGrow1(array, Double.TYPE);
			newArray[newArray.length - 1] = element;
			return newArray;
		}

		public static <T> T[] add(final T[] array, final T element) {
			final Class<?> type;
			if (array != null) {
				type = array.getClass().getComponentType();
			} else if (element != null) {
				type = element.getClass();
			} else {
				throw new IllegalArgumentException("Arguments cannot both be null");
			}
			@SuppressWarnings("unchecked") // type must be T
			final
			T[] newArray = (T[]) copyArrayGrow1(array, type);
			newArray[newArray.length - 1] = element;
			return newArray;
		}

		public static boolean[] addAll(final boolean[] array, final boolean... elements) {
			if (array == null) return clone(elements);
			if (elements == null) return clone(array);

			boolean[] out = new boolean[array.length + elements.length];
			System.arraycopy(array, 0, out, 0, array.length);
			System.arraycopy(elements, 0, out, array.length, elements.length);
			return out;
		}

		public static byte[] addAll(final byte[] array, final byte... elements) {
			if (array == null) return clone(elements);
			if (elements == null) return clone(array);

			byte[] out = new byte[array.length + elements.length];
			System.arraycopy(array, 0, out, 0, array.length);
			System.arraycopy(elements, 0, out, array.length, elements.length);
			return out;
		}

		public static char[] addAll(final char[] array, final char... elements) {
			if (array == null) return clone(elements);
			if (elements == null) return clone(array);

			char[] out = new char[array.length + elements.length];
			System.arraycopy(array, 0, out, 0, array.length);
			System.arraycopy(elements, 0, out, array.length, elements.length);
			return out;
		}

		public static short[] addAll(final short[] array, final short... elements) {
			if (array == null) return clone(elements);
			if (elements == null) return clone(array);

			short[] out = new short[array.length + elements.length];
			System.arraycopy(array, 0, out, 0, array.length);
			System.arraycopy(elements, 0, out, array.length, elements.length);
			return out;
		}

		public static int[] addAll(final int[] array, final int... elements) {
			if (array == null) return clone(elements);
			if (elements == null) return clone(array);

			int[] out = new int[array.length + elements.length];
			System.arraycopy(array, 0, out, 0, array.length);
			System.arraycopy(elements, 0, out, array.length, elements.length);
			return out;
		}

		public static long[] addAll(final long[] array, final long... elements) {
			if (array == null) return clone(elements);
			if (elements == null) return clone(array);

			long[] out = new long[array.length + elements.length];
			System.arraycopy(array, 0, out, 0, array.length);
			System.arraycopy(elements, 0, out, array.length, elements.length);
			return out;
		}

		public static float[] addAll(final float[] array, final float... elements) {
			if (array == null) return clone(elements);
			if (elements == null) return clone(array);

			float[] out = new float[array.length + elements.length];
			System.arraycopy(array, 0, out, 0, array.length);
			System.arraycopy(elements, 0, out, array.length, elements.length);
			return out;
		}

		public static double[] addAll(final double[] array, final double... elements) {
			if (array == null) return clone(elements);
			if (elements == null) return clone(array);

			double[] out = new double[array.length + elements.length];
			System.arraycopy(array, 0, out, 0, array.length);
			System.arraycopy(elements, 0, out, array.length, elements.length);
			return out;
		}

		@SuppressWarnings("unchecked") // type must be T
		public static <T> T[] addAll(final T[] array, final T... elements) {
			if (array == null) return clone(elements);
			if (elements == null) return clone(array);

			final T[] out = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length + elements.length);
			System.arraycopy(array, 0, out, 0, array.length);
			System.arraycopy(elements, 0, out, array.length, elements.length);
			return out;
		}

		public static boolean[] insert(final boolean[] array, final int index, final boolean element) {
			final boolean[] newArray = (boolean[]) copyArrayGrow1(array, Boolean.TYPE);
			System.arraycopy(newArray, index, newArray, index + 1, newArray.length - index - 1);
			newArray[index] = element;
			return newArray;
		}

		public static byte[] insert(final byte[] array, final int index, final byte element) {
			final byte[] newArray = (byte[]) copyArrayGrow1(array, Byte.TYPE);
			System.arraycopy(newArray, index, newArray, index + 1, newArray.length - index - 1);
			newArray[index] = element;
			return newArray;
		}

		public static char[] insert(final char[] array, final int index, final char element) {
			final char[] newArray = (char[]) copyArrayGrow1(array, Character.TYPE);
			System.arraycopy(newArray, index, newArray, index + 1, newArray.length - index - 1);
			newArray[index] = element;
			return newArray;
		}

		public static short[] insert(final short[] array, final int index, final short element) {
			final short[] newArray = (short[]) copyArrayGrow1(array, Short.TYPE);
			System.arraycopy(newArray, index, newArray, index + 1, newArray.length - index - 1);
			newArray[index] = element;
			return newArray;
		}

		public static int[] insert(final int[] array, final int index, final int element) {
			final int[] newArray = (int[]) copyArrayGrow1(array, Integer.TYPE);
			System.arraycopy(newArray, index, newArray, index + 1, newArray.length - index - 1);
			newArray[index] = element;
			return newArray;
		}

		public static long[] insert(final long[] array, final int index, final long element) {
			final long[] newArray = (long[]) copyArrayGrow1(array, Long.TYPE);
			System.arraycopy(newArray, index, newArray, index + 1, newArray.length - index - 1);
			newArray[index] = element;
			return newArray;
		}

		public static float[] insert(final float[] array, final int index, final float element) {
			final float[] newArray = (float[]) copyArrayGrow1(array, Float.TYPE);
			System.arraycopy(newArray, index, newArray, index + 1, newArray.length - index - 1);
			newArray[index] = element;
			return newArray;
		}

		public static double[] insert(final double[] array, final int index, final double element) {
			final double[] newArray = (double[]) copyArrayGrow1(array, Double.TYPE);
			System.arraycopy(newArray, index, newArray, index + 1, newArray.length - index - 1);
			newArray[index] = element;
			return newArray;
		}

		@SuppressWarnings("unchecked") // type must be T
		public static <T> T[] insert(final T[] array, final int index, final T element) {
			final T[] newArray = (T[]) copyArrayGrow1(array, element.getClass());
			System.arraycopy(newArray, index, newArray, index + 1, newArray.length - index - 1);
			newArray[index] = element;
			return newArray;
		}

		public static boolean[] remove(final boolean[] array, final boolean element) {
			if (array == null) return null;
			if (array.length == 0) return array;

			final int length = array.length;
			final int index = indexOf(array, element);
			if (index == INVALID_INDEX) return array;

			final boolean[] newArray = new boolean[length - 1];
			System.arraycopy(array, 0, newArray, 0, index);
			System.arraycopy(array, index + 1, newArray, index, length - index - 1);
			return newArray;
		}

		public static byte[] remove(final byte[] array, final byte element) {
			if (array == null) return null;
			if (array.length == 0) return array;

			final int length = array.length;
			final int index = indexOf(array, element);
			if (index == INVALID_INDEX) return array;

			final byte[] newArray = new byte[length - 1];
			System.arraycopy(array, 0, newArray, 0, index);
			System.arraycopy(array, index + 1, newArray, index, length - index - 1);
			return newArray;
		}

		public static char[] remove(final char[] array, final char element) {
			if (array == null) return null;
			if (array.length == 0) return array;

			final int length = array.length;
			final int index = indexOf(array, element);
			if (index == INVALID_INDEX) return array;

			final char[] newArray = new char[length - 1];
			System.arraycopy(array, 0, newArray, 0, index);
			System.arraycopy(array, index + 1, newArray, index, length - index - 1);
			return newArray;
		}

		public static short[] remove(final short[] array, final short element) {
			if (array == null) return null;
			if (array.length == 0) return array;

			final int length = array.length;
			final int index = indexOf(array, element);
			if (index == INVALID_INDEX) return array;

			final short[] newArray = new short[length - 1];
			System.arraycopy(array, 0, newArray, 0, index);
			System.arraycopy(array, index + 1, newArray, index, length - index - 1);
			return newArray;
		}

		public static int[] remove(final int[] array, final int element) {
			if (array == null) return null;
			if (array.length == 0) return array;

			final int length = array.length;
			final int index = indexOf(array, element);
			if (index == INVALID_INDEX) return array;

			final int[] newArray = new int[length - 1];
			System.arraycopy(array, 0, newArray, 0, index);
			System.arraycopy(array, index + 1, newArray, index, length - index - 1);
			return newArray;
		}

		public static long[] remove(final long[] array, final long element) {
			if (array == null) return null;
			if (array.length == 0) return array;

			final int length = array.length;
			final int index = indexOf(array, element);
			if (index == INVALID_INDEX) return array;

			final long[] newArray = new long[length - 1];
			System.arraycopy(array, 0, newArray, 0, index);
			System.arraycopy(array, index + 1, newArray, index, length - index - 1);
			return newArray;
		}

		public static float[] remove(final float[] array, final float element) {
			if (array == null) return null;
			if (array.length == 0) return array;

			final int length = array.length;
			final int index = indexOf(array, element);
			if (index == INVALID_INDEX) return array;

			final float[] newArray = new float[length - 1];
			System.arraycopy(array, 0, newArray, 0, index);
			System.arraycopy(array, index + 1, newArray, index, length - index - 1);
			return newArray;
		}

		public static double[] remove(final double[] array, final double element) {
			if (array == null) return null;
			if (array.length == 0) return array;

			final int length = array.length;
			final int index = indexOf(array, element);
			if (index == INVALID_INDEX) return array;

			final double[] newArray = new double[length - 1];
			System.arraycopy(array, 0, newArray, 0, index);
			System.arraycopy(array, index + 1, newArray, index, length - index - 1);
			return newArray;
		}

		@SuppressWarnings("unchecked") // type must be T
		public static <T> T[] remove(final T[] array, final T element) {
			if (array == null) return null;
			if (array.length == 0) return array;

			final int length = array.length;
			final int index = indexOf(array, element);
			if (index == INVALID_INDEX) return array;

			final Class<?> type = array.getClass().getComponentType();
			final T[] newArray = (T[]) copyArrayGrow1(array, type);
			System.arraycopy(array, 0, newArray, 0, index);
			System.arraycopy(array, index + 1, newArray, index, length - index - 1);
			return newArray;
		}

		public static int indexOf(final boolean[] array, final boolean element) {
			if (array == null) return INVALID_INDEX;
			if (array.length == 0) return INVALID_INDEX;

			for (int i = 0; i < array.length; i++) if (array[i] == element) return i;

			return INVALID_INDEX;
		}

		public static int indexOf(final byte[] array, final byte element) {
			if (array == null) return INVALID_INDEX;
			if (array.length == 0) return INVALID_INDEX;

			for (int i = 0; i < array.length; i++) if (array[i] == element) return i;

			return INVALID_INDEX;
		}

		public static int indexOf(final char[] array, final char element) {
			if (array == null) return INVALID_INDEX;
			if (array.length == 0) return INVALID_INDEX;

			for (int i = 0; i < array.length; i++) if (array[i] == element) return i;

			return INVALID_INDEX;
		}

		public static int indexOf(final short[] array, final short element) {
			if (array == null) return INVALID_INDEX;
			if (array.length == 0) return INVALID_INDEX;

			for (int i = 0; i < array.length; i++) if (array[i] == element) return i;

			return INVALID_INDEX;
		}

		public static int indexOf(final int[] array, final int element) {
			if (array == null) return INVALID_INDEX;
			if (array.length == 0) return INVALID_INDEX;

			for (int i = 0; i < array.length; i++) if (array[i] == element) return i;

			return INVALID_INDEX;
		}

		public static int indexOf(final long[] array, final long element) {
			if (array == null) return INVALID_INDEX;
			if (array.length == 0) return INVALID_INDEX;

			for (int i = 0; i < array.length; i++) if (array[i] == element) return i;

			return INVALID_INDEX;
		}

		public static int indexOf(final float[] array, final float element) {
			if (array == null) return INVALID_INDEX;
			if (array.length == 0) return INVALID_INDEX;

			for (int i = 0; i < array.length; i++) if (array[i] == element) return i;

			return INVALID_INDEX;
		}

		public static int indexOf(final double[] array, final double element) {
			if (array == null) return INVALID_INDEX;
			if (array.length == 0) return INVALID_INDEX;

			for (int i = 0; i < array.length; i++) if (array[i] == element) return i;

			return INVALID_INDEX;
		}

		public static <T> int indexOf(final T[] array, final T element) {
			if (array == null) return INVALID_INDEX;
			if (array.length == 0) return INVALID_INDEX;

			for (int i = 0; i < array.length; i++) if (element == null ? array[i] == null : element.equals(array[i])) return i;

			return INVALID_INDEX;
		}

		// reference https://github.com/apache/commons-lang/blob/master/src/main/java/org/apache/commons/lang3/ArrayUtils.java
		private static Object copyArrayGrow1(final Object array, final Class<?> newArrayComponentType) {
			if (array != null) {
				final int arrayLength = Array.getLength(array);
				final Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
				System.arraycopy(array, 0, newArray, 0, arrayLength);
				return newArray;
			}
			return Array.newInstance(newArrayComponentType, 1);
		}

		public static boolean getCyclic(int index, final boolean[] array) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.getCyclic(int, boolean[]): array is null");
				return false;
			}
			if (array.length == 0) {
				Logger.instance.logError("ArrayUtils.getCyclic(int, boolean[]): array.length == 0");
				return false;
			}

			return (index >= array.length) ? array[index % array.length] : (index < 0) ? array[array.length + index] : array[index];
		}

		public static byte getCyclic(int index, final byte[] array) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.getCyclic(int, byte[]): array is null");
				return 0;
			}
			if (array.length == 0) {
				Logger.instance.logError("ArrayUtils.getCyclic(int, byte[]): array.length == 0");
				return 0;
			}
			return (index >= array.length) ? array[index % array.length] : (index < 0) ? array[array.length + index] : array[index];
		}

		public static char getCyclic(int index, final char[] array) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.getCyclic(int, byte[]): array is null");
				return 0;
			}
			if (array.length == 0) {
				Logger.instance.logError("ArrayUtils.getCyclic(int, byte[]): array.length == 0");
				return 0;
			}
			return (index >= array.length) ? array[index % array.length] : (index < 0) ? array[array.length + index] : array[index];
		}

		public static short getCyclic(int index, final short[] array) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.getCyclic(int, byte[]): array is null");
				return 0;
			}
			if (array.length == 0) {
				Logger.instance.logError("ArrayUtils.getCyclic(int, byte[]): array.length == 0");
				return 0;
			}
			return (index >= array.length) ? array[index % array.length] : (index < 0) ? array[array.length + index] : array[index];
		}

		public static int getCyclic(int index, final int[] array) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.getCyclic(int, byte[]): array is null");
				return 0;
			}
			if (array.length == 0) {
				Logger.instance.logError("ArrayUtils.getCyclic(int, byte[]): array.length == 0");
				return 0;
			}
			return (index >= array.length) ? array[index % array.length] : (index < 0) ? array[array.length + index] : array[index];
		}

		public static long getCyclic(int index, final long[] array) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.getCyclic(int, byte[]): array is null");
				return 0;
			}
			if (array.length == 0) {
				Logger.instance.logError("ArrayUtils.getCyclic(int, byte[]): array.length == 0");
				return 0;
			}
			return (index >= array.length) ? array[index % array.length] : (index < 0) ? array[array.length + index] : array[index];
		}

		public static float getCyclic(int index, final float[] array) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.getCyclic(int, byte[]): array is null");
				return 0;
			}
			if (array.length == 0) {
				Logger.instance.logError("ArrayUtils.getCyclic(int, byte[]): array.length == 0");
				return 0;
			}
			return (index >= array.length) ? array[index % array.length] : (index < 0) ? array[array.length + index] : array[index];
		}

		public static double getCyclic(int index, final double[] array) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.getCyclic(int, byte[]): array is null");
				return 0;
			}
			if (array.length == 0) {
				Logger.instance.logError("ArrayUtils.getCyclic(int, byte[]): array.length == 0");
				return 0;
			}
			return (index >= array.length) ? array[index % array.length] : (index < 0) ? array[array.length + index] : array[index];
		}

		public static <T> T getCyclic(int index, final T[] array) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.getCyclic(int, T[]): array is null");
				return null;
			}
			if (array.length == 0) {
				Logger.instance.logError("ArrayUtils.getCyclic(int, T[]): array.length == 0");
				return null;
			}
			return (index >= array.length) ? array[index % array.length] : (index < 0) ? array[array.length + index] : array[index];
		}

		public static boolean contains(final boolean[] array, boolean value) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.contains(boolean[], boolean): array is null");
				return false;
			}

			for (boolean b : array) if (b == value) return true;
			
			return false;
		}

		public static boolean contains(final byte[] array, byte value) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.contains(byte[], byte): array is null");
				return false;
			}

			for (byte b : array) if (b == value) return true;
			
			return false;
		}

		public static boolean contains(final char[] array, char value) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.contains(char[], char): array is null");
				return false;
			}

			for (char c : array) if (c == value) return true;
			
			return false;
		}

		public static boolean contains(final short[] array, short value) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.contains(short[], short): array is null");
				return false;
			}

			for (short s : array) if (s == value) return true;
			
			return false;
		}

		public static boolean contains(final int[] array, int value) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.contains(int[], int): array is null");
				return false;
			}

			for (int i : array) if (i == value) return true;
			
			return false;
		}

		public static boolean contains(final long[] array, long value) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.contains(long[], long): array is null");
				return false;
			}

			for (long l : array) if (l == value) return true;
			
			return false;
		}

		public static boolean contains(final float[] array, float value) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.contains(float[], float): array is null");
				return false;
			}

			for (float f : array) if (f == value) return true;
			
			return false;
		}

		public static boolean contains(final double[] array, double value) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.contains(double[], double): array is null");
				return false;
			}

			for (double d : array) if (d == value) return true;
			
			return false;
		}

		public static <T> boolean contains(final T[] array, T value) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.contains(T[], T): array is null");
				return false;
			}

			for (T t : array) if (t.equals(value)) return true;
			return false;
		}

		public static <T> boolean contains(final T[] array, T value, T result) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.contains(T[], T): array is null");
				return false;
			}

			for (T t : array) {
				if (t.equals(value)) {
					result = t;
					return true;
				}
			}
			return false;
		}

		public static <T, J> boolean containsType(final T[] array,  Class<J> value) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.containsType(T[], T): array is null");
				return false;
			}

			for (T t : array) if (t.getClass().isAssignableFrom(value)) return true;
			return false;
		}

		public static <T, J> boolean containsType(final T[] array,  Class<J> value, T result) {
			if (array == null) {
				Logger.instance.logError("ArrayUtils.containsType(T[], T): array is null");
				return false;
			}

			for (T t : array) if (t.getClass().isAssignableFrom(value)) {
				result = t;
				return true;
			}
			return false;
		}

		public static <T, J extends T> boolean containsRelativeType(final T[] array,  Class<J> value) {
			return containsType(array, value);
		}

		public static <T, J extends T> boolean containsRelativeType(final T[] array,  Class<J> value, T result) {
			return containsType(array, value, result);
		}

		public static List<Boolean> toArrayList(final boolean[] array) {
			if (array == null) {
				Logger.instance.logError("Array is null");
				return null;
			}

			List<Boolean> list = new ArrayList<Boolean>(array.length);
			for (boolean b : array) list.add(b);
			return list;
		}

		public static List<Byte> toArrayList(final byte[] array) {
			if (array == null) {
				Logger.instance.logError("Array is null");
				return null;
			}

			List<Byte> list = new ArrayList<Byte>(array.length);
			for (byte b : array) list.add(b);
			return list;
		}

		public static List<Character> toArrayList(final char[] array) {
			if (array == null) {
				Logger.instance.logError("Array is null");
				return null;
			}

			List<Character> list = new ArrayList<Character>(array.length);
			for (char c : array) list.add(c);
			return list;
		}

		public static List<Short> toArrayList(final short[] array) {
			if (array == null) {
				Logger.instance.logError("Array is null");
				return null;
			}

			List<Short> list = new ArrayList<Short>(array.length);
			for (short s : array) list.add(s);
			return list;
		}

		public static List<Integer> toArrayList(final int[] array) {
			if (array == null) {
				Logger.instance.logError("Array is null");
				return null;
			}

			List<Integer> list = new ArrayList<Integer>(array.length);
			for (int i : array) list.add(i);
			return list;
		}

		public static List<Long> toArrayList(final long[] array) {
			if (array == null) {
				Logger.instance.logError("Array is null");
				return null;
			}

			List<Long> list = new ArrayList<Long>(array.length);
			for (long l : array) list.add(l);
			return list;
		}

		public static List<Float> toArrayList(final float[] array) {
			if (array == null) {
				Logger.instance.logError("Array is null");
				return null;
			}

			List<Float> list = new ArrayList<Float>(array.length);
			for (float f : array) list.add(f);
			return list;
		}

		public static List<Double> toArrayList(final double[] array) {
			if (array == null) {
				Logger.instance.logError("Array is null");
				return null;
			}

			List<Double> list = new ArrayList<Double>(array.length);
			for (double d : array) list.add(d);
			return list;
		}

		public static List<String> toArrayList(final String[] array) {
			if (array == null) {
				Logger.instance.logError("Array is null");
				return null;
			}

            List<String> list = new ArrayList<String>();
			for (String s : array) list.add(s);
			return list;
        }

		public static <T> List<T> toArrayList(final T[] array) {
			if (array == null) {
				Logger.instance.logError("Array is null");
				return null;
			}

			List<T> list = new ArrayList<T>(array.length);
			for (T t : array) list.add(t);
			return list;
		}

		public static boolean[] clone(boolean[] ref) {
			if (ref == null) return null;
			boolean[] out = new boolean[ref.length];

			System.arraycopy(ref, 0, out, 0, ref.length);
			return out;
		}

		public static byte[] clone(byte[] ref) {
			if (ref == null) return null;
			byte[] out = new byte[ref.length];

			System.arraycopy(ref, 0, out, 0, ref.length);
			return out;
		}

		public static char[] clone(char[] ref) {
			if (ref == null) return null;
			char[] out = new char[ref.length];

			System.arraycopy(ref, 0, out, 0, ref.length);
			return out;
		}

		public static short[] clone(short[] ref) {
			if (ref == null) return null;
			short[] out = new short[ref.length];

			System.arraycopy(ref, 0, out, 0, ref.length);
			return out;
		}

		public static int[] clone(int[] ref) {
			if (ref == null) return null;
			int[] out = new int[ref.length];

			System.arraycopy(ref, 0, out, 0, ref.length);
			return out;
		}

		public static long[] clone(long[] ref) {
			if (ref == null) return null;
			long[] out = new long[ref.length];

			System.arraycopy(ref, 0, out, 0, ref.length);
			return out;
		}

		public static float[] clone(float[] ref) {
			if (ref == null) return null;
			float[] out = new float[ref.length];

			System.arraycopy(ref, 0, out, 0, ref.length);
			return out;
		}

		public static double[] clone(double[] ref) {
			if (ref == null) return null;
			double[] out = new double[ref.length];

			System.arraycopy(ref, 0, out, 0, ref.length);
			return out;
		}

		@SuppressWarnings("unchecked") // we know that the array is of the correct type
		public static <T> T[] clone(T[] ref) {
			if (ref == null) return null;
			T[] out = (T[]) Array.newInstance(ref.getClass().getComponentType(), ref.length);

			System.arraycopy(ref, 0, out, 0, ref.length);
			return out;
		}
	}
}