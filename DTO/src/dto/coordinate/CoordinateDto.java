package dto.coordinate;

public record CoordinateDto(int row, int column) {

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        String columnLetter = Character.toString((char) (column - 1 + 'A'));
        return columnLetter + row;
    }
}
