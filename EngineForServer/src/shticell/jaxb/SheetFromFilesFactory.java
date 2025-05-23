package shticell.jaxb;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import shticell.jaxb.schema.classes.STLBoundaries;
import shticell.jaxb.schema.classes.STLCell;
import shticell.jaxb.schema.classes.STLRange;
import shticell.jaxb.schema.classes.STLSheet;
import shticell.range.RangesManager;
import shticell.sheet.api.Sheet;
import shticell.sheet.impl.SheetImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class SheetFromFilesFactory {

    private static final int maxPossibleNumOfRows = 50;
    private static final int maxPossibleNumOfColumns = 20;

    public static Sheet CreateSheetObjectFromXmlFile(File file, String ownerUsername) throws Exception {
        STLSheet stlSheet;
        Sheet newSheet = new SheetImpl();

        try (InputStream inputStreamFromFile = new FileInputStream(file)) {
            stlSheet = unMarshalFromFile(inputStreamFromFile);
            newSheet = convertStlSheetFromFileToSheetImpl(stlSheet, ownerUsername);
        }
        catch (FileNotFoundException ex) {
            throw new FileNotFoundException("Missing file: " + file.getAbsolutePath());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return newSheet;
    }

    private static STLSheet unMarshalFromFile(InputStream inputStreamFromFile) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (STLSheet) unmarshaller.unmarshal(inputStreamFromFile);

    }

    private static Sheet convertStlSheetFromFileToSheetImpl(STLSheet stlSheet, String ownerUsername) {

        Sheet sheetImplFromFile;
        boolean cellAddedAsPartOfInitialization = true;
        int currentRowNum, currentColumnNum, newSheetVersion = 1;

        try {
            checkProvidedNumOfRowsAndColsSmallerThanPossibleMax(stlSheet.getSTLLayout().getRows(),stlSheet.getSTLLayout().getColumns());

            sheetImplFromFile = new SheetImpl(stlSheet.getName(), stlSheet.getSTLLayout().getRows(), stlSheet.getSTLLayout().getColumns(),
                    stlSheet.getSTLLayout().getSTLSize().getRowsHeightUnits(), stlSheet.getSTLLayout().getSTLSize().getColumnWidthUnits(),newSheetVersion);

            updateRangesInRangeFactoryFromStlSheet(stlSheet, sheetImplFromFile);

            List<STLCell> allCellsFromFile = stlSheet.getSTLCells().getSTLCell();

            for (STLCell cellFromFile : allCellsFromFile) {
                currentRowNum = cellFromFile.getRow();
                currentColumnNum = getColumnNumFromString(cellFromFile.getColumn());
                sheetImplFromFile = sheetImplFromFile.updateCellValueAndCalculate
                                    (currentRowNum, currentColumnNum, cellFromFile.getSTLOriginalValue(),
                                     cellAddedAsPartOfInitialization, ownerUsername);
            }

            return sheetImplFromFile;
        } catch (Exception e) {
            throw new RuntimeException("Error when trying to load recent file: " + e.getMessage());
        }

    }

    private static void updateRangesInRangeFactoryFromStlSheet(STLSheet stlSheet, Sheet actualBasicSheet) {
        try {
            List<STLRange> allStlRangesFromFile = stlSheet.getSTLRanges().getSTLRange();
            STLBoundaries currentStlBoundaries;
            String topLeftStartCoordinateString, bottomRightEndCoordinateString;
            RangesManager rangesManager = actualBasicSheet.getRangesManager();

            for (STLRange currentStlRangeFromFile : allStlRangesFromFile) {
                currentStlBoundaries = currentStlRangeFromFile.getSTLBoundaries();
                topLeftStartCoordinateString = currentStlBoundaries.getFrom();
                bottomRightEndCoordinateString = currentStlBoundaries.getTo();

                rangesManager.createRangeFromTwoCoordinateStringsAndNameString(actualBasicSheet,
                        currentStlRangeFromFile.getName(), topLeftStartCoordinateString, bottomRightEndCoordinateString);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void checkProvidedNumOfRowsAndColsSmallerThanPossibleMax(int providedNumOfRows, int providedNumOfColumns) {

        if (providedNumOfRows > maxPossibleNumOfRows) {
            throw new IllegalArgumentException("Number of rows provided in the file (" + providedNumOfRows +
                    ") is greater than maximum possible number of rows for a sheet (" + maxPossibleNumOfRows + ")");
        }
        if (providedNumOfColumns > maxPossibleNumOfColumns) {
            throw new IllegalArgumentException("Number of columns provided in the file (" + providedNumOfColumns +
                    ") is greater than maximum possible number of columns for a sheet (" + maxPossibleNumOfColumns + ")");
        }
        if (providedNumOfRows < 1) {
            throw new IllegalArgumentException("Number of rows provided in the file is less than 1 - not possible");
        }
        if (providedNumOfColumns < 1) {
            throw new IllegalArgumentException("Number of columns provided in the file is less than 1 - not possible");
        }
    }

    public static int getColumnNumFromString(String columnLetterString) {

        int columnNumFromChar;
        char letterChar = columnLetterString.toUpperCase().charAt(0);

        if (columnLetterString.length() != 1) {
            throw new IllegalArgumentException("Column must be 1 character, from 'A' to 'T'");
        }

        if (letterChar >= 'A' && letterChar <= 'T') {
            columnNumFromChar = letterChar - 'A' + 1;
        } else {
            throw new IllegalArgumentException
                    ("First character, representing a column number, must be a letter from A to T (for columns 1 to max 20)");
        }

        return columnNumFromChar;
    }
}