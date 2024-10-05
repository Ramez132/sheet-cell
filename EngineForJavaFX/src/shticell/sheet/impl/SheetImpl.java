package shticell.sheet.impl;

import shticell.cell.api.EffectiveValue;
import shticell.cell.impl.CellImpl;
import shticell.expression.api.Expression;
import shticell.expression.parser.FunctionParser;
import shticell.range.Range;
import shticell.sheet.api.Sheet;
import shticell.cell.api.Cell;
import shticell.coordinate.Coordinate;
import shticell.coordinate.CoordinateFactory;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;

public class SheetImpl implements Sheet, Serializable {

    private Map<Coordinate, Cell> activeCells = new HashMap<>();
    private Map<String, Range> allRangesReferencedInSheet = new HashMap<>();
    private Map<String, Integer> countersOfReferencesToRange = new HashMap<>();
    private String nameOfSheet;
    private int numOfRows;
    private int numOfColumns;
    private int rowHeight;
    private int columnWidth;
    private int thisSheetVersion = 1; //need to update?
    private int numOfCellsWhichEffectiveValueChangedInNewVersion = 0;
    private static final int versionNumForEmptyCellWithoutPreviousValues = -1 ;//-1 to indicate that the cell has never been set before


    public SheetImpl(String nameOfSheet, int numOfRows, int numOfColumns, int rowHeight, int columnWidth, int thisSheetVersion) {
        this();
        this.nameOfSheet = nameOfSheet;
        this.numOfRows = numOfRows;
        this.numOfColumns = numOfColumns;
        this.rowHeight = rowHeight;
        this.columnWidth = columnWidth;
        this.thisSheetVersion = thisSheetVersion;
    }

    public SheetImpl() {
        this.activeCells = new HashMap<>();
    }

    @Override
    public String getNameOfSheet() {
        return nameOfSheet;
    }

    @Override
    public int getVersion() {
        return thisSheetVersion;
    }

    @Override
    public int getNumOfRows() {
        return numOfRows;
    }

    @Override
    public int getNumOfColumns() {
        return numOfColumns;
    }

    @Override
    public int getColumnWidth() {
        return columnWidth;
    }

    @Override
    public int getRowHeight() {
        return rowHeight;
    }

    @Override
    public void addRangeToAllRangesReferencedInSheet(String rangeName, Range range) {
        allRangesReferencedInSheet.put(rangeName, range);
    }

    @Override
    public boolean isSelectedRangeIsUsedInSheet(String rangeName) {
//        return allRangesReferencedInSheet.containsKey(rangeName);
        if (countersOfReferencesToRange.containsKey(rangeName)) {
            return countersOfReferencesToRange.get(rangeName) > 0;
        } else {
            return false;
        }
    }

    @Override
    public List<String> getUniqueValuesForFilteringInSelectedColumnAndRelevantArea
            (char charLetterOfColumnToGetUniqueValuesToFilter,
             String newFilterStartCoordinateStr,
             String newFilterEndCoordinateStr) {
        int columnNumFromChar = CoordinateFactory.getColumnNumberFromChar(charLetterOfColumnToGetUniqueValuesToFilter, this);
        Coordinate topLeftStartCoordinate = CoordinateFactory.getCoordinateFromStr(newFilterStartCoordinateStr, this);
        Coordinate bottomRightEndCoordinate = CoordinateFactory.getCoordinateFromStr(newFilterEndCoordinateStr, this);
        List<String> uniqueValuesInSelectedColumnAndRelevantArea = new ArrayList<>();
        int startRowOfFilteringArea = topLeftStartCoordinate.getRow();
        int endRowOfFilteringArea = bottomRightEndCoordinate.getRow();

        for (int currentRow = startRowOfFilteringArea; currentRow <= endRowOfFilteringArea; currentRow++) {
            Cell currentCell = getCell(currentRow, columnNumFromChar);

            if (!currentCell.getIsCellEmptyBoolean()) {
//                EffectiveValue effectiveValueOfCell = currentCell.getCurrentEffectiveValue();
//                String effectiveValueOfCellAsString = "";
//                if (effectiveValueOfCell != null) {
//                    effectiveValueOfCellAsString = effectiveValueOfCell.getValue().toString();
//                }

                Coordinate coordinate = CoordinateFactory.getCoordinate(currentRow, columnNumFromChar);
                String effectiveValueOfCellAsString;
                if (activeCells.containsKey(coordinate)) {
                    EffectiveValue effectiveValueOfCell = activeCells.get(coordinate).getCurrentEffectiveValue();
                    if (effectiveValueOfCell == null) {
                        effectiveValueOfCellAsString = addThousandsSeparator("");
                    } else {
                        effectiveValueOfCellAsString = addThousandsSeparator(effectiveValueOfCell.getValue().toString());
                    }
                } else {
                    effectiveValueOfCellAsString = addThousandsSeparator("");
                }

                if (!uniqueValuesInSelectedColumnAndRelevantArea.contains(effectiveValueOfCellAsString)
                        && !effectiveValueOfCellAsString.isEmpty()) {
                    uniqueValuesInSelectedColumnAndRelevantArea.add(effectiveValueOfCellAsString);
                }
            }
        }


//        String effectiveValueOfCellAsString;
//        if (sheet.getActiveCells().containsKey(coordinate)) {
//            EffectiveValue effectiveValueOfCell = sheet.getActiveCells().get(coordinate).getCurrentEffectiveValue();
//            if (effectiveValueOfCell == null) {
//                effectiveValueOfCellAsString = addThousandsSeparator(" ");
//            } else {
//                effectiveValueOfCellAsString = addThousandsSeparator(effectiveValueOfCell.getValue().toString());
//            }
//        } else {
//            effectiveValueOfCellAsString = addThousandsSeparator(" ");
//        }

        return uniqueValuesInSelectedColumnAndRelevantArea;
    }

    private String addThousandsSeparator(String number) throws NumberFormatException {
        try {
            return NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(number));
        }
        catch (NumberFormatException e) {
            return number;
        }
    }

    @Override
    public void increaseCounterOfReferencesToSelectedRange(String rangeName) {
        if (countersOfReferencesToRange.containsKey(rangeName)) {
            countersOfReferencesToRange.put(rangeName, countersOfReferencesToRange.get(rangeName) + 1);
        } else {
            countersOfReferencesToRange.put(rangeName, 1);
        }
    }

    @Override
    public Cell getCell(int row, int column) throws IllegalArgumentException {

        try {
            boolean isCoordinateValid = isCoordinateInSheetRange(row, column);

            if (isCoordinateValid) {
                Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);
                if (activeCells.containsKey(coordinate)) {
                    return activeCells.get(coordinate);
                } else {
                    return setNewEmptyCell(row, column);
                }
            }
        } catch (Exception e) {
            // deal with the runtime error that was discovered as part of invocation
            throw  e;
        }

        return null;
    }

    @Override
    public boolean isCoordinateInSheetRange(int rowNum, int columnNum) throws IllegalArgumentException {

        if (rowNum > numOfRows) {
            throw new IllegalArgumentException("Row number provided (" + rowNum + ") is greater than the maximum row number of the sheet (" + numOfRows + ")");
        }
        if (columnNum > numOfColumns) {
            throw new IllegalArgumentException("Column character provided (to represent column number " + columnNum + ") is greater than the maximum column number of the sheet (" + numOfColumns + ")");
        }
        if (rowNum < 1) {
            throw new IllegalArgumentException("Row number provided is less than 1 - not possible");
        }
        if (columnNum < 1) {
            throw new IllegalArgumentException("Column character provided (to represent column number " + columnNum + ") is less than 1 - not possible");
        }
        return true;
    }

    /**
     * @return true if the cell is empty, false otherwise
     */
    @Override
    public boolean isCellEmpty(int row, int column) {
        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);
        boolean isCellEmptyBoolean;
        if (activeCells.containsKey(coordinate)) {
            isCellEmptyBoolean = activeCells.get(coordinate).getIsCellEmptyBoolean();
        } else {
            isCellEmptyBoolean = true;
        }

        return isCellEmptyBoolean;

        // return (activeCells.containsKey(coordinate) && activeCells.get(coordinate).getIsCellEmptyBoolean());


//        Cell cellThatMightBeEmpty = activeCells.get(coordinate);
//        isCellEmptyBoolean = cellThatMightBeEmpty == null || cellThatMightBeEmpty.getIsCellEmptyBoolean();
//        return Optional.ofNullable(activeCells.get(coordinate)).isEmpty();
    }

    @Override
    public Cell setNewEmptyCell(int row, int column) {
        Cell emptyCell = new CellImpl(row, column, "", versionNumForEmptyCellWithoutPreviousValues, this);
        activeCells.put(CoordinateFactory.createCoordinate(row, column), emptyCell);

        return emptyCell;
    }

    @Override
    public boolean isCellsCollectionContainsCoordinate(int row, int column) {
        return activeCells.containsKey(CoordinateFactory.createCoordinate(row, column));
    }

    @Override
    public Map<Coordinate, Cell> getActiveCells() {
        return activeCells;
    }

    @Override
    public int getVersionNumForEmptyCellWithoutPreviousValues() {
        return versionNumForEmptyCellWithoutPreviousValues;
    }


    @Override
    public Sheet updateCellValueAndCalculate
                        (int row, int column, String value,
                         boolean isUpdatePartOfSheetInitialization)
                         throws IllegalArgumentException {

        try {
            if (isCoordinateInSheetRange(row, column)) {

                Coordinate coordinateOfNewCellOrCellToBeUpdated = CoordinateFactory.createCoordinate(row, column);
                Cell cellBeforeUpdate = activeCells.get(coordinateOfNewCellOrCellToBeUpdated);
                int sheetVersionNumForNewCell, lastVersionInWhichCellBeforeUpdateHasChanged = -1; //is -1 the right value? indicate an empty cell?

                Map<Coordinate,Cell> influencingOnMapOfCellThatMightBeUpdated = new HashMap<>();
                Map<Coordinate,Cell> dependsOnMapOfCellThatMightBeUpdated = new HashMap<>();
                Map<String,Range> rangesReferencedInCellBeforeUpdate = new HashMap<>();
                if (cellBeforeUpdate != null) { //this coordinate already have a cell
                    //get the cells that already depends on the cell\coordinate that is updating - to update their dependsOnMap to reference the updated cell
                    influencingOnMapOfCellThatMightBeUpdated = cellBeforeUpdate.getInfluencingOnMap();
                    dependsOnMapOfCellThatMightBeUpdated = cellBeforeUpdate.getDependsOnMap();
                    rangesReferencedInCellBeforeUpdate = cellBeforeUpdate.getRangesReferencedInCell();
//                    cellBeforeUpdate.calculateNewEffectiveValueAndDetermineIfItChanged();  //needed or will cause a problem?
                    lastVersionInWhichCellBeforeUpdateHasChanged = cellBeforeUpdate.getLastVersionInWhichCellHasChanged();
                }

                SheetImpl newSheetVersion = copySheet();
                if (isUpdatePartOfSheetInitialization) {
                    newSheetVersion.thisSheetVersion = sheetVersionNumForNewCell = 1;;
                }
                else {
                    newSheetVersion.thisSheetVersion = sheetVersionNumForNewCell = this.thisSheetVersion + 1;
                }

                Cell newCell = new CellImpl(row, column, value, sheetVersionNumForNewCell, newSheetVersion);
                if (!isUpdatePartOfSheetInitialization && cellBeforeUpdate != null) {
                    newCell.setPreviousEffectiveValue(cellBeforeUpdate.getCurrentEffectiveValue());
                }
                if (newSheetVersion.activeCells.containsKey(coordinateOfNewCellOrCellToBeUpdated)) { //the cell existed before
//                    Map<Coordinate, Cell> influencingOnMapOfCopiedCell = newSheetVersion.activeCells.get(coordinateOfNewCellOrCellToBeUpdated).getInfluencingOnMap();
                    Map<Coordinate, Cell> influencingOnMapOfCopiedCell = new HashMap<>(influencingOnMapOfCellThatMightBeUpdated);
                    newCell.insertInfluencingOnMapFromCellBeforeUpdate(influencingOnMapOfCopiedCell);
                }
                newSheetVersion.activeCells.put(coordinateOfNewCellOrCellToBeUpdated, newCell);

                for (String rangeNameReferencedInCellBeforeUpdate : rangesReferencedInCellBeforeUpdate.keySet()) {
                    if (newCell.getRangesReferencedInCell().containsKey(rangeNameReferencedInCellBeforeUpdate)) {
                        continue; //new cell also references this range
                    } else {
                        //new cell does not reference this range
                        //remove the current range from rangesReferencedInCell of the cell before the update
                        newCell.removeRangeFromRangesReferencedInCell(rangeNameReferencedInCellBeforeUpdate);
                        this.countersOfReferencesToRange.put(rangeNameReferencedInCellBeforeUpdate, this.countersOfReferencesToRange.get(rangeNameReferencedInCellBeforeUpdate) - 1);
//                        int numOfReferencesToSelectedRange = cellBeforeUpdate.getCounterOfReferencesToSelectedRange(rangeNameReferencedInCellBeforeUpdate);
//                        int newNumOfReferencesToSelectedRange = this.countersOfReferencesToRange.get(rangeNameReferencedInCellBeforeUpdate) - numOfReferencesToSelectedRange;
//                        this.countersOfReferencesToRange.put(rangeNameReferencedInCellBeforeUpdate, numOfReferencesToSelectedRange - 1);
                    }
                }

                for (Coordinate coordinateThatThisCellDependedOnBeforeTheUpdate : dependsOnMapOfCellThatMightBeUpdated.keySet()) {
                    //checking if the new cell already depends on this coordinate, with the new original value
                    if (newCell.getDependsOnMap().containsKey(coordinateThatThisCellDependedOnBeforeTheUpdate)) {
                        continue; //new cell already depends on this cell
                    } else {
                        //new cell does not depend on this coordinate
                        // remove the current coordinate/cell from influencingOnMap of the cell pointed before the update
                        Cell cellThatNewCellDependedOnBeforeTheUpdate = newSheetVersion.activeCells.get(coordinateThatThisCellDependedOnBeforeTheUpdate);
                        cellThatNewCellDependedOnBeforeTheUpdate.removeSelectedCoordinateFromInfluencingOnMap(coordinateOfNewCellOrCellToBeUpdated);
                    }
                }

                //if the cell updated - needs to know who he dependsOn his coordinate - update with influencingOnMap from the cell before the update
                //newCell.insertInfluencingOnMapFromCellBeforeUpdate(influencingOnMapOfCellThatMightBeUpdated); //if deep copied - it's still needed?

                for (Coordinate coordinateOfCellThatAlreadyDependsOnThisCoordinate : newCell.getInfluencingOnMap().keySet()) {
                    Cell cellInNewVersionThatAlreadyDependsOnThisCoordinate = newSheetVersion.activeCells.get(coordinateOfCellThatAlreadyDependsOnThisCoordinate);
                    cellInNewVersionThatAlreadyDependsOnThisCoordinate.getDependsOnMap().put(coordinateOfNewCellOrCellToBeUpdated, newCell);

                    newCell.getInfluencingOnMap().put(coordinateOfCellThatAlreadyDependsOnThisCoordinate, cellInNewVersionThatAlreadyDependsOnThisCoordinate);
                }

                Map<Cell, List<Cell>> adjacencyList = newSheetVersion.buildGraphAdjacencyList();
                if (newSheetVersion.hasCycle(adjacencyList)) {
                    throw new IllegalArgumentException("Could not update the value of cell: " + coordinateOfNewCellOrCellToBeUpdated +
                            ". The requested update would cause the sheet to have a circular dependencies, and that is not allowed in this system."); //this is what we want to do?
                }

                try {
                    Expression expression = FunctionParser.parseExpression(newCell.getOriginalValueStr(), newSheetVersion);
                } catch (IllegalArgumentException e) { //in case a function is not recognized or number of arguments is incorrect
                    if (isUpdatePartOfSheetInitialization)
                        throw new IllegalArgumentException("There is an error in cell " + newCell.getCoordinate().toString()
                                + " - " + e.getMessage());
                    else
                        throw new IllegalArgumentException("The update could not be done - there is an error in cell " + newCell.getCoordinate().toString()
                                + " - " + e.getMessage());
                }

                int numOfCellsWhichEffectiveValueChangedInNewVersion = 0;
                boolean effectiveValueChanged;
                for (Cell currentCellToCalcEffectiveValue : newSheetVersion.activeCells.values())
                {
                    effectiveValueChanged = currentCellToCalcEffectiveValue.calculateNewEffectiveValueAndDetermineIfItChanged();
                    EffectiveValue currentEffectiveValue = currentCellToCalcEffectiveValue.getCurrentEffectiveValue();
                    EffectiveValue previousEffectiveValue = currentCellToCalcEffectiveValue.getPreviousEffectiveValue();
                    if (effectiveValueChanged && !currentEffectiveValue.equals(previousEffectiveValue)) {
                        numOfCellsWhichEffectiveValueChangedInNewVersion++;
                        currentCellToCalcEffectiveValue.setLastVersionInWhichCellHasChanged(newSheetVersion.thisSheetVersion);
                    }
                }

                if (isUpdatePartOfSheetInitialization) {
                    newSheetVersion.numOfCellsWhichEffectiveValueChangedInNewVersion = newSheetVersion.activeCells.size();
                } else {
                    newSheetVersion.numOfCellsWhichEffectiveValueChangedInNewVersion = numOfCellsWhichEffectiveValueChangedInNewVersion;
                }

                return newSheetVersion;
            }
        } catch (IllegalArgumentException e) {
            // deal with the runtime error that was discovered as part of invocation
            throw e;
        }
        return null;
    }

    @Override
    public int getNumOfCellsWhichEffectiveValueChangedInNewVersion() {
        return numOfCellsWhichEffectiveValueChangedInNewVersion;
    }

    private Map<Cell, List<Cell>> buildGraphAdjacencyList() {

        Map<Cell, List<Cell>> adjacencyList = new HashMap<>();

        for (Cell cell : activeCells.values()) {
            int row = cell.getCoordinate().getRow();
            int column = cell.getCoordinate().getColumn();
            if (!isCellEmpty( row, column)) {

                List<Cell> dependsOnList = new ArrayList<>(cell.getDependsOnMap().values());;
                List<Cell> dependsOnListWithoutEmptyCells = new ArrayList<>();

                for (Cell neighbor : dependsOnList) {
                    if (!neighbor.getIsCellEmptyBoolean()) { //ignore empty cells for adjacency list - can not create a cycle
                        dependsOnListWithoutEmptyCells.add(neighbor);
                    }
                }
                adjacencyList.put(cell, dependsOnListWithoutEmptyCells);
            }
        }

        return adjacencyList;
    }

    public boolean hasCycle(Map<Cell, List<Cell>> adjacencyList) {
        Map<Cell, Integer> inDegree = new HashMap<>();
        Queue<Cell> queue = new LinkedList<>();

        // Initialize in-degree of all cells
        for (Cell cell : adjacencyList.keySet()) {
            inDegree.put(cell, 0);
        }

        // Calculate in-degree of each cell
        for (List<Cell> neighbors : adjacencyList.values()) {
            for (Cell neighbor : neighbors) {
                inDegree.put(neighbor, inDegree.get(neighbor) + 1);
            }
        }

        // Add cells with in-degree 0 to the queue
        for (Map.Entry<Cell, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        int processedNodes = 0;

        // Process nodes with in-degree 0
        while (!queue.isEmpty()) {
            Cell cell = queue.poll();
            processedNodes++;

            for (Cell neighbor : adjacencyList.get(cell)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        // If all nodes are processed, there is no cycle
        return processedNodes != adjacencyList.size();
    }

    private SheetImpl copySheet() {
        // lots of options here:
        // 1. implement clone all the way (yac... !)
        // 2. implement copy constructor for CellImpl and SheetImpl

        // 3. how about serialization ?
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            return (SheetImpl) ois.readObject();
        } catch (Exception e) {
            // deal with the runtime error that was discovered as part of invocation
            return this;
        }
    }


    @Override
    public SheetImpl createCopyOfSheet() {
        return copySheet();
    }
}