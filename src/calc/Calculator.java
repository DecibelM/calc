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
        List<String> postfix = infixToPostfix(tokens);
        double result = evalPostfix(postfix);
        return result;
    }

    // ------  Evaluate RPN expression -------------------

    // TODO Eval methods
    double evalPostfix(List<String> postfix) { //Calculates the postfix
        double finalResult;
        Deque<Double> dStack = new ArrayDeque<>();

        for (String str : postfix) {
            if (Character.isDigit(str.charAt(0))) {
                dStack.push(Double.valueOf(str));
            } else {
                count(str, dStack);
            }
        }
        if (dStack.size() == 1) {
            finalResult = dStack.pop();
        } else {
            throw new IllegalArgumentException(MISSING_OPERATOR);
        }
        return finalResult;
    }

    void count(String str, Deque<Double> dStack) { //Applies the operators and counts
        double d1 = dStack.pop();
        double d2;
        if (!dStack.isEmpty()) {
            d2 = dStack.pop();
        } else {
            throw new IllegalArgumentException(MISSING_OPERAND);
        }
        double result = applyOperator(str, d1, d2);
        dStack.push(result);
    }


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

    // ------- Infix to Postfix ------------------------

    List<String> infixToPostfix(List<String> infix) {
        List<String> postfix = new ArrayList<String>();
        Deque<String> tmpStack = new ArrayDeque<>();
        int[] x = {0};
        int[] y = {0};

        for (String str : infix) {
            if (Character.isDigit(str.charAt(0))) {
                postfix.add(str);
            } else if (str.equals("(")) {
                tmpStack.push(str);
                x[0] = 1;
                y[0]++;
            } else if (str.equals(")")) {
                parentheses(str, postfix, tmpStack, x);
                y[0]--;
            } else {
                Operators(str, postfix, tmpStack, x);
            }
        }
        if (y[0] == 0) {
            emptyStack(postfix, tmpStack);
        } else {
            throw new IllegalArgumentException(MISSING_OPERATOR);
        }
        return postfix;
    }



    void parentheses(String str, List<String> postfix, Deque<String> tmpStack, int[] x) { //If the string is a parentheses
        while (!tmpStack.isEmpty() && !tmpStack.peek().equals("(")) {
            postfix.add(tmpStack.pop());
        }
        if (!tmpStack.isEmpty() && tmpStack.peek().equals("(")) {
            tmpStack.pop();
        }
        if (!tmpStack.isEmpty()) {
            x[0] = getPrecedence(tmpStack.peek());
        } else {
            x[0] = 0;
        }
    }

    Deque<String> Operators(String str, List<String> postfix, Deque<String> tmpStack, int[] x) { //If the character is a operator
        if (getPrecedence(str) > x[0]) {
            tmpStack.push(str);
            x[0] = getPrecedence(str);
        } else if (getPrecedence(str) < x[0]) {
            //emptyStack(postfix, tmpStack);
            postfix.add(tmpStack.pop());
            tmpStack.push(str);
            x[0] = getPrecedence(str);
        } else if (getPrecedence(str) == x[0]) {
            Associativity(str, postfix, tmpStack);
        }
        return tmpStack;
    }

    void Associativity(String str, List<String> postfix, Deque<String> tmpStack) { //If the operators have the same precedence, check associativity
        if (getAssociativity(str).equals(Assoc.LEFT)) {
            //emptyStack(postfix, tmpStack);
            postfix.add(tmpStack.pop());
            tmpStack.push(str);
        } else {
            tmpStack.push(str);
        }
    }

    void emptyStack(List<String> postfix, Deque<String> tmpStack) { //Empties the stack and adds it to the postfix-list
        while (!tmpStack.isEmpty()) {
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

    List<String> tokenize(String expr) { //Turns the string into an arraylist
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