package com.starworks.kronos.toolkit.numerics;

import com.starworks.kronos.toolkit.numerics.Operator.Properties.Associativity;

public non-sealed class Operator extends Operand implements Comparable<Operator> {

	protected final Scope m_scope;

	public Operator(String value, Scope scope) {
		super(value);
		this.m_scope = scope;
	}

	public int getOperandCount() {
		return m_scope.getOperatorProperties(m_value).m_params;
	}

	public Associativity getAssociativity() {
		return m_scope.getOperatorProperties(m_value).m_associativity;
	}

	public boolean isFunction() {
		return m_scope.getOperatorProperties(m_value).function != null;
	}

	public boolean isVariableOrConstant() {
		return m_scope.getOperatorProperties(m_value) != null && m_scope.getOperatorProperties(m_value).m_params == 0;
	}

	public Operand evaluate(Operand ... operands) {
		// function
		if (this.isFunction()) {
			return m_scope.getOperatorProperties(m_value).function.evaluate(operands);
		}
		
		// operator, variable, or constant
		switch (m_value) {
			case "+": return new Operand(String.valueOf(operands[0].getValue() + operands[1].getValue()));
			case "-": return new Operand(String.valueOf(operands[0].getValue() - operands[1].getValue()));
			
			case "*": return new Operand(String.valueOf(operands[0].getValue() * operands[1].getValue()));
			case "/": return new Operand(String.valueOf(operands[0].getValue() / operands[1].getValue()));
			case "%": return new Operand(String.valueOf(operands[0].getValue() % operands[1].getValue()));
			case "^": return new Operand(String.valueOf(Math.pow(operands[0].getValue(), operands[1].getValue())));
			
			case "uminus": return new Operand(String.valueOf(-operands[0].getValue()));
			case "uplus": return new Operand(String.valueOf(+operands[0].getValue()));
			
			default: throw new IllegalStateException("Unsupported operator");
		}
		
	}

	@Override
	public int compareTo(Operator other) {
		// compare the precedences of the two operators
		return m_scope.getOperatorProperties(this.m_value).m_precedence - m_scope.getOperatorProperties(other.m_value).m_precedence;
	}

	public static final class Properties {

		public final int m_params;
		public final int m_precedence;
		public final Associativity m_associativity;
		public final Function function;

		public Properties(int params, int precedence) {
			this(params, precedence, Associativity.LEFT, null);
		}

		public Properties(int params, int precedence, Associativity associativity) {
			this(params, precedence, associativity, null);
		}

		public Properties(int params, int precedence, Function function) {
			this(params, precedence, function != null ? Associativity.NONE : Associativity.LEFT, function);
		}

		public Properties(int params, int precedence, Associativity associativity, Function function) {
			this.m_params = params;
			this.m_precedence = precedence;
			this.m_associativity = associativity;
			this.function = function;
			
			if (this.m_params == -1 && this.function == null) {
				throw new IllegalStateException("Only functions can have variable number of parameter.");
			}
		}

		public static enum Associativity {

			LEFT,
			RIGHT,
			NONE;
		}
	}
}
