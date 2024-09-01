package shticell;

import shticell.sheet.api.Sheet;
import shticell.cell.api.Cell;
import shticell.sheet.impl.SheetImpl;

public class Main {

    public static void main(String[] args) {
        Sheet sheet = new SheetImpl();

        sheet = sheet.updateCellValueAndCalculate(5, 5, "{REF,B2}", true);
        Cell cell3 = sheet.getCell(5, 5);
        try {
            cell3.calculateNewEffectiveValueAndDetermineIfItChanged();
            Object value3 = cell3.getCurrentEffectiveValue().getValue();
            System.out.println(value3);
        } catch (Exception e) {
            System.out.println("The cell 5,5 is empty.");
        }


        try {
            sheet = sheet.updateCellValueAndCalculate(51, 51, "{REF,Z51}", true);
        }
        catch (IllegalArgumentException e) {
            System.out.println("The cell 51,51 is out of range.");
        }
        sheet = sheet.updateCellValueAndCalculate(1, 1, "{Plus,{REF,B2},5}", true);

        Object  value;
        Cell cell = sheet.getCell(1, 1);
        cell.calculateNewEffectiveValueAndDetermineIfItChanged();
        value = cell.getCurrentEffectiveValue().getValue();
        System.out.println("value of cell A1: " + value);

        try {
           sheet = sheet.updateCellValueAndCalculate(2, 2, "{PLUS,{REF,C3},4}", true);
            cell = sheet.getCell(2, 2);
            cell.calculateNewEffectiveValueAndDetermineIfItChanged();
            value = cell.getCurrentEffectiveValue().getValue();
            System.out.println("value of cell B2: " + value);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            sheet = sheet.updateCellValueAndCalculate(3, 3, "{plus,{REF,A1},5}", true);
            cell = sheet.getCell(3, 3);
            cell.calculateNewEffectiveValueAndDetermineIfItChanged();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }



        try {
            sheet = sheet.updateCellValueAndCalculate(1, 1, "{plus,{REF,A1},3}", true);
            cell = sheet.getCell(1, 1);
            cell.calculateNewEffectiveValueAndDetermineIfItChanged();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Double result = cell.getCurrentEffectiveValue().extractValueWithExpectation(Double.class);
        System.out.println("result: " + result);

        sheet = sheet.updateCellValueAndCalculate(3,5,"{REF,A1}", true);
        Cell newCell = sheet.getCell(3,5);
        //newCell.calculateNewEffectiveValueAndDetermineIfItChanged();
        System.out.println("The cell A1 has: " + newCell.getCurrentEffectiveValue().getValue());

        sheet = sheet.updateCellValueAndCalculate(4,4, "{concat,{REF,C3}, up}", true);
        Cell cell2 = sheet.getCell(4, 4);
        cell2.calculateNewEffectiveValueAndDetermineIfItChanged();
        value = cell2.getCurrentEffectiveValue().getValue();
        System.out.println("The cell D4 has value: " + value);
    }
}


//    public static void main(String[] args) {
//        Sheet sheet = new SheetImpl();
//
//        sheet.updateCellValueAndCalculate(5, 5, "{REF,B2}", true);
//        Cell cell3 = sheet.getCell(5, 5);
//        try {
//            cell3.calculateNewEffectiveValueAndDetermineIfItChanged();
//            Object value3 = cell3.getCurrentEffectiveValue().getValue();
//            System.out.println(value3);
//        } catch (Exception e) {
//            System.out.println("The cell is empty.");
//        }
//
//
//        try {
//            sheet.updateCellValueAndCalculate(51, 51, "{REF,Z51}", true);
//        }
//        catch (IllegalArgumentException e) {
//            System.out.println("The cell is out of range.");
//        }
//        sheet.updateCellValueAndCalculate(2, 2, "Hello, World!", true);
//
//        Cell cell = sheet.getCell(2, 2);
//        cell.calculateNewEffectiveValueAndDetermineIfItChanged();
//        Object value = cell.getCurrentEffectiveValue().getValue();
//        System.out.println(value);
//
//        sheet.updateCellValueAndCalculate(3, 3, "6", true);
//
//        try {
//            sheet.updateCellValueAndCalculate(1, 1, "{kop, {REF,D3}, 2}", true);
//            cell = sheet.getCell(1, 1);
//            cell.calculateNewEffectiveValueAndDetermineIfItChanged();
//        }
//        catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//
//        try {
//            sheet.updateCellValueAndCalculate(1, 1, "{plus,5,2,6}", true);
//            cell = sheet.getCell(1, 1);
//            cell.calculateNewEffectiveValueAndDetermineIfItChanged();
//        }
//        catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//
//        try {
//            sheet.updateCellValueAndCalculate(1, 1, "{plus,{REF,B2},3}", true);
//            cell = sheet.getCell(1, 1);
//            cell.calculateNewEffectiveValueAndDetermineIfItChanged();
//        }
//        catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//
//        Double result = cell.getCurrentEffectiveValue().extractValueWithExpectation(Double.class);
//        System.out.println("result: " + result);
//
//        sheet.updateCellValueAndCalculate(3,5,"{REF,A1}", true);
//        Cell newCell = sheet.getCell(3,5);
//        //newCell.calculateNewEffectiveValueAndDetermineIfItChanged();
//        System.out.println("The cell A1 has: " + newCell.getCurrentEffectiveValue().getValue());
//
//        sheet.updateCellValueAndCalculate(4,4, "{concat,{REF,C3}, up}", true);
//        Cell cell2 = sheet.getCell(4, 4);
//        cell2.calculateNewEffectiveValueAndDetermineIfItChanged();
//        value = cell2.getCurrentEffectiveValue().getValue();
//        System.out.println("The cell D4 has value: " + value);
//    }
//}


//another example of error in effective value - PLUS should return NaN:

//public class Main {
//    public static void main(String[] args) {
//        Sheet sheet = new SheetImpl();
//        sheet.updateCellValueAndCalculate(2, 2, "Hello, World!");
//
//        Cell cell = sheet.getCell(2, 2);
//        cell.calculateNewEffectiveValueAndDetermineIfItChanged();
//        Object value = cell.getCurrentEffectiveValue().getValue();
//        System.out.println(value);
//
//        sheet.updateCellValueAndCalculate(3, 3, "5");
//        sheet.updateCellValueAndCalculate(1,1, "{plus, {REF,C3}, 2}");
//
//        cell = sheet.getCell(1, 1);
//
//        cell.calculateNewEffectiveValueAndDetermineIfItChanged();
//        Double result = cell.getCurrentEffectiveValue().extractValueWithExpectation(Double.class);
//        System.out.println("result: " + result);
//
//        Cell newCell = new CellImpl(3, 5, "{REF,B2}", 0, sheet);
//        newCell.calculateNewEffectiveValueAndDetermineIfItChanged();
//        System.out.println("The cell B2 has: " + newCell.getCurrentEffectiveValue().getValue());
//    }
//}


//example of error in effective value - PLUS should return NaN:

//public static void main(String[] args) {
//    Sheet sheet = new SheetImpl();
//    sheet.updateCellValueAndCalculate(2, 2, "Hello, World!");
//
//    Cell cell = sheet.getCell(2, 2);
//    cell.calculateNewEffectiveValueAndDetermineIfItChanged();
//    Object value = cell.getCurrentEffectiveValue().getValue();
//    System.out.println(value);
//
//    sheet.updateCellValueAndCalculate(1,1, "{plus, {REF,B2}, 2}");
//                          //example of error in effective value - should return NaN
//
//    cell = sheet.getCell(1, 1);
//
//    cell.calculateNewEffectiveValueAndDetermineIfItChanged();
//    Double result = cell.getCurrentEffectiveValue().extractValueWithExpectation(Double.class);
//    System.out.println("result: " + result);
//
//    Cell newCell = new CellImpl(3, 5, "{REF,B2}", 0, sheet);
//    newCell.calculateNewEffectiveValueAndDetermineIfItChanged();
//    System.out.println("The cell B2 has: " + newCell.getCurrentEffectiveValue().getValue());
//}