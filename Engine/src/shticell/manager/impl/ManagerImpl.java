package shticell.manager.impl;

import shticell.cell.api.Cell;
import shticell.manager.api.Manager;
import shticell.sheet.api.Sheet;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ManagerImpl implements Manager {
    private List<Sheet> sheetVersions;
    public ManagerImpl() {
        this.sheetVersions = new ArrayList<Sheet>();
    }

    @Override
    public Sheet getSheetFromFile(String fileName) {
        return null;
    }

    @Override
    public Sheet displayMostRecentSheet() {
        if (!sheetVersions.isEmpty()) {
            return sheetVersions.getLast();
        }
        else {
            throw new NoSuchElementException("There is no sheet to display.");
        }
    }

//    @Override
//    public Sheet displaySheet(Sheet sheet) {
//        int index = sheetVersions.indexOf(sheet);
//        return List<>
//    }

    @Override
    public Cell getCellFromSheet(Sheet sheet, int row, int column) {
        return sheet.getCell(row, column); //is it the expected behavior?
    }

    @Override
    public Sheet updateValueOfCellAndDisplayNewSheet(Sheet sheet, int row, int col, String value) {
        try{
            return sheet.updateCellValueAndCalculate(row, col, value);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Sheet getSheetOfSpecificVersion(int version) {
        try {
            if (!sheetVersions.isEmpty()) {
                return sheetVersions.get(version - 1);
            }
            else {
                throw new NoSuchElementException("There are no sheets in the system.");
            }
        }
        catch(IndexOutOfBoundsException e){
            throw new IndexOutOfBoundsException("The requested version is not available.");
        }
    }

    @Override
    public int getLatestVersionNumber() {
        return sheetVersions.size();
    }
}
