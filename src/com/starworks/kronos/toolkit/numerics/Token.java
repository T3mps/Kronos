package com.starworks.kronos.toolkit.numerics;

public abstract sealed class Token permits Operand {

	protected String m_value;

	public Token(String value) {
		this.m_value = value;
	}

	@Override
	public String toString() {
		return this.m_value;
	}
}
