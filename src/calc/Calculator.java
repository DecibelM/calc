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
        // TODO List<String> tokens = tokenize(expr);
        // TODO List<String> postfix = infix2Postfix(tokens);
        // TODO double result = evalPostfix(postfix);
        return 0; // result;
    }

    // ------  Evaluate RPN expression -------------------

    // TODO Eval methods

    double applyOperator(String op, double d1, double d2) {
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

    List<String> infix2Postfix(List<String> infix) {
        List<String> postfix = new ArrayList();
        Deque<String> tmpStack = new ArrayDeque();
        int[] x = {0};

        for (String str : infix) {
            if (Character.isDigit(str.charAt(0))) {
                postfix.add(str);
            } else {
                precendence(str, x, tmpStack, postfix);
            }
        }
        emptyStack(tmpStack, postfix);
        return postfix;
    }


    Deque<String> precendence(String str, int[] x, Deque<String> tmpStack, List<String> postfix){
        if (getPrecedence(str) > x[0]) {
            tmpStack.push(str);
            x[0] = getPrecedence(str);
        } else if (getPrecedence(str) < x[0]) {
            emptyStack(tmpStack, postfix);
            tmpStack.push(str);
            x[0] = getPrecedence(str);
        } else if (getPrecedence(str) == 0){
            associativity(str, tmpStack, postfix);
        }
        return tmpStack;
    }


    void associativity(String str, Deque<String> tmpStack, List<String> postfix){
        if (getAssociativity(str).equals(Assoc.LEFT)) {
            emptyStack(tmpStack, postfix);
            tmpStack.push(str);
        } else if(getAssociativity(str).equals(Assoc.RIGHT)){
            tmpStack.push(str);
        }
    }


    void emptyStack(Deque<String> tmpStack, List<String> postfix){
        while (!tmpStack.isEmpty()){
            postfix.add(tmpStack.pop());
        }
    }


    int getPrecedence(String op) {
        if ("+-".contains(op)) {
            return 2;
        } else if ("*/".contains(op)) {
            return 3;
        } else if ("^".contains(op)) {
            return 4;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }


    enum Assoc {
        LEFT,
        RIGHT
    }


    Assoc getAssociativity(String op) {
        if ("+-*/".contains(op)) {
            return Assoc.LEFT;
        } else if ("^".contains(op)) {
            return Assoc.RIGHT;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }


    // ---------- Tokenize -----------------------

    // TODO Methods to tokenize


    List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<String>();
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

