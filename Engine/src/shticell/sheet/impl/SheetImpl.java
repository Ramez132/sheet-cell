package shticell.sheet.impl;

import shticell.cell.impl.CellImpl;
import shticell.sheet.api.Sheet;
import shticell.cell.api.Cell;
import shticell.coordinate.Coordinate;
import shticell.coordinate.CoordinateFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SheetImpl implements Sheet {

    private Map<Coordinate, Cell> activeCells = new HashMap<>();
    private int maxRowNumber;
    private int maxColumnNumber;
    private int thisSheetVersion;


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
            boolean isCoordinateValid = checkIfCoordinateIsValid(row, column);

            if (isCoordinateValid) {
                return activeCells.get(CoordinateFactory.createCoordinate(row, column));
            }
        } catch (IllegalArgumentException e) {
            // deal with the runtime error that was discovered as part of invocation
            throw  e;
        }

        return null;
    }

    private boolean checkIfCoordinateIsValid(int row, int column) throws IllegalArgumentException {

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

    @Override
    public Sheet updateCellValueAndCalculate(int row, int column, String value) throws IllegalArgumentException {

        try {
            if (checkIfCoordinateIsValid(row, column)) {

                Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);

                SheetImpl newSheetVersion = copySheet();
                Cell newCell = new CellImpl(row, column, value, newSheetVersion.getVersion() + 1, newSheetVersion);
                newSheetVersion.activeCells.put(coordinate, newCell);

                try {
                    List<Cell> cellsThatHaveChanged =
                            newSheetVersion
                                    .orderCellsForCalculation()
                                    .stream()
                                    .filter(Cell::calculateEffectiveValue)
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