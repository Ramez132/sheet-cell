package shticell.sorter;

import shticell.coordinate.Coordinate;
import shticell.coordinate.CoordinateFactory;
import shticell.range.Range;
import shticell.range.RangeImpl;
import shticell.row.RangeWithRowsInArea;
import shticell.row.RowInArea;
import shticell.sheet.api.Sheet;

import java.util.*;

public class RangeSorter {
    RangeWithRowsInArea rangeWithRowsToSort;
    RangeWithRowsInArea sortedRangeWithRows;
    Sheet sheet;
    List<Character> columnsToSortBy; //first column to sort by will be the first element in the list, etc.
    List<Double> sortedUniqueNumbersInFirstColumnToSortBy;

    //relevant for the creating an instance from outside this class, for the first time, in order to sort it
    //effectiveValues are not yet filled
    public RangeSorter(Sheet sheet, Range rangeToSort, List<Character> columnsToSortBy) {
        this.rangeWithRowsToSort = new RangeWithRowsInArea(sheet, rangeToSort);
        this.rangeWithRowsToSort.buildRangeFromAllRelevantCellsInRange();
        this.columnsToSortBy = columnsToSortBy;
        this.sheet = sheet;
    }

    //relevant for the creating an instance from within this class, in order to sort partial range and then unite all partial ranges
    //effectiveValues are already filled in all rows
    public RangeSorter(Sheet sheet, RangeWithRowsInArea rangeWithRowsToSort, List<Character> columnsToSortBy) {
        this.rangeWithRowsToSort = rangeWithRowsToSort;
        this.columnsToSortBy = columnsToSortBy;
        this.sheet = sheet;
    }

    public void sortRange() {
        if (columnsToSortBy.isEmpty()) {
            //no more columns to sort by - then for this system, it's considered sorted
            //updating the sorted range with the original and finishing
            sortedRangeWithRows = rangeWithRowsToSort;
            return;
        } else { //there are more columns to sort by
            updateListOfSortedUniqueNumbersInFirstColumnToSortBy();

            Map<Double, List<RowInArea>> mapFromUniqueValueInSortingColumnToListOfRelevantRows = new HashMap<>();
            for (Double uniqueValueInFirstColumnToSortBy : sortedUniqueNumbersInFirstColumnToSortBy) {
                mapFromUniqueValueInSortingColumnToListOfRelevantRows.put(uniqueValueInFirstColumnToSortBy, new ArrayList<>());
            }
            int rowStart = rangeWithRowsToSort.getRange().getRowStart();
            int rowEnd = rangeWithRowsToSort.getRange().getRowEnd();
            int columnNumOfFirstColumnToSortBy = CoordinateFactory.getColumnNumberFromChar(columnsToSortBy.getFirst(), sheet);

            for (int currentRow = rowStart; currentRow <= rowEnd; currentRow++) {
                double valueInFirstColumnToSortBy =
                        rangeWithRowsToSort.getEffectiveValueOfCellAsDouble(currentRow, columnNumOfFirstColumnToSortBy);
                RowInArea selectedRowInArea = rangeWithRowsToSort.getSelectedRowInAreaFromMap(currentRow);
                mapFromUniqueValueInSortingColumnToListOfRelevantRows
                        .get(valueInFirstColumnToSortBy) // returns List<RowInArea>
                        .add(selectedRowInArea);
            }

            int firstColumnOfPartialRange = rangeWithRowsToSort.getRange().getColumnStart();
            int lastColumnOfPartialRange = rangeWithRowsToSort.getRange().getColumnEnd();

            ArrayList<RangeSorter> arrayOfSortedPartialRangeSorters = new ArrayList<>();
            List<Character> newListOfColumnsToSortByWithoutFirstColumn = new ArrayList<>(columnsToSortBy);
            //the method checked at the beginning if columnsToSortBy is empty
            //will get here only if columnsToSortBy has at least 1 element, therefore remove first will not throw an exception
            newListOfColumnsToSortByWithoutFirstColumn.removeFirst();


            int updatedRowNumber = rowStart;
            for (Double uniqueValueInFirstColumnToSortBy : sortedUniqueNumbersInFirstColumnToSortBy) {
                List<RowInArea> currentListOfRows = mapFromUniqueValueInSortingColumnToListOfRelevantRows.get(uniqueValueInFirstColumnToSortBy);
                //update row numbers in each row
                for (RowInArea currentRowInArea : currentListOfRows) {
                    currentRowInArea.setRowNumber(updatedRowNumber);
                    updatedRowNumber++;
                }

                int firstRowOfPartialRange = currentListOfRows.getFirst().getRowNumber();
                int lastRowInPartialRange = currentListOfRows.getLast().getRowNumber();
                Coordinate startCoordinateOfPartialRange = CoordinateFactory.getCoordinate(firstRowOfPartialRange, firstColumnOfPartialRange);
                Coordinate endCoordinateOfPartialRange = CoordinateFactory.getCoordinate(lastRowInPartialRange, lastColumnOfPartialRange);
                Range newPartialRange = new RangeImpl(startCoordinateOfPartialRange, endCoordinateOfPartialRange);
                RangeWithRowsInArea partialRangeWithRowsInArea = new RangeWithRowsInArea(newPartialRange);

                //updating the map of rows in partial RangeWithRowsInArea - with rows from currentListOfRows and their row numbers
                for (RowInArea currentRowInArea : currentListOfRows) {
                    int currentRowNum = currentRowInArea.getRowNumber();
                    partialRangeWithRowsInArea.addRowInAreaToMap(currentRowNum, currentRowInArea);
                }
                RangeSorter partialRangeSorter = new RangeSorter(sheet, partialRangeWithRowsInArea, newListOfColumnsToSortByWithoutFirstColumn);
                partialRangeSorter.sortRange();
                arrayOfSortedPartialRangeSorters.add(partialRangeSorter);
            }

            updateSortedRangeFromArrayOfSortedPartialRangeSorters(arrayOfSortedPartialRangeSorters);
        }
    }

    public void updateListOfSortedUniqueNumbersInFirstColumnToSortBy() {
        char firstColumnLetterToSortBy = columnsToSortBy.getFirst();
        int firstColumnToSortByAsNumber = CoordinateFactory.getColumnNumberFromChar(firstColumnLetterToSortBy, sheet);
        int firstRowInArea = rangeWithRowsToSort.getRange().getRowStart();
        int lastRowInArea = rangeWithRowsToSort.getRange().getRowEnd();
        List<String> uniqueValuesInFirstColumnToSortByAsStrings = new ArrayList<>();
        
        for (int currentRow = firstRowInArea; currentRow <= lastRowInArea; currentRow++) {
            
            String valueInFirstColumnToSortBy = rangeWithRowsToSort.getEffectiveValueOfCellAsString(currentRow, firstColumnToSortByAsNumber);

            if (!uniqueValuesInFirstColumnToSortByAsStrings.contains(valueInFirstColumnToSortBy)) {
                uniqueValuesInFirstColumnToSortByAsStrings.add(valueInFirstColumnToSortBy);
            }
//            if (rangeToSort.mapRowNumberToRowInArea.get(currentRow).isColumnInRow(firstColumnToSortByAsNumber)) {
//                rangeToSort.mapRowNumberToRowInArea.get(currentRow).getEffectiveValueOfColumn(firstColumnToSortByAsNumber);
//            }
        }

        List<Double> uniqueNumbersInFirstColumnToSortBy = new ArrayList<>();
        for (String uniqueValueInFirstColumnToSortByAsString : uniqueValuesInFirstColumnToSortByAsStrings) {
            uniqueNumbersInFirstColumnToSortBy.add(Double.parseDouble(uniqueValueInFirstColumnToSortByAsString));
        }
        
        uniqueNumbersInFirstColumnToSortBy.sort(Comparator.naturalOrder());
        sortedUniqueNumbersInFirstColumnToSortBy = uniqueNumbersInFirstColumnToSortBy;
//        List<String> uniqueValuesInFirstColumnToSortByAsStrings = sheet.getUniqueValuesForFilteringInSelectedColumnAndRelevantArea(firstColumnLetterToSortBy,
//                rangeToSort.getTopLeftStartCoordinate().toString(), rangeToSort.getBottomRightEndCoordinate().toString());
//        List<Double> uniqueValuesInFirstColumnToSortByAsNumbers = new ArrayList<>();
//
//        Coordinate topLeftStartCoordinate = rangeToSort.getTopLeftStartCoordinate();
//        Coordinate bottomRightEndCoordinate = rangeToSort.getBottomRightEndCoordinate();
//        return rangeToSort.getRowsInAreaList().get(0).getMapColumnToEffectiveValueString().keySet();
    }

    public void updateSortedRangeFromArrayOfSortedPartialRangeSorters(ArrayList<RangeSorter> arrayOfPartialRangeSorters) {
        //sorted range has the same start and end coordinates of the original range
        sortedRangeWithRows = new RangeWithRowsInArea(rangeWithRowsToSort.getRange());

        for (RangeSorter partialRangeSorter : arrayOfPartialRangeSorters) {
            Map<Integer, RowInArea> mapRowNumberToRowInArea = partialRangeSorter.sortedRangeWithRows.getMapRowNumberToRowInArea();
            for (Integer rowNumber : mapRowNumberToRowInArea.keySet()) {
                sortedRangeWithRows.addRowInAreaToMap(rowNumber, mapRowNumberToRowInArea.get(rowNumber));
            }
        }
    }

    public RangeWithRowsInArea getSortedRangeWithRows() {
        return sortedRangeWithRows;
    }
}
