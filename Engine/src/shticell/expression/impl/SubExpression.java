package shticell.expression.impl;

import shticell.cell.api.CellType;
import shticell.cell.api.EffectiveValue;
import shticell.expression.api.Expression;
import shticell.sheet.api.SheetReadActions;
import shticell.cell.impl.EffectiveValueImpl;

public class SubExpression implements Expression {
    private final Expression source;
    private final Expression start_index;
    private final Expression end_index;

    public SubExpression(Expression source, Expression start_index, Expression end_index) {
        this.source = source;
        this.start_index = start_index;
        this.end_index = end_index;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        EffectiveValue sourceValue = source.eval(sheet);
        EffectiveValue startIndexValue = start_index.eval(sheet);
        EffectiveValue endIndexValue = end_index.eval(sheet);

        // Convert the values to appropriate types
        String source_string = sourceValue.extractValueWithExpectation(String.class);
        Double converted_start_index = startIndexValue.extractValueWithExpectation(Double.class);
        Double converted_end_index = endIndexValue.extractValueWithExpectation(Double.class);

        // Check for type conversion issues
        if (source_string == null) {
            throw new IllegalArgumentException("Error: The source provided to the SUB function is not a valid string. " +
                    "Please ensure that the source argument is a valid string value.");
        }
        if (converted_start_index == null || converted_end_index == null) {
            throw new IllegalArgumentException("Error: The start or end index provided to the SUB function is not numeric. " +
                    "Please ensure that both indices are valid numeric values.");
        }
        if (converted_start_index % 1 != 0 || converted_end_index % 1 != 0) {
            return new EffectiveValueImpl(CellType.STRING, "!UNDEFINED!");
        }
        if (converted_start_index < 0 || converted_end_index < 0 || converted_start_index > converted_end_index ) {
            return new EffectiveValueImpl(CellType.STRING, "!UNDEFINED!");
        }
        if (!(converted_start_index < source_string.length() && converted_end_index < source_string.length())) {
            return new EffectiveValueImpl(CellType.STRING, "!UNDEFINED!");
        }

        int start = converted_start_index.intValue();
        int end = converted_end_index.intValue();

        String result = source_string.substring(start - 1, end - 1);
        return new EffectiveValueImpl(CellType.STRING, result);

    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.STRING;
    }
}
