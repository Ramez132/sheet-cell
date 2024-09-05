package consoleUIManager;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

import menu.MainMenu;
import shticell.cell.api.Cell;
import shticell.cell.api.EffectiveValue;
import shticell.coordinate.Coordinate;
import shticell.coordinate.CoordinateFactory;
import shticell.manager.api.EngineManager;
import shticell.manager.impl.EngineManagerImpl;
import shticell.sheet.api.Sheet;

public class ConsoleUiManager {


    private EngineManager engineManager = new EngineManagerImpl();//important new!!!
    private final Scanner scanner = new Scanner(System.in);

    ConsoleUiManager(){}

    public void Run() {
        boolean isSystemStillRunning = true;
        do {
            try {
                MainMenu.PrintMenuAndAskForInput();
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1" -> getSheetFromXMLFile();
                    case "2" -> printMostRecentSheet();
                    case "3" -> printTheDataOfSpecificCell();
                    case "4" -> updateOriginalValueOfSpecificCellAndPrintTheNewSheet();
                    case "5" -> handlePrintingOfSheetFromSpecificVersion();
                    case "6" -> isSystemStillRunning = false;
                    default -> System.out.println("Invalid choice. Please write only the number of your choice (1-6), and then press enter.");
                }
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        } while (isSystemStillRunning);
        System.out.println("The program has ended.");
        System.out.println("Thank you for using the system.");
    }

    private void getSheetFromXMLFile(){
        System.out.println("Please write the full path of the XML file you wish to load the sheet from.");
        System.out.println("For example: C:\\Users\\user\\Desktop\\sheet.xml");
        String filePathStr = scanner.nextLine().trim();
        try {
            engineManager.getSheetFromFile(filePathStr);
            System.out.println("The sheet was loaded successfully.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println();
        System.out.println("Going back to the main menu...");
    }

    void printMostRecentSheet(){
        try {
            printSheetToConsole(engineManager.getMostRecentSheet());
        }
        catch (NoSuchElementException e) {
            System.out.println("Error while trying to print a sheet: " + e.getMessage());
        }

        System.out.println();
        System.out.println("Going back to the main menu...");
    }

    private void printSheetToConsole(Sheet sheet){
        printFirstLineOfSheetWithLettersRepresentingColumns(sheet);
        printAllRowsWithEffectiveValueOfCells(sheet);
    }

    private void printFirstLineOfSheetWithLettersRepresentingColumns(Sheet sheet) {
        String space = " ";
        int maxRowNumber = sheet.getNumOfRows();
        int rowNumberWidth = String.valueOf(maxRowNumber).length() + 2;
        System.out.println("Printing a sheet...");
        System.out.println("The name of the sheet is: " + sheet.getNameOfSheet());
        System.out.println("The version of the sheet is: " + sheet.getVersion());
        System.out.println();
        System.out.print(space.repeat(rowNumberWidth) + "|");

        for (int columnNum = 0; columnNum < sheet.getNumOfColumns(); columnNum++) {
            int widthOfColumn = sheet.getColumnWidth();
            widthOfColumn = widthOfColumn % 2 == 0 ? widthOfColumn + 1 : widthOfColumn;

            int leftPadding = widthOfColumn / 2;
            int rightPadding = widthOfColumn - leftPadding - 1;

            System.out.print(space.repeat(leftPadding));
            System.out.print((char) (columnNum + 'A'));
            System.out.print(space.repeat(rightPadding));
            System.out.print("|");
        }

        System.out.println();
    }

    private void printAllRowsWithEffectiveValueOfCells(Sheet sheet) {
        String space = " ";
        int rowHeight = sheet.getRowHeight();
        int numOfRows = sheet.getNumOfRows();
        int numOfColumns = sheet.getNumOfColumns();
        int rowNumberWidth = String.valueOf(numOfRows).length() + 2;
        int columnWidth = sheet.getColumnWidth() % 2 == 0 ? sheet.getColumnWidth() + 1 : sheet.getColumnWidth();

        for (int rowNum = 1; rowNum <= numOfRows; rowNum++) {
            for (int h = 0; h < rowHeight; h++) {  // Repeat for the rowNum height
                if (h == rowHeight / 2) {
                    System.out.print(String.format("%" + rowNumberWidth + "d", rowNum));
                } else {
                    System.out.print(space.repeat(rowNumberWidth));
                }
                System.out.print("|");
                for (int columnNum = 1; columnNum <= numOfColumns; columnNum++) {
                    Coordinate coordinate = CoordinateFactory.getCoordinate(rowNum, columnNum);
                    String effectiveValueOfCellAsString;
                    if (sheet.getActiveCells().containsKey(coordinate)) {
                        EffectiveValue effectiveValueOfCell = sheet.getActiveCells().get(coordinate).getCurrentEffectiveValue();
                        if (effectiveValueOfCell == null) {
                            effectiveValueOfCellAsString = addThousandsSeparator(" ");
                        } else {
                            effectiveValueOfCellAsString = addThousandsSeparator(effectiveValueOfCell.getValue().toString());
                        }
                    } else {
                        effectiveValueOfCellAsString = addThousandsSeparator(" ");
                    }

                    if (h == rowHeight / 2) {
                        System.out.print(effectiveValueOfCellAsString);
                        int count = columnWidth - effectiveValueOfCellAsString.length();
                        System.out.print(space.repeat(Math.abs(count)));
                    } else {
                        System.out.print(space.repeat(columnWidth));
                    }
                    System.out.print("|");
                }
                System.out.println();
            }
        }
    }

    private String addThousandsSeparator(String number) throws NumberFormatException {
        try {
            return NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(number));
        }
        catch (NumberFormatException e) {
            return number;
        }
    }

    private Coordinate selectCoordinate(){
        try {
            Sheet sheet = engineManager.getMostRecentSheet();
            System.out.println("Please write the details of the cell you want to select (for example 'C4' for cell in column 3, row 4), then press Enter.");
            String coordinateStr = scanner.nextLine().trim().toUpperCase();
            return CoordinateFactory.getCoordinateFromStr(coordinateStr, sheet);
        }
        catch (NoSuchElementException e) { //could come from line: sheet = engineManager.getMostRecentSheet();
            throw new IllegalArgumentException("There is no sheet to select a cell from. Please load a sheet first.");
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void printTheDataOfSpecificCell()
    {
        try {
            Coordinate selectedCoordinate = selectCoordinate();
            Sheet sheet = engineManager.getMostRecentSheet();

            //if the cell is not present in activeCells in the sheet, it will be created as a new empty cell
            Cell cell = engineManager.getCellFromMostRecentSheet(selectedCoordinate.getRow(), selectedCoordinate.getColumn());
            System.out.println("Printing the data of the selected cell:");
            System.out.println("The coordinate of the cell is: " + selectedCoordinate.toString());

            boolean isCellEmpty = cell.getIsCellEmptyBoolean();
            int lastVersionInWhichCellHasChanged = cell.getLastVersionInWhichCellHasChanged();

            if (isCellEmpty) {
                System.out.println("The cell doesn't have an original value - it is empty.");
                System.out.println("The cell has no effective value - it is empty.");

                if (lastVersionInWhichCellHasChanged == sheet.getVersionNumForEmptyCellWithoutPreviousValues()) { //empty cell without previous values
                    System.out.println("Cannot print the last version in which the cell was updated - the cell is empty and didn't have a value before.");
                } else { //cell which is empty now but had a value before
                    System.out.println("The last version in which the cell was updated is: " + lastVersionInWhichCellHasChanged);
                }
            }
            else { //the cell is not empty
                System.out.println("The original value of the cell is: " + cell.getOriginalValueStr());
                String effectiveValueOfCellAsString = addThousandsSeparator(
                        sheet.getActiveCells().get(selectedCoordinate).
                                getCurrentEffectiveValue().getValue().toString());
                System.out.println("The effective value of the cell is: " + effectiveValueOfCellAsString);
                System.out.println("The last version in which the cell was updated is: " + lastVersionInWhichCellHasChanged);
            }

            if (cell.getDependsOnMap().isEmpty()) {
                System.out.println("The selected cell doesn't depend on any other cell.");
            }
            else {
                System.out.println("The selected cell directly depends on the cells:");
                for (Coordinate coordinateTheCellDependsOn : cell.getDependsOnMap().keySet()) {
                    System.out.println(coordinateTheCellDependsOn.toString());
                }
                System.out.println();
            }

            if (cell.getInfluencingOnMap().isEmpty()) {
                System.out.println("The selected cell doesn't influence any other cell.");
            }
            else {
                System.out.println("The selected cell is influencing directly on the cells:");
                for (Coordinate coordinateTheCellInfluencingOn : cell.getInfluencingOnMap().keySet()) {
                    System.out.println(coordinateTheCellInfluencingOn.toString());
                }
                System.out.println();
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        System.out.println();
        System.out.println("Going back to the main menu...");
    }

    private void updateOriginalValueOfSpecificCellAndPrintTheNewSheet(){
        try {
            Coordinate selectedCoordinate = selectCoordinate();
            System.out.println("Please write the new value for the cell, then press Enter.");
            System.out.println("Pressing just enter will set the cell to be empty.");
            String dataToEnterTheCell = scanner.nextLine().trim();
            Sheet possibleNewSheet = engineManager.updateValueOfCellAndGetNewSheet(selectedCoordinate.getRow(), selectedCoordinate.getColumn(), dataToEnterTheCell);
            printSheetToConsole(possibleNewSheet);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        System.out.println();
        System.out.println("Going back to the main menu...");
    }

    private void handlePrintingOfSheetFromSpecificVersion(){
        try {
            if (!engineManager.isThereASheetLoadedToTheSystem()) {
                throw new NoSuchElementException("There is no sheet loaded to the system, so there are no versions to display.");
            }
            System.out.println("These are all the versions of the sheet, and the number of cells that their effective value has changed in this version:");
            System.out.println();
            for (int i = 1; i <= engineManager.getLatestVersionNumber(); i++) {
                Sheet sheetOfSpecificVersion = engineManager.getSheetOfSpecificVersion(i);
                System.out.println("Version " + i + " : There are "
                        + sheetOfSpecificVersion.getNumOfCellsWhichEffectiveValueChangedInNewVersion() + " cells that have changed their effective value.");
            }
            System.out.println();

            System.out.println("Please write the version number of the sheet you want to print, then press Enter.");
            System.out.println("The version number should be a number between 1 and " + engineManager.getLatestVersionNumber() + ".");
            String versionNumber = scanner.nextLine().trim();

            try {
                int version = Integer.parseInt(versionNumber);
                if (version < 1 || version > engineManager.getLatestVersionNumber()) {
                    throw new IllegalArgumentException("The version number provided is not in the correct range.");
                } else {
                    Sheet selectedSheet = engineManager.getSheetOfSpecificVersion(version);
                    printSheetToConsole(selectedSheet);
                }
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("The system expected a number.");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        System.out.println();
        System.out.println("Going back to the main menu...");
    }
}

