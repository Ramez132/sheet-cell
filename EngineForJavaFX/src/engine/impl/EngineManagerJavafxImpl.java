package engine.impl;

import engine.api.EngineManagerJavafx;
import shticell.cell.api.Cell;
import shticell.jaxb.SheetFromFilesFactory;
import shticell.range.Range;
import shticell.range.RangeFactory;
import shticell.sheet.api.Sheet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class EngineManagerJavafxImpl implements EngineManagerJavafx {

    private List<Sheet> sheetVersionsArray;

    public EngineManagerJavafxImpl() {
        this.sheetVersionsArray = new ArrayList<Sheet>();
    }

    @Override
    public Sheet getSheetFromFile(File file) throws Exception {
        Sheet currentSheet;

        try {
            currentSheet = SheetFromFilesFactory.CreateSheetObjectFromXmlFile(file);
            sheetVersionsArray.clear(); //if the code gets here, the file is valid and new Sheet object was created - clear all previous versions
            sheetVersionsArray.add(currentSheet);
        }
        catch (Exception e) {
            throw e;
        }

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

    @Override
    public Range addRangeToMostRecentSheet(String rangeName, String leftTopStartCoordinateStr, String rightBottomEndCoordinateStr) {
        return RangeFactory.createRangeFromTwoCoordinateStringsAndNameString(sheetVersionsArray.getLast(), rangeName, leftTopStartCoordinateStr, rightBottomEndCoordinateStr);
    }

    @Override
    public Range getRangeFromMostRecentSheet(String rangeName) {
        return RangeFactory.getRangeByItsName(rangeName);
    }

    @Override
    public boolean isSelectedRangeUsedInAnyCellWithRelevantFunction(String rangeName) {
        return sheetVersionsArray.getLast().isSelectedRangeIsUsedInSheet(rangeName);
    }

    @Override
    public void deleteRangeFromRangeFactory(String rangeName) {
        try {
            RangeFactory.deleteRangeFromRangesFactory(rangeName);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public List<String> getAllRangeNamesInTheSystem() {
        return RangeFactory.getAllRangesNames();
    }

    @Override
    public Range getRangeByItsName(String rangeName) { return RangeFactory.getRangeByItsName(rangeName); }

    @Override
    public boolean isThereAnyRangeInRangesFactory() {
        return RangeFactory.isThereAnyRangeInRangesFactory();
    }


}
