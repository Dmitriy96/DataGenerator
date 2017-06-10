package by.bsuir.diplom.datageneration;

import java.util.HashMap;
import java.util.Map;

//import org.jmat.function.expressionParser.Evaluator;

public class ExpressionEvaluator {
	// term for start variable name
	public static final String startWords = "${";

	// term for end variable name
	public static final String endWords = "}";

	// context that contains all actually variable
	private Map context = new HashMap();

	/**
	 * evaluates expression using curruntly context.
	 * 
	 * @param exp expression as string.
	 * 
	 * @return evaluated string object.
	 * 
	 * @throws Exception if expression has incorrect syntax.
	 */
	public Object evaluate(String exp) throws Exception {
		int startIdx = 0;
		int endIdx = 0;
		String evalExp = new String(exp);
		while (startIdx != -1) {
			startIdx = findVariable(evalExp, startIdx);
			if (startIdx == -1) {
				break;
			}
			endIdx = evalExp.indexOf(endWords, startIdx);
			String name = evalExp.substring(startIdx, endIdx);
			if ((name == null) || "".equals(name.trim())) {
				throw new Exception("invalid variable name that begin from "
						+ startIdx + " character:" + name);
			}
			Object value = getVariableValue(name);
			String stringValue = ((value == null) ? "" : value.toString());
			evalExp = evalExp.substring(0, startIdx - startWords.length())
					+ stringValue
					+ evalExp.substring(endIdx + endWords.length());
			startIdx = startIdx - startWords.length() + stringValue.length();
		}
		return evalExp;
	}

	/**
	 * evaluate arithmetic expression and return result as Double
	 * 
	 * @param expression
	 * @return result of evaluating expression
	 */
	public Double evaluateArithmeticExpression(String expression) {
		return Double.parseDouble(expression);
//		return (Double) (new Evaluator(expression)).getValue();
	}

	/**
	 * returns context for instance.
	 * 
	 * @return
	 */
	public Map getContext() {
		return context;
	}

	/**
	 * finds index in expression string from its start variable name.
	 * 
	 * @param exp expression as string.
	 * @param startIndex index from start to find variables.
	 * 
	 * @return index from start variable name.
	 * 
	 * @throws Exception if expression has incorrect syntax.
	 */
	private int findVariable(String exp, int startIndex) throws Exception {
		int begVarIdx = exp.indexOf(startWords, startIndex);
		if (begVarIdx == -1) {
			return -1;
		} else {
			int endVarIdx = exp.indexOf(endWords, startIndex);
			if (endVarIdx == -1) {
				throw new Exception(
						"haven't close tag for variable that start in "
								+ startIndex + " character.");
			}
			return begVarIdx + startWords.length();
		}
	}

	/**
	 * gets value for variable name.
	 * 
	 * @param name variable name.
	 * 
	 * @return
	 */
	private Object getVariableValue(String name) {
		if (context.containsKey(name)) {
			return context.get(name);
		} else {
			return null;
		}
	}

}
