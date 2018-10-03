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
    double evalPostfix(List<String> postfix){                       //Evaluates the postfix expression.
        double finalResult = 0;

        Deque<Double> stack = new ArrayDeque<>();

        for(String str: postfix){
            if (Character.isDigit(str.charAt(0))){                  //Is digit?
                stack.push(Double.valueOf(str));                    //Turn to double and add to stack.
            }else{
                count(stack, str);                                  //If operator?
            }                                                       //Take out two numbers from stack and do operation.
        }

        if(stack.size() == 1) {                                     //If size of stack = 1, the top element is the result.
            finalResult = stack.pop();
        }else{
            throw new IllegalArgumentException(MISSING_OPERATOR);
        }

        return finalResult;
    }

    void count(Deque<Double> stack, String str){                    //Performs the operations
        double d1 = stack.pop();                                    //Pops the two top elements in the stack for the operation.
        double d2;
        if(!stack.isEmpty()) {
            d2 = stack.pop();
        }else{
            throw new IllegalArgumentException(MISSING_OPERAND);
        }

        double result = applyOperator(str, d1, d2);                 //Applies the operators.
        stack.push(result);
        return;
    }

    List<String> infix2Postfix(List<String> infix){                 //From infix for to postfix form.
        List<String> postfix = new ArrayList<>();
        Deque<String> tmpStack = new ArrayDeque<>();

        int x = 0; //Inital value for priority of the index 0 element in stack.
        int n = 0; //Initial value for counting parantheses.

        for(String str: infix) {                    //Looping through the infix list.
            if (Character.isDigit(str.charAt(0))) { //Is digit?
                postfix.add(str);                   //Add to postfix.
            }else if(str.equals("(")) {             //Is opening parenthesis?
                tmpStack.push(str);                 //Add to stack
                x = 1;                              //Set priority to 1 (higher than zero, lower than +
                n++;
            }else if(str.equals(")")){              //Is closing parenthesis?
                rightPar(tmpStack, postfix, x);     //Pop out all operators until opening parenthesis, add to postfix list.
                n--;
            }else {                                 //Is operator?
                x = opAction(str, tmpStack, x, postfix); //Changes around the operators according to precedecce and assosiativity. Return priority value.
            }
        }

        if(n != 0){                                 //Checks counter for parenthesis matching. If not 0, throw error message.
            throw new IllegalArgumentException(MISSING_OPERATOR);
        }
        popAll(postfix, tmpStack);                  //Pop out all elements in stack and add to postfix.

        return postfix;
    }

    int opAction(String str, Deque<String> tmpStack, int x, List<String> postfix){
        if(getPrecedence(str) > x){                                 //Current str has higher prio
            tmpStack.push(str);                                     //Push to stack
            x = getPrecedence(str);                                 //Current priority.
        }else if(getPrecedence(str)< x){                            //Current str has higher prio
            postfix.add(tmpStack.pop());
            //popAll(postfix, tmpStack);                              ////Pop out all elements in stack and add to postfix.
            tmpStack.push(str);                                     //Push current str to stack
            x = getPrecedence(str);

        }else{
            if(getAssociativity(str).equals(Assoc.LEFT)){           //Prio is equal --> use assosiativity.
                postfix.add(tmpStack.pop());
                //popAll(postfix, tmpStack);                          //LEFT: Pop all, add to postfix.
                tmpStack.push(str);                                 //Push current str to stack
            }else if(getAssociativity(str).equals(Assoc.RIGHT)){    //RIGHT: Push to stack.
                tmpStack.push(str);
            }
        }
        return x;
    }

    void rightPar(Deque<String> tmpStack, List<String> postfix, int x){
        while(!tmpStack.isEmpty() && !tmpStack.peek().equals("(")){     //Push from stack until "(".
            postfix.add(tmpStack.pop());                                //Add to postfix.
        }
        if(!tmpStack.isEmpty()){                                        //
            x = getPrecedence(tmpStack.peek());                         //Set priority and pop "("
            tmpStack.pop();
        } else{
            x = 0;
        }
        return;
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
    List<String> tokenize(String expr) {                //From string input to list on infix form.
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        expr = expr.trim();                             //Remove whitespace in beginning or end.

        for (int i = 0; i < expr.length(); i++) {
            if (i != expr.length()-1 && Character.isDigit(expr.charAt(i)) && Character.isDigit(expr.charAt(i+1))) {
                sb.append(expr.charAt(i));              //If the element is a digit followed by a digit, append to stringbuilder.
            } else {
                sb.append(expr.charAt(i)).append(" "); //Else: append to stringbuilder followed by a whitespace.
            }
        }

        expr = sb.toString();
        String[] tokensArr = expr.split("\\s+");    //Stringsplit: Split string to words in a list.

        tokens = Arrays.asList(tokensArr);                //tokens: A list on infix form.

        return tokens;
    }


}
