/*
 * The MIT License
 *
 * Copyright (c) 2016-2022 JOML
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.starworks.kronos.maths;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import com.starworks.kronos.toolkit.XMLMap;

/**
 * Class which holds constants for interoperation of the math library.
 * 
 * @author Kai Burjack
 * @author Ethan Temprovich
 */
public final class Maths {
	
	private static final XMLMap s_map;
	static {
		s_map = new XMLMap("maths.xml");
		if (s_map.wasGenerated()) {
			s_map.put("debug", 						"false");
			s_map.put("noUnsafe", 					"false");
			s_map.put("forceUnsafe", 				"true");
			s_map.put("fastmath", 					"true");
			s_map.put("sinLookup", 					"true");
			s_map.put("sinLookupBits", 				"14");
			s_map.put("useNumberFormat", 			"true");
			s_map.put("useMathFMA", 				"true");
			s_map.put("numberFormatBigDecimals", 	"3");
			s_map.export();
		}
	}
	
    /**
     * Whether certain debugging checks should be made, such as that only direct NIO Buffers are used when Unsafe is active,
     * and a proxy should be created on calls to readOnlyView().
     */
    public static final boolean DEBUG = s_map.get("debug").equalsIgnoreCase("true");

//#ifdef __HAS_UNSAFE__
    /**
     * Whether <i>not</i> to use sun.misc.Unsafe when copying memory with MemUtil.
     */
    public static final boolean NO_UNSAFE = s_map.get("noUnsafe").equalsIgnoreCase("true");
    /**
     * Whether to <i>force</i> the use of sun.misc.Unsafe when copying memory with MemUtil.
     */
    public static final boolean FORCE_UNSAFE = s_map.get("forceUnsafe").equalsIgnoreCase("true");
//#endif

    /**
     * Whether fast approximations of some java.lang.Math operations should be used.
     */
    public static final boolean FASTMATH = s_map.get("fastmath").equalsIgnoreCase("true");

    /**
     * When {@link #FASTMATH} is <code>true</code>, whether to use a lookup table for sin/cos.
     */
    public static final boolean SIN_LOOKUP = s_map.get("sinLookup").equalsIgnoreCase("true");

    /**
     * When {@link #SIN_LOOKUP} is <code>true</code>, this determines the table size.
     */
    public static final int SIN_LOOKUP_BITS = Integer.parseInt(s_map.get("sinLookupBits"));

    /**
     * Whether to use a {@link NumberFormat} producing scientific notation output when formatting matrix,
     * vector and quaternion components to strings.
     */
    private static final boolean s_useNumberFormat = s_map.get("useNumberFormat").equalsIgnoreCase("true");

    /**
     * Whether to try using java.lang.Math.fma() in most matrix/vector/quaternion operations if it is available.
     * If the CPU does <i>not</i> support it, it will be a lot slower than `a*b+c` and potentially generate a lot of memory allocations
     * for the emulation with `java.util.BigDecimal`, though.
     */
    public static final boolean USE_MATH_FMA = s_map.get("useMathFMA").equalsIgnoreCase("true");

    /**
     * When {@link #s_useNumberFormat} is <code>true</code> then this determines the number of decimal digits
     * produced in the formatted numbers.
     */
    public static final int numberFormatDecimals = Integer.parseInt(s_map.get("numberFormatBigDecimals"));

    /**
     * The {@link NumberFormat} used to format all numbers throughout all JOML classes.
     */
    public static final NumberFormat NUMBER_FORMAT = decimalFormat();

    private Maths() {}

    private static NumberFormat decimalFormat() {
        NumberFormat df;
        if (s_useNumberFormat) {
            char[] prec = new char[numberFormatDecimals];
            Arrays.fill(prec, '0');
            df = new DecimalFormat(" 0." + new String(prec) + "E0;-");
        } else {
            df = NumberFormat.getNumberInstance(Locale.ENGLISH);
            df.setGroupingUsed(false);
        }
        return df;
    }
}
