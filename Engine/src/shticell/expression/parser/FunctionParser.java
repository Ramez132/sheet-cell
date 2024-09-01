package shticell.expression.parser;

import shticell.cell.api.Cell;
import shticell.coordinate.Coordinate;
import shticell.coordinate.CoordinateFactory;
import shticell.expression.api.Expression;
import shticell.expression.impl.*;
import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public enum FunctionParser {
    IDENTITY {
        @Override
        public Expression parse(List<String> arguments) {
            // validations of the function. it should have exactly one argument
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for IDENTITY function. Expected 1, but got " + arguments.size());
            }

            // all is good. create the relevant function instance
            ///    String actualValue = arguments.get(0).trim(); //should we trim or not?
            String actualValue = arguments.get(0); //should we trim or not? if not - what happens?
            if (isBoolean(actualValue)) {
                return new IdentityExpression(Boolean.parseBoolean(actualValue), CellType.BOOLEAN);
            }
            else if (isNumeric(actualValue)) {
                return new IdentityExpression(Double.parseDouble(actualValue), CellType.NUMERIC);
            }
            else if (isEmptyStringToRepresentEmptyCell(actualValue)) {
                return new IdentityExpression(actualValue, CellType.Empty);
            }
            else {
                return new IdentityExpression(actualValue, CellType.STRING);
            }
        }

        private boolean isEmptyStringToRepresentEmptyCell(String actualValue) {
            return actualValue.isEmpty();
        }

        private boolean isBoolean(String value) {
            return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
        }

        private boolean isNumeric(String value) {
            try {
                Double.parseDouble(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    },
    PLUS {
        @Override
        public Expression parse(List<String> arguments) {
            // validations of the function (e.g. number of arguments)
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for PLUS function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim());
            Expression right = parseExpression(arguments.get(1).trim());

            // more validations on the expected argument types
            CellType leftCellType = left.getFunctionResultType();
            CellType rightCellType = right.getFunctionResultType();
            // support UNKNOWN type as its value will be determined at runtime
            if ( (!leftCellType.equals(CellType.NUMERIC) && !leftCellType.equals(CellType.UNKNOWN)) ||
                    (!rightCellType.equals(CellType.NUMERIC) && !rightCellType.equals(CellType.UNKNOWN)) ) {
                throw new IllegalArgumentException("Invalid argument types for PLUS function. Expected NUMERIC, but got " + leftCellType + " and " + rightCellType);
            }

            // all is good. create the relevant function instance
            return new PlusExpression(left, right);
        }
    },
    MINUS {
        @Override
        public Expression parse(List<String> arguments) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for MINUS function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim());
            Expression right = parseExpression(arguments.get(1).trim());

            // more validations on the expected argument types
            if (!left.getFunctionResultType().equals(CellType.NUMERIC) || !right.getFunctionResultType().equals(CellType.NUMERIC)) {
                throw new IllegalArgumentException("Invalid argument types for MINUS function. Expected NUMERIC, but got " + left.getFunctionResultType() + " and " + right.getFunctionResultType());
            }

            // all is good. create the relevant function instance
            return new MinusExpression(left, right);
        }
    },
    UPPER_CASE {
        @Override
        public Expression parse(List<String> arguments) {
            // validations of the function. it should have exactly one argument
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for UPPER_CASE function. Expected 1, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression arg = parseExpression(arguments.get(0).trim());

            // more validations on the expected argument types
            if (!arg.getFunctionResultType().equals(CellType.STRING)) {
                throw new IllegalArgumentException("Invalid argument types for UPPER_CASE function. Expected STRING, but got " + arg.getFunctionResultType());
            }

            // all is good. create the relevant function instance
            return new UpperCaseExpression(arg);
        }
    },
    REF {
        @Override
        public Expression parse(List<String> arguments) {
            // validations of the function. it should have exactly one argument
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for REF function. Expected 1, but got " + arguments.size());
            }

            // verify indeed argument represents a reference to a cell and create a Coordinate instance. if not ok returns a null. need to verify it
            Coordinate target = CoordinateFactory.getCoordinateFromStr(arguments.get(0).trim());
            if (target == null) {
                throw new IllegalArgumentException("Invalid argument for REF function. Expected a valid cell reference, but got " + arguments.get(0));
            }

            // should verify if the coordinate is within boundaries of the sheet ?
            // ...

            return new RefExpression(target);
        }
    },
    TIMES {
        @Override
        public Expression parse(List<String> arguments) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for TIMES function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim());
            Expression right = parseExpression(arguments.get(1).trim());

            // more validations on the expected argument types
            if (!left.getFunctionResultType().equals(CellType.NUMERIC) || !right.getFunctionResultType().equals(CellType.NUMERIC)) {
                throw new IllegalArgumentException("Invalid argument types for TIMES function. Expected NUMERIC, but got " + left.getFunctionResultType() + " and " + right.getFunctionResultType());
            }

            // all is good. create the relevant function instance
            return new TimesExpression(left, right);
        }
    },
    ABS {
        @Override
        public Expression parse(List<String> arguments) {
            // Ensure there is exactly one argument
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for ABS function. Expected 1, but got " + arguments.size());
            }

            // Parse the argument
            Expression argument = parseExpression(arguments.get(0).trim());

            // Validate that the argument is numeric or unknown
            CellType argumentCellType = argument.getFunctionResultType();
            if (!argumentCellType.equals(CellType.NUMERIC) && !argumentCellType.equals(CellType.UNKNOWN)) {
                throw new IllegalArgumentException("Invalid argument type for ABS function. Expected NUMERIC, but got " + argumentCellType);
            }

            // Create and return the AbsExpression
            return new AbsExpression(argument);
        }
    },
    POW {
        @Override
        public Expression parse(List<String> arguments) {
            // Ensure there are exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for POW function. Expected 2, but got " + arguments.size());
            }

            // Parse the arguments
            Expression base = parseExpression(arguments.get(0).trim());
            Expression exponent = parseExpression(arguments.get(1).trim());

            // Validate that the arguments are numeric or unknown
            CellType baseCellType = base.getFunctionResultType();
            CellType exponentCellType = exponent.getFunctionResultType();
            if ((!baseCellType.equals(CellType.NUMERIC) && !baseCellType.equals(CellType.UNKNOWN)) ||
                (!exponentCellType.equals(CellType.NUMERIC) && !exponentCellType.equals(CellType.UNKNOWN))) {
                throw new IllegalArgumentException("Invalid argument types for POW function. Expected NUMERIC, but got " + baseCellType + " and " + exponentCellType);
            }

            // Create and return the PowExpression
            return new PowExpression(base, exponent);
        }
    },
    MODULO {
        @Override
        public Expression parse(List<String> arguments) {
            // Ensure there are exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for MODULO function. Expected 2, but got " + arguments.size());
            }

            // Parse the arguments
            Expression dividend = parseExpression(arguments.get(0).trim());
            Expression divisor = parseExpression(arguments.get(1).trim());

            // Validate that the arguments are numeric or unknown
            CellType dividendCellType = dividend.getFunctionResultType();
            CellType divisorCellType = divisor.getFunctionResultType();
            if ((!dividendCellType.equals(CellType.NUMERIC) && !dividendCellType.equals(CellType.UNKNOWN)) ||
                (!divisorCellType.equals(CellType.NUMERIC) && !divisorCellType.equals(CellType.UNKNOWN))) {
                throw new IllegalArgumentException("Invalid argument types for MODULO function. Expected NUMERIC, but got " + dividendCellType + " and " + divisorCellType);
            }

            // Create and return the ModuloExpression
            return new ModuloExpression(dividend, divisor);
        }
    },
    DIVIDE {
        @Override
        public Expression parse(List<String> arguments) {
            // Ensure there are exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for DIVIDE function. Expected 2, but got " + arguments.size());
            }

            // Parse the arguments
            Expression dividend = parseExpression(arguments.get(0).trim());
            Expression divisor = parseExpression(arguments.get(1).trim());

            // Validate that the arguments are numeric or unknown
            CellType dividendCellType = dividend.getFunctionResultType();
            CellType divisorCellType = divisor.getFunctionResultType();
            if ((!dividendCellType.equals(CellType.NUMERIC) && !dividendCellType.equals(CellType.UNKNOWN)) ||
                (!divisorCellType.equals(CellType.NUMERIC) && !divisorCellType.equals(CellType.UNKNOWN))) {
                throw new IllegalArgumentException("Invalid argument types for DIVIDE function. Expected NUMERIC, but got " + dividendCellType + " and " + divisorCellType);
            }

            // Create and return the DivideExpression
            return new DivideExpression(dividend, divisor);
        }
    },
    CONCAT {
        @Override
        public Expression parse(List<String> arguments) {
            // Ensure there are at least two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for CONCAT function. Expected 2, but got " + arguments.size());
            }

            // Parse the arguments
            List<Expression> expressions = new ArrayList<>();
            for (String argument : arguments) {
                expressions.add(parseExpression(argument));
            }

            // Validate that the arguments are strings or unknown
            for (Expression currentExpression : expressions) {
                CellType expressionCellType = currentExpression.getFunctionResultType();
                if (!expressionCellType.equals(CellType.STRING) && !expressionCellType.equals(CellType.UNKNOWN)) {
                    throw new IllegalArgumentException("Invalid argument types for CONCAT function. Expected STRING, but got " + expressionCellType);
                }
            }

            // Create and return the ConcatExpression
            return new ConcatExpression(expressions.get(0), expressions.get(1));
        }
    }
    ;

    abstract public Expression parse(List<String> arguments);

    public static Expression parseExpression(String input) {

        if (input.startsWith("{") && input.endsWith("}")) {

            String functionContent = input.substring(1, input.length() - 1);
            List<String> topLevelParts = parseMainParts(functionContent);


            String functionName = topLevelParts.get(0).trim().toUpperCase();

            //remove the first element from the array
            topLevelParts.remove(0);
            if (!isValidFunctionName(functionName)) {
                throw new IllegalArgumentException("Invalid function name: " + functionName);
            }
            return FunctionParser.valueOf(functionName).parse(topLevelParts);
            //valueOf - If the name does not match any existing enum constant, it throws an IllegalArgumentException.
            //should we through an exception if the function name is not found?

            //where do I check if the number of arguments is correct?
        }

        // handle identity expression
        ///////         return FunctionParser.IDENTITY.parse(List.of(input.trim())); //should we trim or not?
        return FunctionParser.IDENTITY.parse(List.of(input)); //should we trim or not?
    }


    public static boolean isValidFunctionName(String functionName) {

        boolean isValidFuncName;

        if (functionName == null) {
            isValidFuncName = false;
        }
        try {
            FunctionParser.valueOf(functionName);
            isValidFuncName = true;
        } catch (IllegalArgumentException e) {
            isValidFuncName = false;
        }

        return isValidFuncName;
    }

//    public class EnumUtils {
//    public static <T extends Enum<T>> boolean isValidEnum(Class<T> enumClass, String enumName) {
//        if (enumName == null) {
//            return false;
//        }
//        try {
//            Enum.valueOf(enumClass, enumName);
//            return true;
//        } catch (IllegalArgumentException e) {
//            return false;
//        }
//    }
//}

    private static List<String> parseMainParts(String input) {
        List<String> parts = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        for (char c : input.toCharArray()) {
            if (c == '{') {
                stack.push(c);
            } else if (c == '}') {
                stack.pop();
            }

            if (c == ',' && stack.isEmpty()) {
                // If we are at a comma and the stack is empty, it's a separator for top-level parts
                //////////   parts.add(buffer.toString().trim());  //should we trim or not?
                parts.add(buffer.toString());  //should we trim or not?
                buffer.setLength(0); // Clear the buffer for the next part
            } else {
                buffer.append(c);
            }
        }

        // Add the last part
        if (buffer.length() > 0) {
            /////// parts.add(buffer.toString().trim());  //should we trim or not?
            parts.add(buffer.toString());  //should we trim or not?
        }

        return parts;
    }

    public static void main(String[] args) {

        //String input = "plus, {plus, 1, 2}, {plus, 1, {plus, 1, 2}}";
//        String input = "1";
//        parseMainParts(input).forEach(System.out::println);

//        String input = "{plus, 1, 2}";
        String input = "{plus, {minus, 44, 22}, {plus, 1, 2}}";
//        String input = "{upper_case, hello world}";
//        String input = "4";
        Expression expression = parseExpression(input);
        EffectiveValue result = expression.eval(null);
        System.out.println("result: " + result.getValue() + " of type " + result.getCellType());
    }

}