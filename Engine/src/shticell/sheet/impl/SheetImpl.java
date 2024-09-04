package shticell.sheet.impl;

import shticell.cell.impl.CellImpl;
import shticell.expression.api.Expression;
import shticell.expression.parser.FunctionParser;
import shticell.sheet.api.Sheet;
import shticell.cell.api.Cell;
import shticell.coordinate.Coordinate;
import shticell.coordinate.CoordinateFactory;

import java.io.*;
import java.util.*;

public class SheetImpl implements Sheet, Serializable {

    private Map<Coordinate, Cell> activeCells = new HashMap<>();
    private String nameOfSheet;
    private int numOfRows;
    private int numbOfColumns;
    private int rowHeight;
    private int columnWidth;
    private int thisSheetVersion = 1; //need to update?
    private int numOfCellsWhichEffectiveValueChangedInNewVersion = 0;
    private static final int versionNumForEmptyCellWithoutPreviousValues = -1 ;//-1 to indicate that the cell has never been set before


    public SheetImpl(String nameOfSheet, int numOfRows, int numbOfColumns, int rowHeight, int columnWidth, int thisSheetVersion) {
        this();
        this.nameOfSheet = nameOfSheet;
        this.numOfRows = numOfRows;
        this.numbOfColumns = numbOfColumns;
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
        return numbOfColumns;
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
    public Cell getCell(int row, int column) throws IllegalArgumentException {

        try {
            boolean isCoordinateValid = isCoordinateInSheetRange(row, column);

            if (isCoordinateValid) {
                return activeCells.get(CoordinateFactory.createCoordinate(row, column));
            }
        } catch (IllegalArgumentException e) {
            // deal with the runtime error that was discovered as part of invocation
            throw  e;
        }

        return null;
    }

    @Override
    public boolean isCoordinateInSheetRange(int row, int column) throws IllegalArgumentException {

        if (row > numOfRows) {
            throw new IllegalArgumentException("Row number provided is greater than the maximum row number of the sheet");
        }
        if (column > numbOfColumns) {
            throw new IllegalArgumentException("Column number provided is greater than the maximum column number of the sheet");
        }
        if (row < 1) {
            throw new IllegalArgumentException("Row number provided is less than 1 - not possible");
        }
        if (column < 1) {
            throw new IllegalArgumentException("Column number provided is less than 1 - not possible");
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
    public Sheet updateCellValueAndCalculate
                        (int row, int column, String value,
                         boolean isUpdatePartOfSheetInitialization)
                         throws IllegalArgumentException {

        try {
            if (isCoordinateInSheetRange(row, column)) {

                Coordinate coordinateOfNewCellOrCellToBeUpdated = CoordinateFactory.createCoordinate(row, column);
                Cell cellBeforeUpdate = activeCells.get(coordinateOfNewCellOrCellToBeUpdated);

                Map<Coordinate,Cell> influencingOnMapOfCellThatMightBeUpdated = new HashMap<>();
                if (cellBeforeUpdate != null) { //this coordinate already have a cell
                    //get the cells that already depends on the cell\coordinate that is updating - to update their dependsOnMap to reference the updated cell
                    influencingOnMapOfCellThatMightBeUpdated = activeCells.get(coordinateOfNewCellOrCellToBeUpdated).getInfluencingOnMap();
                    cellBeforeUpdate.calculateNewEffectiveValueAndDetermineIfItChanged();
                }

                SheetImpl newSheetVersion = copySheet();
                int sheetVersionNumForNewCell;
                if (isUpdatePartOfSheetInitialization) {
                    sheetVersionNumForNewCell = 1;
                } else {
                    sheetVersionNumForNewCell = newSheetVersion.getVersion() + 1;
                }

                Cell newCell = new CellImpl(row, column, value, sheetVersionNumForNewCell, newSheetVersion);
                if (newSheetVersion.activeCells.containsKey(coordinateOfNewCellOrCellToBeUpdated)) { //the cell existed before
                    Map<Coordinate, Cell> influencingOnMapOfCopiedCell = newSheetVersion.activeCells.get(coordinateOfNewCellOrCellToBeUpdated).getInfluencingOnMap();
                    newCell.insertInfluencingOnMapFromCellBeforeUpdate(influencingOnMapOfCopiedCell);
                }
                newSheetVersion.activeCells.put(coordinateOfNewCellOrCellToBeUpdated, newCell);

                //if the cell updated - needs to know who he dependsOn his coordinate - update with influencingOnMap from the cell before the update
                //newCell.insertInfluencingOnMapFromCellBeforeUpdate(influencingOnMapOfCellThatMightBeUpdated); //if deep copied - it's still needed?

                for (Coordinate coordinateOfCellThatAlreadyDependsOnThisCoordinate : newCell.getInfluencingOnMap().keySet()) {
                    Cell cellInNewVersionThatAlreadyDependsOnThisCoordinate = newSheetVersion.activeCells.get(coordinateOfCellThatAlreadyDependsOnThisCoordinate);
                    cellInNewVersionThatAlreadyDependsOnThisCoordinate.getDependsOnMap().put(coordinateOfNewCellOrCellToBeUpdated, newCell);

                    newCell.getInfluencingOnMap().put(coordinateOfCellThatAlreadyDependsOnThisCoordinate, cellInNewVersionThatAlreadyDependsOnThisCoordinate);
                }

//                //updating dependsOnMap of cells that reference this coordinate - to reference the new cell object
//                for (Cell cellThatAlreadyDependsOnThisCoordinate : influencingOnMapOfCellThatMightBeUpdated.values()) {
//                    cellThatAlreadyDependsOnThisCoordinate.getDependsOnMap().put(coordinateOfNewCellOrCellToBeUpdated, newCell);
//                }
//                //what happens if the update fails? reverts this change - so the cells will reference the cell object from before the update

//                Map<Coordinate,Cell> influencingOnMapOfNewCell = newCell.getInfluencingOnMap();

//                for (Cell influencedCell : influencingOnMapOfNewCell.values()) { //update dependsOnMap of to reference the newCell
//                   Map<Coordinate, Cell> dependsOnMapOfInfluencedCell = influencedCell.getDependsOnMap();
//                    dependsOnMapOfInfluencedCell.put(newCell.getCoordinate(), newCell);
//                }
//
//                for (Map.Entry<Coordinate, Cell> entry : influencingOnMapOfNewCell.entrySet()) {
//                    Coordinate influencingOnCoordinate = entry.getKey();
//                    Cell influencingOnCell = entry.getValue();
//                    if (newSheetVersion.isCellsCollectionContainsCoordinate(influencingOnCoordinate.getRow(), influencingOnCoordinate.getColumn())) {
//                        newSheetVersion.activeCells.put(influencingOnCoordinate, influencingOnCell);
//                    }
//                }

                Map<Cell, List<Cell>> adjacencyList = newSheetVersion.buildGraphAdjacencyList();
                if (newSheetVersion.hasCycle(adjacencyList)) {
//                    //revert the change from before - so the cells depends on this coordinate will reference the cell object from before the update
//                    for (Cell cellThatAlreadyDependsOnThisCoordinate : influencingOnMapOfCellThatMightBeUpdated.values()) {
//                        cellThatAlreadyDependsOnThisCoordinate.getDependsOnMap().put(coordinateOfNewCellOrCellToBeUpdated, cellBeforeUpdate);
//                    }
                    throw new IllegalArgumentException("The sheet has circular dependencies."); //this is what we want to do?
                }

                try {
                    Expression expression = FunctionParser.parseExpression(newCell.getOriginalValueStr());
                } catch (IllegalArgumentException e) { //in case a function is not recognized or number of arguments is incorrect
//                    //revert the change from before - so the cells depends on this coordinate will reference the cell object from before the update
//                    for (Cell cellThatAlreadyDependsOnThisCoordinate : influencingOnMapOfCellThatMightBeUpdated.values()) {
//                        cellThatAlreadyDependsOnThisCoordinate.getDependsOnMap().put(coordinateOfNewCellOrCellToBeUpdated, cellBeforeUpdate);
//                    }
                    if (isUpdatePartOfSheetInitialization)
                        throw new IllegalArgumentException("The file could not be loaded - there is an error in cell " + newCell.getCoordinate().toString()
                                + ": " + e.getMessage());
                    else
                        return this;
                }

                boolean effectiveValueChanged;
                for (Cell currentCellToCalcEffectiveValue : newSheetVersion.activeCells.values())
                {
                    effectiveValueChanged = currentCellToCalcEffectiveValue.calculateNewEffectiveValueAndDetermineIfItChanged();
                    if (effectiveValueChanged) {
                        newSheetVersion.numOfCellsWhichEffectiveValueChangedInNewVersion++;
                    }
                }
                //newCell.calculateNewEffectiveValueAndDetermineIfItChanged();



                return newSheetVersion;

//                try {
//                    List<Cell> cellsThatHaveChanged =
//                            newSheetVersion
//                                    .orderCellsForCalculation()
//                                    .stream()
//                                    .filter(Cell::calculateNewEffectiveValueAndDetermineIfItChanged) //is it "checking" here if effective value is valid after calculation? or it's better to do it before?
//                                    .collect(Collectors.toList());
//
//                    // successful calculation. update sheet and relevant cells version
//                    // int newVersion = newSheetVersion.increaseVersion();
//                    // cellsThatHaveChanged.forEach(cell -> cell.updateVersion(newVersion));
//
//                    return newSheetVersion;
//                } catch (Exception e) {
//                    // deal with the runtime error that was discovered as part of invocation
//                    return this;
//                }
        }
        } catch (IllegalArgumentException e) {
            // deal with the runtime error that was discovered as part of invocation
            throw e;
        }
        return null;
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


    private List<Cell> orderCellsForCalculation() {
        // data structure 1 0 1: Topological sort...
        // build graph from the cells. each cell is a node. each cell that has ref(s) constitutes an edge
        // handle case of circular dependencies -> should fail
        return null;
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
}