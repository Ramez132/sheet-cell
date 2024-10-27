package shticell.expression.parser;

import shticell.cell.api.CellType;
import shticell.coordinate.Coordinate;
import shticell.coordinate.CoordinateFactory;
import shticell.expression.api.Expression;
import shticell.expression.impl.*;
import shticell.sheet.api.SheetReadActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public enum FunctionParser {
    IDENTITY {
        @Override
        public Expression parse(List<String> arguments, SheetReadActions sheet) {
            // validations of the function. it should have exactly one argument
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for IDENTITY function. Expected 1, but got " + arguments.size());
            }

            // all is good. create the relevant function instance
            ///    String actualValue = arguments.get(0).trim(); //should we trim or not?
            String actualValue = arguments.getFirst(); //should we trim or not? if not - what happens?
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
        public Expression parse(List<String> arguments , SheetReadActions sheet) {
            // validations of the function (e.g. number of arguments)
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for PLUS function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

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
        public Expression parse(List<String> arguments , SheetReadActions sheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for MINUS function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);
            CellType leftCellType = left.getFunctionResultType();
            CellType rightCellType = right.getFunctionResultType();
            // more validations on the expected argument types
            if ( (!leftCellType.equals(CellType.NUMERIC) && !leftCellType.equals(CellType.UNKNOWN)) ||
                    (!rightCellType.equals(CellType.NUMERIC) && !rightCellType.equals(CellType.UNKNOWN)) ) {
                throw new IllegalArgumentException("Invalid argument types for MINUS function. Expected NUMERIC, but got " + leftCellType + " and " + rightCellType);
            }

            // all is good. create the relevant function instance
            return new MinusExpression(left, right);
        }
    },
    REF {
        @Override
        public Expression parse(List<String> arguments, SheetReadActions sheet) {
            // validations of the function. it should have exactly one argument
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for REF function. Expected 1, but got " + arguments.size());
            }

            // verify indeed argument represents a reference to a cell and create a Coordinate instance. if not ok returns a null. need to verify it
            Coordinate target = CoordinateFactory.getCoordinateFromStr(arguments.getFirst().trim(), sheet);
            if (target == null) {
                throw new IllegalArgumentException("Invalid argument for REF function. Expected a valid cell reference, but got " + arguments.getFirst());
            }
            try {
                boolean isCoordinateInSheetRange = sheet.isCoordinateInSheetRange(target.getRow(), target.getColumn());
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("There is a referenced cell which is not in the sheet range: " + e.getMessage());
            }

            return new RefExpression(target);
        }
    },
    TIMES {
        @Override
        public Expression parse(List<String> arguments, SheetReadActions sheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for TIMES function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);
            CellType leftCellType = left.getFunctionResultType();
            CellType rightCellType = right.getFunctionResultType();
            // more validations on the expected argument types
            if ( (!leftCellType.equals(CellType.NUMERIC) && !leftCellType.equals(CellType.UNKNOWN)) ||
                    (!rightCellType.equals(CellType.NUMERIC) && !rightCellType.equals(CellType.UNKNOWN)) ) {
                throw new IllegalArgumentException("Invalid argument types for TIMES function. Expected NUMERIC, but got " + leftCellType + " and " + rightCellType);
            }

            // all is good. create the relevant function instance
            return new TimesExpression(left, right);
        }
    },
    ABS {
        @Override
        public Expression parse(List<String> arguments, SheetReadActions sheet) {
            // Ensure there is exactly one argument
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for ABS function. Expected 1, but got " + arguments.size());
            }

            // Parse the argument
            Expression argument = parseExpression(arguments.get(0).trim(), sheet);

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
        public Expression parse(List<String> arguments, SheetReadActions sheet) {
            // Ensure there are exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for POW function. Expected 2, but got " + arguments.size());
            }

            // Parse the arguments
            Expression base = parseExpression(arguments.get(0).trim(), sheet);
            Expression exponent = parseExpression(arguments.get(1).trim(), sheet);

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
        public Expression parse(List<String> arguments, SheetReadActions sheet) {
            // Ensure there are exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for MODULO function. Expected 2, but got " + arguments.size());
            }

            // Parse the arguments
            Expression dividend = parseExpression(arguments.get(0).trim(), sheet);
            Expression divisor = parseExpression(arguments.get(1).trim(), sheet);

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
        public Expression parse(List<String> arguments, SheetReadActions sheet) {
            // Ensure there are exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for DIVIDE function. Expected 2, but got " + arguments.size());
            }

            // Parse the arguments
            Expression dividend = parseExpression(arguments.get(0).trim(), sheet);
            Expression divisor = parseExpression(arguments.get(1).trim(), sheet);

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
        public Expression parse(List<String> arguments, SheetReadActions sheet) {
            // Ensure there are at least two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for CONCAT function. Expected 2, but got " + arguments.size());
            }

            // Parse the arguments
            List<Expression> expressions = new ArrayList<>();
            for (String argument : arguments) {
                expressions.add(parseExpression(argument, sheet));
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
    },
    SUB {
        @Override
        public Expression parse(List<String> arguments, SheetReadActions sheet) {
            // Ensure there are at least two arguments
            if (arguments.size() != 3) {
                throw new IllegalArgumentException("Invalid number of arguments for CONCAT function. Expected 2, but got " + arguments.size());
            }

            // Parse the arguments
            List<Expression> expressions = new ArrayList<>();
            for (String argument : arguments) {
                expressions.add(parseExpression(argument, sheet));
            }
            Expression firstExpression = expressions.getFirst();
            CellType expressionCellType = firstExpression.getFunctionResultType();

            if (!expressionCellType.equals(CellType.STRING) && !expressionCellType.equals(CellType.UNKNOWN)) {
                throw new IllegalArgumentException("Invalid argument types for SUB function. Expected STRING, but got " + expressionCellType);
            }
            Expression secondExpression = expressions.get(1);
            Expression thirdExpression = expressions.get(2);
            CellType secondExpressionCellType = secondExpression.getFunctionResultType();
            CellType thirdExpressionCellType = thirdExpression.getFunctionResultType();
            if (!secondExpressionCellType.equals(CellType.NUMERIC) && !secondExpressionCellType.equals(CellType.UNKNOWN)) {
                throw new IllegalArgumentException("Invalid argument types for SUB function. Expected NUMERIC, but got " + secondExpressionCellType);
            }
            if (!thirdExpressionCellType.equals(CellType.NUMERIC) && !thirdExpressionCellType.equals(CellType.UNKNOWN)) {
                throw new IllegalArgumentException("Invalid argument types for SUB function. Expected NUMERIC, but got " + thirdExpressionCellType);
            }
//            // Validate that the arguments are strings or unknown
//            for (Expression currentExpression : expressions) {
//                expressionCellType = currentExpression.getFunctionResultType();
//                if (!expressionCellType.equals(CellType.STRING) && !expressionCellType.equals(CellType.UNKNOWN)) {
//                    throw new IllegalArgumentException("Invalid argument types for SUB function. Expected STRING, but got " + expressionCellType);
//                }
//            }

            // Create and return the ConcatExpression
            return new SubExpression(expressions.get(0), expressions.get(1), expressions.get(2));
        }
    },
    SUM {
        @Override
        public Expression parse(List<String> arguments, SheetReadActions sheet) {
            // Ensure there is exactly one argument
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for SUM function. Expected a single range name, but got " + arguments.size());
            }

            String rangeName = arguments.getFirst().trim();
            Expression rangeNameExpression = parseExpression(rangeName, sheet);

            CellType expressionCellType = rangeNameExpression.getFunctionResultType();
            if (!expressionCellType.equals(CellType.STRING)) {
                throw new IllegalArgumentException("Invalid argument type for SUM function. Expected STRING that represent a range name, but got " + expressionCellType);
            }

            // Create and return the SumExpression
            return new SumExpression(rangeNameExpression);
        }
    },
    AVERAGE {
        @Override
        public Expression parse(List<String> arguments, SheetReadActions sheet) {
            // Ensure there is exactly one argument
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for AVERAGE function. Expected a single range name, but got " + arguments.size());
            }

            String rangeName = arguments.getFirst().trim();
            Expression rangeNameExpression = parseExpression(rangeName, sheet);

            CellType expressionCellType = rangeNameExpression.getFunctionResultType();
            if (!expressionCellType.equals(CellType.STRING)) {
                throw new IllegalArgumentException("Invalid argument type for AVERAGE function. Expected STRING that represent a range name, but got " + expressionCellType);
            }

            // Create and return the AverageExpression
            return new AverageExpression(rangeNameExpression);
        }
    },
    PERCENT {
        @Override
        public Expression parse(List<String> arguments, SheetReadActions sheet) {
            // Ensure there are exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for PERCENT function. Expected 2, but got " + arguments.size());
            }

            // Parse the arguments
            Expression part = parseExpression(arguments.get(0).trim(), sheet);
            Expression whole = parseExpression(arguments.get(1).trim(), sheet);

            // Validate that the arguments are numeric or unknown
            CellType partCellType = part.getFunctionResultType();
            CellType wholeCellType = whole.getFunctionResultType();
            if ((!partCellType.equals(CellType.NUMERIC) && !partCellType.equals(CellType.UNKNOWN)) ||
                (!wholeCellType.equals(CellType.NUMERIC) && !wholeCellType.equals(CellType.UNKNOWN))) {
                throw new IllegalArgumentException("Invalid argument types for PERCENT function. Expected NUMERIC, but got " + partCellType + " and " + wholeCellType);
            }

            // Create and return the PercentExpression
            return new PercentExpression(part, whole);
        }
    },
    EQUAL {
        @Override
        public Expression parse(List<String> arguments, SheetReadActions sheet) {
            // Ensure there are exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for EQUAL function. Expected 2, but got " + arguments.size());
            }

            // Parse the arguments
            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

            // Create and return the EqualExpression
            return new EqualExpression(left, right);
        }
    },
    NOT {
        @Override
        public Expression parse(List<String> arguments, SheetReadActions sheet) {
            // Ensure there is exactly one argument
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for NOT function. Expected 1, but got " + arguments.size());
            }

            // Parse the argument
            Expression argument = parseExpression(arguments.get(0).trim(), sheet);

            // Validate that the argument is boolean or unknown
            CellType argumentCellType = argument.getFunctionResultType();
            if (!argumentCellType.equals(CellType.BOOLEAN) && !argumentCellType.equals(CellType.UNKNOWN)) {
                throw new IllegalArgumentException("Invalid argument type for NOT function. Expected BOOLEAN, but got " + argumentCellType);
            }

            // Create and return the NotExpression
            return new NotExpression(argument);
        }
    },
    OR {
        @Override
        public Expression parse(List<String> arguments, SheetReadActions sheet) {
            // Ensure there are exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for OR function. Expected 2, but got " + arguments.size());
            }

            // Parse the arguments
            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

            // Validate that the arguments are boolean or unknown
            CellType leftCellType = left.getFunctionResultType();
            CellType rightCellType = right.getFunctionResultType();
            if (!leftCellType.equals(CellType.BOOLEAN) && !leftCellType.equals(CellType.UNKNOWN)) {
                throw new IllegalArgumentException("Invalid argument type for OR function. Expected BOOLEAN, but got " + leftCellType);
            }
            if (!rightCellType.equals(CellType.BOOLEAN) && !rightCellType.equals(CellType.UNKNOWN)) {
                throw new IllegalArgumentException("Invalid argument type for OR function. Expected BOOLEAN, but got " + rightCellType);
            }

            // Create and return the OrExpression
            return new OrExpression(left, right);
        }
    },
    AND {
        @Override
        public Expression parse(List<String> arguments, SheetReadActions sheet) {
            // Ensure there are at least two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for AND function. Expected at least 2, but got " + arguments.size());
            }

            // Parse the arguments
            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

            // Validate that the arguments are boolean or unknown
            CellType leftCellType = left.getFunctionResultType();
            CellType rightCellType = right.getFunctionResultType();
            if (!leftCellType.equals(CellType.BOOLEAN) && !leftCellType.equals(CellType.UNKNOWN)) {
                throw new IllegalArgumentException("Invalid argument type for OR function. Expected BOOLEAN, but got " + leftCellType);
            }
            if (!rightCellType.equals(CellType.BOOLEAN) && !rightCellType.equals(CellType.UNKNOWN)) {
                throw new IllegalArgumentException("Invalid argument type for OR function. Expected BOOLEAN, but got " + rightCellType);
            }

            // Create and return the AndExpression
            return new AndExpression(left, right);
        }
    },
    BIGGER {
        @Override
        public Expression parse(List<String> arguments, SheetReadActions sheet) {
            // Ensure there are exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for BIGGER function. Expected 2, but got " + arguments.size());
            }

            // Parse the arguments
            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

            // Validate that the arguments are numeric or unknown
            CellType leftCellType = left.getFunctionResultType();
            CellType rightCellType = right.getFunctionResultType();
            if ((!leftCellType.equals(CellType.NUMERIC) && !leftCellType.equals(CellType.UNKNOWN)) ||
                (!rightCellType.equals(CellType.NUMERIC) && !rightCellType.equals(CellType.UNKNOWN))) {
                throw new IllegalArgumentException("Invalid argument types for BIGGER function. Expected NUMERIC, but got " + leftCellType + " and " + rightCellType);
            }

            // Create and return the BiggerExpression
            return new BiggerExpression(left, right);
        }
    },
    LESS {
        @Override
        public Expression parse(List<String> arguments, SheetReadActions sheet) {
            // Ensure there are exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for LESS function. Expected 2, but got " + arguments.size());
            }

            // Parse the arguments
            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

            // Validate that the arguments are numeric or unknown
            CellType leftCellType = left.getFunctionResultType();
            CellType rightCellType = right.getFunctionResultType();
            if ((!leftCellType.equals(CellType.NUMERIC) && !leftCellType.equals(CellType.UNKNOWN)) ||
                (!rightCellType.equals(CellType.NUMERIC) && !rightCellType.equals(CellType.UNKNOWN))) {
                throw new IllegalArgumentException("Invalid argument types for LESS function. Expected NUMERIC, but got " + leftCellType + " and " + rightCellType);
            }

            // Create and return the LessExpression
            return new LessExpression(left, right);
        }
    },
    IF {
        @Override
        public Expression parse(List<String> arguments, SheetReadActions sheet) {
            // Ensure there are exactly three arguments
            if (arguments.size() != 3) {
                throw new IllegalArgumentException("Invalid number of arguments for IF function. Expected 3, but got " + arguments.size());
            }

            // Parse the arguments
            Expression condition = parseExpression(arguments.get(0).trim(), sheet);
            Expression trueExpression = parseExpression(arguments.get(1).trim(), sheet);
            Expression falseExpression = parseExpression(arguments.get(2).trim(), sheet);

            // Validate that the condition is boolean or unknown
            CellType conditionCellType = condition.getFunctionResultType();
            if (!conditionCellType.equals(CellType.BOOLEAN) && !conditionCellType.equals(CellType.UNKNOWN)) {
                throw new IllegalArgumentException("Invalid argument type for IF function. Expected BOOLEAN, but got " + conditionCellType);
            }

            // Create and return the IfExpression
            return new IfExpression(condition, trueExpression, falseExpression);
        }
    },
    ;

    abstract public Expression parse(List<String> arguments, SheetReadActions sheet);

    public static Expression parseExpression(String input, SheetReadActions sheet) {

        if (input.startsWith("{") && input.endsWith("}")) {

            String functionContent = input.substring(1, input.length() - 1);
            List<String> topLevelParts = parseMainParts(functionContent);


            String functionName = topLevelParts.get(0).trim().toUpperCase();

            //remove the first element from the array
            topLevelParts.removeFirst();
            if (!isValidFunctionName(functionName)) {
                throw new IllegalArgumentException("Invalid function name: " + functionName);
            }
            return FunctionParser.valueOf(functionName).parse(topLevelParts, sheet);
            //valueOf - If the name does not match any existing enum constant, it throws an IllegalArgumentException.
            //should we through an exception if the function name is not found?
        }

        return FunctionParser.IDENTITY.parse(List.of(input), sheet); //should we trim or not?
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

}