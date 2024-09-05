package shticell.manager.impl;

import shticell.cell.api.Cell;
import shticell.jaxb.SheetFromFilesFactory;
import shticell.manager.api.EngineManager;
import shticell.sheet.api.Sheet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class EngineManagerImpl implements EngineManager {
    private List<Sheet> sheetVersionsArray;
//    private Sheet currentSheet;

    public EngineManagerImpl() {
        this.sheetVersionsArray = new ArrayList<Sheet>();
    }

    @Override
    public Sheet getSheetFromFile(String fileName) throws Exception {
        File file = new File(fileName);
        Sheet currentSheet;

        if (!file.exists()) {
            throw new IllegalArgumentException("There is no file in the given path.");
        }
        if (!fileName.endsWith(".xml")) {
            throw new IllegalArgumentException("The file path must be in XML format.");
        }
        try {
            currentSheet = SheetFromFilesFactory.CreateSheetObjectFromXmlFile(file);
            sheetVersionsArray.clear(); //if the code gets here, the file is valid and new Sheet object was created - clear all previous versions
            sheetVersionsArray.add(currentSheet);
        }
        catch (Exception e) {
            throw e;
        }
        // try to open the file - > fails? throw an exception
        // make the file go through auto-generated classes
        // check parameters - i.e. all cells in sheet range - > fails? throw an exception
        // if all is good - clear sheetVersionsArray and add the new sheet to the array

        return sheetVersionsArray.getLast();
    }

    @Override
    public Sheet getMostRecentSheet() throws NoSuchElementException {
        if (!sheetVersionsArray.isEmpty()) {
            return sheetVersionsArray.getLast();
        }
        else {
            throw new NoSuchElementException("There is no sheet in the system.");
        }
    }

//    @Override
//    public Sheet displaySheet(Sheet sheet) {
//        int index = sheetVersionsArray.indexOf(sheet);
//        return List<>
//    }

    @Override
    public Cell getCellFromMostRecentSheet(int row, int column) {
        return sheetVersionsArray.getLast().getCell(row, column); //is it the expected behavior?
    }

    @Override
    public Sheet updateValueOfCellAndGetNewSheet(int row, int col, String value) throws RuntimeException {
        Sheet possibleNewSheet;
        boolean isUpdatePartOfSheetInitialization = sheetVersionsArray.isEmpty();
        try {
            possibleNewSheet = sheetVersionsArray.getLast().updateCellValueAndCalculate(row, col, value, isUpdatePartOfSheetInitialization);
            if (possibleNewSheet != sheetVersionsArray.getLast()) { //the sheet has changed
             sheetVersionsArray.add(possibleNewSheet); //add the new sheet to the list
            }

            return sheetVersionsArray.getLast();
        }
        catch (Exception e) { //should we catch a more specific Exception? could there be a few types?
            throw new RuntimeException(e.getMessage());
        }

    }



    /**
     * @param version should be a number of requested version, starting from 1 (not 0)
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
            throw new NoSuchElementException("There is no sheet loaded to the system.");
        }
    }

    @Override
    public boolean isThereASheetLoadedToTheSystem() {  //returns true if there is a sheet loaded to the system
        if (sheetVersionsArray.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
}
