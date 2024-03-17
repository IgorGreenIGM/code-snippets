import java.lang.Math;
import java.util.Stack;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.util.EmptyStackException;


/**
 * The Evaluator class provides methods for evaluating mathematical expressions
 * and generating random arithmetic expressions to a specified file.
 */
public class Evaluator {

    /**
     * Constructs a new Evaluator object.
     */
    public Evaluator() {}


    /**
     * Determines the priority of an operator.
     *
     * @param operator The operator whose priority is to be determined.
     * @return The priority level of the operator.
     * @throws RuntimeException if the input operator is invalid.
     */
    private int operatorPriority(char operator) throws RuntimeException {
        if (operator == '^')
            return 2;
        else if (operator == '/' || operator == '*' || operator == '%')
            return 1;
        else if (operator == '+' || operator == '-')
            return 0;
        else
            throw new RuntimeException("Bad input operator");
    }

    /**
     * Checks if a character is an operator.
     *
     * @param operator The character to be checked.
     * @return true if the character is an operator, otherwise false.
     */
    private boolean isOperator(char operator) {
        return operator == '+' || operator == '-' || operator == '*' || operator == '/' || operator == '%' || operator == '^';
    }

    /**
     * Converts an infix expression to a postfix expression.
     *
     * @param infix The infix expression to be converted.
     * @return The postfix representation of the infix expression.
     * @throws RuntimeException if the input infix expression is invalid.
     */
    private String infixToPostfix(String infix) throws RuntimeException
    {
        char token;
        String result = "";
        Stack<Character> operators = new Stack<Character>();

        for (int i = 0; i < infix.length(); ++i) {
            token = infix.charAt(i);

            if (Character.isDigit(token) || token == '.') {
                result += token;

            } else if (isOperator(token)){
                result += ' ';
                while ((!operators.empty()) && isOperator(operators.peek())) {
                    if (((token == '*' || token == '+' || token == '-' || token == '/' || token == '%') && operatorPriority(token) <= operatorPriority(operators.peek())) || (token == '^' && operatorPriority(token) < operatorPriority(operators.peek())))
                        result += operators.pop();
                    else
                        break;
                }
                operators.add(token);

            } else if (token == '(') {
                operators.add(token);
            
            } else if (token == ')') {
                while (operators.peek() != '(') {
                    result += operators.pop();
                }

                if (operators.empty())
                    throw new RuntimeException("Error, bad parenthesis");
                else
                    operators.pop();
            }
        }

        while (!operators.empty()) {
            token = operators.pop();
            if (token == '(')
                throw new RuntimeException("Errorn bad parenthesis");
            else
                result += token;
        }
    
        return result;
    }


    /**
     * Evaluates a mathematical expression.
     *
     * @param toEval The expression to be evaluated.
     * @return The result of the evaluation.
     * @throws RuntimeException if the input expression is invalid or contains errors.
     */
    public double evaluate(String toEval) throws RuntimeException
    {
        String postFix;
        try{
            postFix = infixToPostfix(toEval);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Error, bad input String, cannot convert to postfix repr");
        }
        
        Stack<Double> stack = new Stack<Double>();
        System.out.println(postFix);
        for (int i = 0; i < postFix.length(); ++i)
        {
            char token = postFix.charAt(i);

            if (Character.isDigit(token) || token == '.'){
                int j = 1;
                char nextToken = postFix.charAt(i + j);
                while (Character.isDigit(nextToken) || nextToken == '.') {
                    ++j;
                    nextToken = postFix.charAt(i + j);
                }
                stack.add(Double.parseDouble(postFix.substring(i, i + j)));
                i += j - 1;

            }else if (isOperator(token)){
                double operand1;
                double operand2;
                double result;
                try{
                    operand1 = stack.pop();
                    operand2 = stack.pop();
                } catch (EmptyStackException e) {
                    throw new RuntimeException("Error, Bad input String");
                }

                switch (token) {
                    case '+':
                        result = operand2 + operand1;
                        break;
                    
                    case '-':
                        result = operand2 - operand1;
                        break;

                    case '*':
                        result = operand2 * operand1;
                        break;

                    case '/':
                        if (operand1 == 0.0)
                            throw new RuntimeException("Zero Division Error");
                        else
                            result = operand2 / operand1;
                        break;
                    
                    case '^':
                        result = Math.pow(operand2, operand1);
                        break;

                    case '%':
                        if (operand1 == 0.0)
                            throw new RuntimeException("Zero Division Error");
                        else
                            result = operand2 % operand1;
                        break;

                    default:
                        throw new RuntimeException("Error, Bad operator : " + token);
                }

                stack.add(result);
            
            } else if (token == ' ') {
                continue;
            
            }else
                throw new RuntimeException("Bad digit in string to eval : " +token);
        }

        return stack.pop();
    }

    /**
     * Generates random arithmetic expressions and writes them to a specified file.
     *
     * @param outputFile  The path of the file where expressions will be written.
     * @param maxOperators The maximum number of operators in each generated expression.
     * @param linesCount   The number of expressions to generate.
     */
    public static void generateExpressions(String outputFile, int maxOperators, int linesCount) {

        BufferedWriter fp = null;
        try {
            fp = new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Random rnd = new Random();
        String[] operators = { "+", "-", "*", "/", "%" };

        for (int lineCpt = 0; lineCpt < linesCount; ++lineCpt) {
            String toEval = "";
            toEval += rnd.nextInt(1, 100);
            for (int operandCpt = 0; operandCpt < rnd.nextInt(1, maxOperators) + 1; ++operandCpt) {
                toEval += operators[rnd.nextInt(0, operators.length)];
                toEval += rnd.nextInt(1, 100);
            }

            try {
                fp.write(toEval);
                fp.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fp.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
