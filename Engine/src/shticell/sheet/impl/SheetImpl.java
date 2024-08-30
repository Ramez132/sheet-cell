package shticell.sheet.impl;

import shticell.cell.impl.CellImpl;
import shticell.sheet.api.Sheet;
import shticell.cell.api.Cell;
import shticell.coordinate.Coordinate;
import shticell.coordinate.CoordinateFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SheetImpl implements Sheet {

    private Map<Coordinate, Cell> activeCells = new HashMap<>();
    private int maxRowNumber = 50;
    private int maxColumnNumber = 20;
    private int thisSheetVersion = 1;
    private final int versionNumForEmptyCellWithoutPreviousValues = -1 ;//-1 to indicate that the cell has never been set before


    public SheetImpl(int maxRowNumber, int maxColumnNumber, int thisSheetVersion) {
        this();
        this.maxRowNumber = maxRowNumber;
        this.maxColumnNumber = maxColumnNumber;
        this.thisSheetVersion = thisSheetVersion;
    }

    public SheetImpl() {
        this.activeCells = new HashMap<>();
    }

    @Override
    public int getVersion() {
        return thisSheetVersion;
    }

    @Override
    public int getMaximumRowNumber() {
        return maxRowNumber;
    }

    @Override
    public int getMaximumColumnNumber() {
        return maxColumnNumber;
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

        if (row > maxRowNumber) {
            throw new IllegalArgumentException("Row number provided is greater than the maximum row number of the sheet");
        }
        if (column > maxColumnNumber) {
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
    public Sheet updateCellValueAndCalculate
                        (int row, int column, String value,
                         boolean isUpdatePartOfSheetInitialization)
                         throws IllegalArgumentException {

        try {
            if (isCoordinateInSheetRange(row, column)) {

                Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);

                SheetImpl newSheetVersion = copySheet();
                int sheetVersionForNewCell;
                if (isUpdatePartOfSheetInitialization) {
                    sheetVersionForNewCell = 1;
                } else {
                    sheetVersionForNewCell = newSheetVersion.getVersion() + 1;
                }
                Cell newCell = new CellImpl(row, column, value, sheetVersionForNewCell, newSheetVersion);
                newSheetVersion.activeCells.put(coordinate, newCell);

                try {
                    List<Cell> cellsThatHaveChanged =
                            newSheetVersion
                                    .orderCellsForCalculation()
                                    .stream()
                                    .filter(Cell::calculateEffectiveValue) //is it "checking" here if effective value is valid after calculation? or it's better to do it before?
                                    .collect(Collectors.toList());

                    // successful calculation. update sheet and relevant cells version
                    // int newVersion = newSheetVersion.increaseVersion();
                    // cellsThatHaveChanged.forEach(cell -> cell.updateVersion(newVersion));

                    return newSheetVersion;
                } catch (Exception e) {
                    // deal with the runtime error that was discovered as part of invocation
                    return this;
                }
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
                adjacencyList.put(cell, new ArrayList<>(cell.getDependsOn()));
            }
        }

        return adjacencyList;
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