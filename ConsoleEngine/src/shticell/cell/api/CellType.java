package shticell.cell.api;

import java.io.Serializable;

public enum CellType implements Serializable {

    NUMERIC(Double.class) ,
    STRING(String.class) ,
    BOOLEAN(Boolean.class),
    Empty(Void.class),   //???
    UNKNOWN(Void.class);

    private Class<?> type;

    CellType(Class<?> type) {
        this.type = type;
    }

    public boolean isAssignableFrom(Class<?> aType) {
        return type.isAssignableFrom(aType);
    }
}
