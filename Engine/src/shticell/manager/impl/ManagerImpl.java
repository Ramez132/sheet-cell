package shticell.manager.impl;

import shticell.cell.api.Cell;
import shticell.manager.api.Manager;
import shticell.sheet.api.Sheet;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ManagerImpl implements Manager {
    private List<Sheet> sheetVersionsArray;

    public ManagerImpl() {
        this.sheetVersionsArray = new ArrayList<Sheet>();
    }

    @Override
    public Sheet getSheetFromFile(String fileName) {
        // check if the name ends with .xml - > not? throw an exception
        // try to open the file - > fails? throw an exception
        // make the file go through auto-generated classes
        // check parameters - i.e. all cells in sheet range - > fails? throw an exception
        // if all is good - clear sheetVersionsArray and add the new sheet to the array

        return null;
    }

    @Override
    public Sheet getMostRecentSheet() throws NoSuchElementException {
        if (!sheetVersionsArray.isEmpty()) {
            return sheetVersionsArray.getLast();
        }
        else {
            throw new NoSuchElementException("There is no sheet to display.");
        }
    }

//    @Override
//    public Sheet displaySheet(Sheet sheet) {
//        int index = sheetVersionsArray.indexOf(sheet);
//        return List<>
//    }

    @Override
    public Cell getCellFromSheet(Sheet sheet, int row, int column) {
        return sheet.getCell(row, column); //is it the expected behavior?
    }

    @Override
    public Sheet updateValueOfCellAndDisplayNewSheet(Sheet sheet, int row, int col, String value) throws RuntimeException {
        try {
            return sheet.updateCellValueAndCalculate(row, col, value);
        }
        catch (Exception e) { //should we catch a more specific Exception? could there be a few types?
            throw new RuntimeException(e);
        }
    }

    /**
     * Expects for version number that is
     * @param version number of requested version, starting from 1 (not 0)
     *
     */
    @Override
    public Sheet getSheetOfSpecificVersion(int version) throws NoSuchElementException, IndexOutOfBoundsException {
        try {
            if (!sheetVersionsArray.isEmpty()) {
                return sheetVersionsArray.get(version - 1);
            }
            else {
                throw new NoSuchElementException("There are no sheets in the system.");
            }
        }
        catch(IndexOutOfBoundsException e){
            throw new IndexOutOfBoundsException("The requested version is not available.");
        }
    }

    /**
     * Returns a counter that starts from 1 (not 0).
     *
     * @return the number of the latest version, starting from 1
     */
    @Override
    public int getLatestVersionNumber() throws NoSuchElementException {
        if (!sheetVersionsArray.isEmpty()) {
            return sheetVersionsArray.size();
        }
        else {
            throw new NoSuchElementException("There are no sheets in the system.");
        }
    }
}
