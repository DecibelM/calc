package calc;

import com.sun.org.apache.xerces.internal.xs.StringList;

import java.lang.reflect.Array;
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

    // TODO Methods
    double evalPostfix(List<String> postfix){
        double finalResult = 0;

        Deque<Double> stack = new ArrayDeque<>();

        for(String str: postfix){
            if (Character.isDigit(str.charAt(0))){
                stack.push(Double.valueOf(str));
            }else{
                double d1 = stack.pop();
                double d2;
                if(!stack.isEmpty()) {
                    d2 = stack.pop();
                }else{
                    throw new IllegalArgumentException(MISSING_OPERAND);
                }

                double result = applyOperator(str, d1, d2);
                stack.push(result);
            }
        }

        if(stack.size() == 1) {
            finalResult = stack.pop();
        }else{
            throw new IllegalArgumentException(MISSING_OPERATOR);
        }

        return finalResult;
    }

    List<String> infix2Postfix(List<String> infix){
        List<String> postfix = new ArrayList<>();
        Deque<String> tmpStack = new ArrayDeque<>();

        int x = 0; //Inital value for priority.
        int n = 0;

        for(String str: infix) {
            if (Character.isDigit(str.charAt(0))) { //Is digit
                postfix.add(str);
            }else if(str.equals("(")) {
                tmpStack.push(str);
                x = 1;
                n++;
            }else if(str.equals(")")){
                //tmpStack.pop();
                n--;
                while(!tmpStack.isEmpty() && !tmpStack.peek().equals("(")){
                    postfix.add(tmpStack.pop());

                }
                if(!tmpStack.isEmpty()){
                    tmpStack.pop();
                }

                if(!tmpStack.isEmpty()){

                    x = getPrecedence(tmpStack.peek());
                } else{
                    x = 0;
                }
            }else { //operator
                if(getPrecedence(str) > x){
                    tmpStack.push(str);
                    x = getPrecedence(str); //Current priority.
                }else if(getPrecedence(str)< x){

                    popAll(postfix, tmpStack);
                    tmpStack.push(str);
                    x = getPrecedence(str);

                }else{
                    if(getAssociativity(str).equals(Assoc.LEFT)){
                        popAll(postfix, tmpStack);
                        tmpStack.push(str);
                    }else if(getAssociativity(str).equals(Assoc.RIGHT)){
                        tmpStack.push(str);
                    }
                }
            }
        }

        if(n != 0){
            throw new IllegalArgumentException(MISSING_OPERATOR);
        }
        popAll(postfix, tmpStack);

        return postfix;
    }


    void popAll(List<String> postfix, Deque<String> tmpStack){
        while(!tmpStack.isEmpty()){
            postfix.add(tmpStack.pop());
        }

        return;
    }


    int getPrecedence(String op) {
        if ("+-".contains(op)) {
            return 2;
        } else if ("*/".contains(op)) {
            return 3;
        } else if ("^".contains(op)) {
            return 4;
        } else if("(".contains(op)) {
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

    // TODO Methods to tokenize
    List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        expr = expr.trim();

        for (int i = 0; i < expr.length(); i++) { //TODO expr (string) --> tokens (list av string, each character as an element in string form).
            if (i != expr.length()-1 && Character.isDigit(expr.charAt(i)) && Character.isDigit(expr.charAt(i+1))) {
                sb.append(expr.charAt(i));
            } else {
                sb.append(expr.charAt(i)).append(" ");
            }
        }

        expr = sb.toString();
        String[] tokensArr = expr.split("\\s+");

        tokens = Arrays.asList(tokensArr);

        return tokens;
    }


}
