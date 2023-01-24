package com.starworks.kronos.toolkit.numerics;

public sealed class Operand extends Token permits Operator {

	public Operand(String value) {
		super(value);
	}

	public Operand(double value) {
		super(String.valueOf(value));
	}

	public double getValue() {
		return Double.parseDouble(m_value);
	}
}
