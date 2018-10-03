package calc;

import java.util.*;

import static java.lang.Double.NaN;
import static java.lang.Math.pow;


/*
 *   A calculator for rather simple arithmetic expressions
 *
 *   This is not the program, it's a class declaration (with methods) in it's
 *   own file (which must be named Calculator.java)
 *
 *   NOTE:
 *   - No negative numbers implemented
 */
class Calculator {

    // Here are the only allowed instance variables!
    // Error messages (more on static later)
    final static String MISSING_OPERAND = "Missing or bad operand";
    final static String DIV_BY_ZERO = "Division with 0";
    final static String MISSING_OPERATOR = "Missing operator or parenthesis";
    final static String OP_NOT_FOUND = "Operator not found";

    // Definition of operators
    final static String OPERATORS = "+-*/^";

    // Method used in REPL
    double eval(String expr) {
        if (expr.length() == 0) {
            return NaN;
        }
        List<String> tokens = tokenize(expr);
        List<String> postfix = infix2Postfix(tokens);
        double result = evalPostfix(postfix);
        return result; // result;
    }

    // ------  Evaluate RPN expression -------------------

    double evalPostfix(List<String> postfix){ /** This method returns the final results of the mathematical operations from the input **/
        Deque<Double> stack = new ArrayDeque();
        double finalAnswer;

        for (String str : postfix){
            if (Character.isDigit(str.charAt(0))) {
                stack.push(Double.valueOf(str));
            } else {
                double d1 = stack.pop();
                double d2;
                if (!stack.isEmpty()) {
                    d2 = stack.pop();
                } else {
                    throw new IllegalArgumentException(MISSING_OPERAND);
                }
                double result = applyOperator(str, d1, d2);
                stack.push(result);
            }
        }
        if (stack.size() != 1){
            throw new IllegalArgumentException(MISSING_OPERATOR);
        } else {
            finalAnswer = stack.pop();
        }
        return finalAnswer;
    }


    double applyOperator(String op, double d1, double d2) { /** This method decides the mathematical operations. **/
        switch (op) {
            case "+":
                return d1 + d2;
            case "-":
                return d2 - d1;
            case "*":
                return d1 * d2;
            case "/":
                if (d1 == 0) {
                    throw new IllegalArgumentException(DIV_BY_ZERO);
                }
                return d2 / d1;
            case "^":
                return pow(d2, d1);
        }
        throw new RuntimeException(OP_NOT_FOUND);
    }

    // ------- Infix 2 Postfix ------------------------

    List<String> infix2Postfix(List<String> infix) { /** This method converts the input to postfix  form. **/
        List<String> postfix = new ArrayList();
        Deque<String> tmpStack = new ArrayDeque();
        int[] x = {0};
        int[] y = {0};

        for (String str : infix) {
            if (Character.isDigit(str.charAt(0))) {
                postfix.add(str);
            } else if ("(".contains(str)) {
                y[0]++;
                tmpStack.push(str);
                x[0] = 1;
            } else if (")".contains(str)){
                y[0]--;
                rightPara(x, tmpStack, postfix);
            }else {
                precendence(str, x, tmpStack, postfix);
            }
        }
        emptyStack(tmpStack, postfix);
        if (y[0] != 0){
            throw new IllegalArgumentException(MISSING_OPERATOR);
        }
        return postfix;
    }


    Deque<String> rightPara (int[] x, Deque<String> tmpStack, List<String> postfix){ /** This method handles parenthesis operators. **/
        while (!tmpStack.isEmpty() && !tmpStack.peek().equals("(")){
            postfix.add(tmpStack.pop());
        }
        if (!tmpStack.isEmpty()) {
            tmpStack.pop();
        }
        if (!tmpStack.isEmpty()){
            x[0] = getPrecedence(tmpStack.peek());
        } else {
            x[0] = 0;
        }
        return tmpStack;
    }


    Deque<String> precendence(String str, int[] x, Deque<String> tmpStack, List<String> postfix) { /** This method handles the operators based on the precedence of them. **/
        if (getPrecedence(str) > x[0]) {
            tmpStack.push(str);
            x[0] = getPrecedence(str);
        } else if (getPrecedence(str) < x[0]) {
            //emptyStack(tmpStack, postfix);
            postfix.add(tmpStack.pop());
            tmpStack.push(str);
            x[0] = getPrecedence(str);
        } else if (getPrecedence(str) == x[0]) {
            associativity(str, tmpStack, postfix);
        }
        return tmpStack;
    }


    void associativity(String str, Deque<String> tmpStack, List<String> postfix) { /** This method handles the operators on the associativity of them. **/
        if (getAssociativity(str).equals(Assoc.LEFT)) {
            //emptyStack(tmpStack, postfix);
            postfix.add(tmpStack.pop());
            tmpStack.push(str);
        } else if (getAssociativity(str).equals(Assoc.RIGHT)) {
            tmpStack.push(str);
        }
    }


    void emptyStack(Deque<String> tmpStack, List<String> postfix) { /** This method empties the stack completely. **/
        while (!tmpStack.isEmpty()) {
            postfix.add(tmpStack.pop());
        }
    }


    int getPrecedence(String op) { /** This method checks the precedence of the operator and returns it. **/
        if ("+-".contains(op)) {
            return 2;
        } else if ("*/".contains(op)) {
            return 3;
        } else if ("^".contains(op)) {
            return 4;
        } else if ("(".contains(op)) {
            return 1;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }


    enum Assoc {
        LEFT,
        RIGHT
    }


    Assoc getAssociativity(String op) { /** This method checks the associativity of the operator and returns it. **/
        if ("+-*/".contains(op)) {
            return Assoc.LEFT;
        } else if ("^".contains(op)) {
            return Assoc.RIGHT;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }


    // ---------- Tokenize -----------------------


    List<String> tokenize(String expr) { /** This method converts the input string into tokens representing numbers and operators.**/

        List<String> tokens = new ArrayList();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < expr.length(); i++) {
            if (i != expr.length() - 1 && Character.isDigit(expr.charAt(i)) && Character.isDigit(expr.charAt(i + 1))) {
                sb.append(expr.charAt(i));
            } else {
                sb.append(expr.charAt(i) + " ");
            }
        }

        String tokenString = sb.toString().trim();

        tokens = Arrays.asList(tokenString.split("\\s+"));
        return tokens;
    }
}

