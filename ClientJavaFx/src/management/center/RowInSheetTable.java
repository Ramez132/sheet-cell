package management.center;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class RowInSheetTable {
    private final SimpleStringProperty sheetName;
    private final SimpleStringProperty ownerName;
    private final SimpleIntegerProperty numRows;
    private final SimpleIntegerProperty numCols;
    private final SimpleStringProperty permissionLevel;

    public RowInSheetTable(String sheetName, String ownerName, int numRows, int numCols, String permissionLevel) {
        this.sheetName = new SimpleStringProperty(sheetName);
        this.ownerName = new SimpleStringProperty(ownerName);
        this.numRows = new SimpleIntegerProperty(numRows);
        this.numCols = new SimpleIntegerProperty(numCols);
        this.permissionLevel = new SimpleStringProperty(permissionLevel);
    }

    public String getSheetName() { return sheetName.get(); }
    public String getPermissionLevel() { return permissionLevel.get(); }

}
