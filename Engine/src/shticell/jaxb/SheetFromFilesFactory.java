package shticell.jaxb;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import shticell.jaxb.schemaClasses.*;
import shticell.sheet.api.Sheet;
import shticell.sheet.impl.SheetImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class SheetFromFilesFactory {

    private final static String JAXB_XML_GAME_PACKAGE_NAME = "shticell/jaxb/schema";
    private static final int maxPossibleNumOfRows = 50;
    private static final int maxPossibleNumOfColumns = 20;

    public static Sheet CreateSheetObjectFromXmlFile(String fileName) throws Exception {
        File file = new File(fileName);
        STLSheet stlSheet;
        Sheet newSheet = new SheetImpl();
        //InputStream inputStreamFromFile = new FileInputStream(file);

        try (InputStream inputStreamFromFile = new FileInputStream(file)) {
            stlSheet = unMarshalFromFile(inputStreamFromFile);
            newSheet = convertStlSheetFromFileToSheetImpl(stlSheet);
        }
        catch (FileNotFoundException ex) {
            System.err.println("Missing file " + file.getAbsolutePath());
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

    private static Sheet convertStlSheetFromFileToSheetImpl(STLSheet stlSheet) {

        Sheet sheetImplFromFile;
        boolean cellAddedAsPartOfInitialization = true;
        int currentRowNum, currentColumnNum, newSheetVersion = 1;

        try {
            checkProvidedNumOfRowsAndColsSmallerThanPossibleMax(stlSheet.getSTLLayout().getRows(),stlSheet.getSTLLayout().getColumns());
            sheetImplFromFile = new SheetImpl(stlSheet.getName(), stlSheet.getSTLLayout().getRows(), stlSheet.getSTLLayout().getColumns(),
                    stlSheet.getSTLLayout().getSTLSize().getRowsHeightUnits(), stlSheet.getSTLLayout().getSTLSize().getColumnWidthUnits(),newSheetVersion);
            List<STLCell> allCellsFromFile = stlSheet.getSTLCells().getSTLCell();

            for (STLCell cellFromFile : allCellsFromFile) {
                currentRowNum = cellFromFile.getRow();
                currentColumnNum = getColumnLetterFromString(cellFromFile.getColumn());
                sheetImplFromFile = sheetImplFromFile.updateCellValueAndCalculate(currentRowNum, currentColumnNum, cellFromFile.getSTLOriginalValue(), cellAddedAsPartOfInitialization);
            }

            return sheetImplFromFile;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private static void checkProvidedNumOfRowsAndColsSmallerThanPossibleMax(int rows, int cols) {

        if (rows > maxPossibleNumOfRows || cols > maxPossibleNumOfColumns) {
            throw new IllegalArgumentException("Number of rows must be smaller than: " + maxPossibleNumOfRows +
                    " and Number of columns must be smaller than: " + maxPossibleNumOfColumns);
        }
    }

    public static int getColumnLetterFromString(String columnLetterString) {

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
