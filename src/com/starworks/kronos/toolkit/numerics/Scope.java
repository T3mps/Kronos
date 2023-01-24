package com.starworks.kronos.toolkit.numerics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.starworks.kronos.maths.Mathk;
import com.starworks.kronos.toolkit.numerics.Operator.Properties;
import com.starworks.kronos.toolkit.numerics.Operator.Properties.Associativity;

public class Scope {
	
	private final Map<String, Properties> m_operators;
	private final Set<String> m_predefined;
	
	public Scope(boolean includePredefines) {
		this.m_operators = new HashMap<String, Properties>();
		this.m_predefined = new HashSet<String>();
		
		if (includePredefines) {
			initialize();
		}
	}
	
	private final void initialize() {
		int precedence = 0;
		m_operators.put("(", 		new Properties(1, precedence));
		m_operators.put(")", 		new Properties(1, precedence));
		
		// no argument - priority 0
		m_operators.put(",", 		new Properties(0, precedence));
		
		++precedence;
		// binary - priority 1
		m_operators.put("+", 		new Properties(2, precedence));
		m_operators.put("-", 		new Properties(2, precedence));
		
		++precedence;
		// binary - priority 2
		m_operators.put("*", 		new Properties(2, precedence));
		m_operators.put("/", 		new Properties(2, precedence));
		m_operators.put("%", 		new Properties(2, precedence));
		
		++precedence;
		// binary - priority 3
		m_operators.put("^", 		new Properties(2, precedence));
		
		++precedence;
		// unary - priority 4
		m_operators.put("uminus", 	new Properties(1, precedence, Associativity.NONE));
		m_operators.put("uplus",  	new Properties(1, precedence, Associativity.NONE));
		
		++precedence;
		// functions - priority 4
		m_operators.put("abs",    	new Properties(1, precedence, (operands) -> { return new Operand(Mathk.abs(operands[0].getValue())); }));
		
		m_operators.put("sin",    	new Properties(1, precedence, (operands) -> { return new Operand(Mathk.sin(operands[0].getValue())); }));
		m_operators.put("cos",    	new Properties(1, precedence, (operands) -> { return new Operand(Mathk.cos(operands[0].getValue())); }));
		m_operators.put("tan",    	new Properties(1, precedence, (operands) -> { return new Operand(Mathk.tan(operands[0].getValue())); }));
		
		m_operators.put("asin",   	new Properties(1, precedence, (operands) -> { return new Operand(Mathk.asin(operands[0].getValue())); }));
		m_operators.put("acos",   	new Properties(1, precedence, (operands) -> { return new Operand(Mathk.acos(operands[0].getValue())); }));
		m_operators.put("atan",   	new Properties(1, precedence, (operands) -> { return new Operand(Math.atan(operands[0].getValue())); }));
		
		m_operators.put("sinh",   	new Properties(1, precedence, (operands) -> { return new Operand(Math.sinh(operands[0].getValue())); }));
		m_operators.put("cosh",   	new Properties(1, precedence, (operands) -> { return new Operand(Math.cosh(operands[0].getValue())); }));
		m_operators.put("tanh",   	new Properties(1, precedence, (operands) -> { return new Operand(Math.tanh(operands[0].getValue())); }));
		
		m_operators.put("degrees",  new Properties(1, precedence, (operands) -> { return new Operand(Mathk.toDegrees(operands[0].getValue())); }));
		m_operators.put("radians",  new Properties(1, precedence, (operands) -> { return new Operand(Mathk.toRadians(operands[0].getValue())); }));
		
		m_operators.put("round",  	new Properties(1, precedence, (operands) -> { return new Operand(Mathk.round(operands[0].getValue())); }));
		m_operators.put("floor",  	new Properties(1, precedence, (operands) -> { return new Operand(Mathk.floor(operands[0].getValue())); }));
		m_operators.put("ceil",   	new Properties(1, precedence, (operands) -> { return new Operand(Mathk.ceil(operands[0].getValue())); }));
		
		m_operators.put("ln",     	new Properties(1, precedence, (operands) -> { return new Operand(Math.log(operands[0].getValue())); }));
		m_operators.put("log",  	new Properties(1, precedence, (operands) -> { return new Operand(Math.log10(operands[0].getValue())); }));
		
		m_operators.put("sqrt",   	new Properties(1, precedence, (operands) -> { return new Operand(Mathk.sqrt(operands[0].getValue())); }));
		m_operators.put("cbrt",   	new Properties(1, precedence, (operands) -> { return new Operand(Mathk.cbrt(operands[0].getValue())); }));
		
		m_operators.put("max",    	new Properties(2, precedence, (operands) -> { return new Operand(Mathk.max(operands[0].getValue(), operands[1].getValue())); }));
		m_operators.put("min",    	new Properties(2, precedence, (operands) -> { return new Operand(Mathk.min(operands[0].getValue(), operands[1].getValue())); }));
		
		// variable argument functions - priority 4
		m_operators.put("avg",    	new Properties(-1, precedence, (operands) -> {
    		double sum = 0;
    		for (Operand operand : operands) {
    			sum += operand.getValue();
    		}
    		return new Operand(sum / operands.length);
    	}));
		
		++precedence;
		// zero argument - priority 5
		m_operators.put("rand",   	new Properties(0, precedence, (operands) -> { return new Operand(Mathk.random()); }));
		m_operators.put("pi",     	new Properties(0, precedence, (operands) -> { return new Operand(Mathk.PI); }));
		m_operators.put("PI",     	new Properties(0, precedence, (operands) -> { return new Operand(Mathk.PI); }));
		m_operators.put("e", 		new Properties(0, precedence, (operands) -> { return new Operand(Mathk.E); }));
		m_operators.put("E", 		new Properties(0, precedence, (operands) -> { return new Operand(Mathk.E); }));

		// add to set
    	m_predefined.addAll(m_operators.keySet());
	}

	public Operator.Properties getOperatorProperties(String value) {
		return m_operators.get(value);
	}

	public boolean isOperator(String op) {
		return m_operators.containsKey(op);
	}

	public boolean isFunction(String op) {
		return m_operators.get(op) != null && m_operators.get(op).function != null;
	}

	public boolean isVariableOrConstant(String op) {
		return m_operators.get(op) != null && m_operators.get(op).m_params == 0;
	}

	public void addFunction(String fName, int params, Function function) {
		if (fName == null) throw new NullPointerException("Function name cannot be null.");
		if (!fName.matches("[a-zA-Z][a-zA-Z0-9]+")) throw new IllegalStateException("Not a valid function name: " + fName + ".");
		if (function == null) throw new NullPointerException("Function cannot be null.");
		if (m_predefined.contains(fName)) throw new IllegalStateException("Cannot override predefined function: " + fName + ".");
		
		m_operators.put(fName, new Properties(params, 4, function));
	}
	
	public void addFunction(String fName, Function function) {
		addFunction(fName, -1, function);
	}

	public void removeFunction(String fName) {
		if (fName == null) throw new NullPointerException("Function name cannot be null.");
		if (!fName.matches("[a-zA-Z0-9]+")) throw new IllegalStateException("Not a valid function name: " + fName + ".");
		if (!isOperator(fName) || !isFunction(fName)) throw new IllegalStateException("Function not found: " + fName + ".");
		if (m_predefined.contains(fName)) throw new IllegalStateException("Cannot remove predefined function: " + fName + ".");
		
		m_operators.remove(fName);
	}

	public List<String> getOperatorList() {
		var opList = new ArrayList<String>();
		var opSet = m_operators.keySet();
	    for (var op : opSet) {
	    	opList.add(op);
	    }
	    
	    Collections.sort(opList);
	    return opList;
	}
}
