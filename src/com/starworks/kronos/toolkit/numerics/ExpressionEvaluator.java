package com.starworks.kronos.toolkit.numerics;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.starworks.kronos.toolkit.collections.stack.Stack;
import com.starworks.kronos.toolkit.numerics.Operator.Properties.Associativity;

public class ExpressionEvaluator {

	private static final int PRECISION = 10;
	
	private static final Scope m_scope = new Scope(true);
	private static final ShuntingYard m_yard = new ShuntingYard(m_scope);
	
	public static double evaluate(String expression) {
		return m_yard.evaluate(expression);
	}
	
	public static void addFunction(String functionName, int params, Function function) {
		m_scope.addFunction(functionName, params, function);
	}
	
	public static void addFunction(String functionName, Function function) {
		m_scope.addFunction(functionName, function);
	}
	
	public static void removeFunction(String fName) {
		m_scope.removeFunction(fName);
	}

	private static final class ShuntingYard {
		
		private final Stack<Operand> operandStack;
		private final Stack<Operator> operatorStack;
		private final Scope scope;
		
		private final Stack<Operator> functions;
		private  final Stack<Integer> functionParams;

		private ShuntingYard(Scope scope) {
			this.operandStack = new Stack<Operand>();
			this.operatorStack = new Stack<Operator>();
			this.scope = scope;
			this.functions = new Stack<Operator>();
			this.functionParams = new Stack<Integer>();
		}

		private double evaluate(String expression) {
			try {
				double result = this.evaluateExpression(expression).getValue();

				if (!Double.isFinite(result)) return result;

				BigDecimal bd = BigDecimal.valueOf(result);
				bd = bd.setScale(PRECISION, RoundingMode.HALF_UP);

				return bd.doubleValue();
			} finally {
				this.operandStack.clear();
				this.operatorStack.clear();
			}
		}

		private Operand evaluateExpression(String expression) {
			expression = expression.replaceAll("(?!\\d|\\+|\\-)\\s+(?!\\d|\\.)", "");

			if (expression.isEmpty()) {
				throw new IllegalStateException("Invalid expression");
			}

			String token = new String();
			String lastToken = null;
			String operator = null;

			functions.clear();
			functionParams.clear();

			for (int i = 0; i < expression.length(); i++) {
				char ch = expression.charAt(i);
				char chNext = (i + 1 < expression.length()) ? expression.charAt(i + 1) : '\u0000';

				if (scope.isOperator(token + ch) && !(token + ch).equals("uplus") && !(token + ch).contentEquals("uminus") && ((scope.isVariableOrConstant(token + ch) && chNext == '\u0000' || (scope.isOperator(String.valueOf(chNext)) && !scope.isFunction(String.valueOf(chNext)))) || !scope.isFunction(token + ch) || chNext == '(')) {
					Operator op = new Operator(token + ch, scope);

					if (((ch == '-' || ch == '+') && chNext != ' ' && (lastToken == null || (scope.isOperator(lastToken) && !lastToken.equals(")"))))) {
						if (ch == '-') op = new Operator("uminus", scope);
						if (ch == '+') op = new Operator("uplus", scope);
					}

					if (op.getOperandCount() == 0) {
						if (op.toString().equals(",")) {
							if (functions.isEmpty() || (functions.peek().getOperandCount() != -1 && functionParams.peek() >= functions.peek().getOperandCount() - 1)) {
								throw new IllegalStateException("Invalid expression");
							} else {
								functionParams.push(functionParams.pop() + 1);
								evaluateParenthesis();
								operatorStack.push(new Operator("(", scope));
							}
						} else {
							Operand eval = op.evaluate();
							operandStack.push(eval);
							lastToken = eval.toString();
							operator = op.toString();
							token = new String();
							continue;
						}
					} else if (op.toString().equals("(")) {
						operatorStack.push(op);
						if ((lastToken == null && chNext == ')') || (lastToken != null && !scope.isFunction(operator) && !scope.isVariableOrConstant(operator) && !operator.equals("(") && chNext == ')')) {
							throw new IllegalStateException("Invalid use of parenthesis");
						}
						if (lastToken != null && scope.isFunction(lastToken)) {
							functions.push(new Operator(lastToken, scope));
							functionParams.push(0);
							operatorStack.push(new Operator("(", scope));
						} else if (!functions.isEmpty()) {
							functions.push(op);
							functionParams.push(0);
						}
					} else if (op.toString().equals(")")) {
						evaluateParenthesis();

						if (!functions.isEmpty()) {
							if (functions.peek().isFunction()) {
								evaluateParenthesis();
								if (functions.peek().getOperandCount() == -1) {
									Operator tosOp = operatorStack.pop();
									int paramsCount = functionParams.peek() + 1;
									tosOp = new Operator(tosOp.toString(), scope) {
										@Override
										public int getOperandCount() {
											return paramsCount;
										}
									};
									operatorStack.push(tosOp);
								}
								evaluateNext();
							}
							functions.pop();
							functionParams.pop();
						}
					} else {
						while (!operatorStack.isEmpty() && ((operatorStack.peek()).compareTo(op) > 0 || ((operatorStack.peek()).compareTo(op) == 0 && (operatorStack.peek()).getAssociativity() == Associativity.LEFT))) {
							evaluateNext();
						}
						operatorStack.push(op);
					}

					lastToken = token + ch;
					operator = lastToken;
					token = new String();
				} else if (isOperand(token + ch) && (chNext != 'e' && chNext != 'E') && (chNext == '\u0000' || !isOperand(token + ch + chNext))) {
					// add to operandStack
					operandStack.push(new Operand(token + ch));

					lastToken = token + ch;
					operator = lastToken;
					token = new String();
				} else {
					token += ch;
				}
			}

			if (!token.isEmpty()) {
				throw new IllegalStateException("Invalid expression");
			}

			while (!operatorStack.isEmpty()) {
				if (operatorStack.peek().toString().equals("(")) {
					throw new IllegalStateException("Unmatched number of parenthesis");
				}
				evaluateNext();
			}

			if (operandStack.size() > 1) {
				throw new IllegalStateException("Invalid expression");
			}

			return operandStack.pop();
		}

		private void evaluateNext() {
			Operator operator = operatorStack.pop();
			Operand[] operands = new Operand[operator.getOperandCount()];

			for (int j = 0; j < operands.length; j++) {
				if (operandStack.isEmpty()) {
					throw new IllegalStateException("Invalid expression");
				}

				operands[operands.length - j - 1] = operandStack.pop();
			}

			Operand operand = operator.evaluate(operands);
			operandStack.push(operand);
		}

		private void evaluateParenthesis() {
			boolean flag = false;
			while (!operatorStack.isEmpty()) {
				if (operatorStack.peek().toString().equals("(")) {
					operatorStack.pop();
					flag = true;
					break;
				}
				evaluateNext();
			}
			if (!flag) {
				throw new IllegalStateException("Unmatched number of parenthesis");
			}
		}
		
		private boolean isOperand(String op) {
			try {
				Double.parseDouble(op);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
	}
}
