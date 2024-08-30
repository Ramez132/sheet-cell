package shticell;

import shticell.cell.impl.CellImpl;
import shticell.sheet.api.Sheet;
import shticell.cell.api.Cell;
import shticell.sheet.impl.SheetImpl;

public class Main {
    public static void main(String[] args) {
        Sheet sheet = new SheetImpl();

        sheet.updateCellValueAndCalculate(5, 5, "{REF,B2}", true);
        Cell cell3 = sheet.getCell(5, 5);
        try {
            cell3.calculateEffectiveValue();
            Object value3 = cell3.getEffectiveValue().getValue();
            System.out.println(value3);
        } catch (Exception e) {
            System.out.println("The cell is empty.");
        }


        try {
            sheet.updateCellValueAndCalculate(51, 51, "{REF,Z51}", true);
        }
        catch (IllegalArgumentException e) {
            System.out.println("The cell is out of range.");
        }
        sheet.updateCellValueAndCalculate(2, 2, "Hello, World!", true);

        Cell cell = sheet.getCell(2, 2);
        cell.calculateEffectiveValue();
        Object value = cell.getEffectiveValue().getValue();
        System.out.println(value);

        sheet.updateCellValueAndCalculate(3, 3, "6", true);
        sheet.updateCellValueAndCalculate(1,1, "{plus, {REF,D3}, 2}", true);

        cell = sheet.getCell(1, 1);

        cell.calculateEffectiveValue();
        Double result = cell.getEffectiveValue().extractValueWithExpectation(Double.class);
        System.out.println("result: " + result);

        sheet.updateCellValueAndCalculate(3,5,"{REF,A1}", true);
        Cell newCell = sheet.getCell(3,5);
        //newCell.calculateEffectiveValue();
        System.out.println("The cell A1 has: " + newCell.getEffectiveValue().getValue());

        sheet.updateCellValueAndCalculate(4,4, "{concat,{REF,C3}, up}", true);
        Cell cell2 = sheet.getCell(4, 4);
        cell2.calculateEffectiveValue();
        value = cell2.getEffectiveValue().getValue();
        System.out.println("The cell D4 has value: " + value);
    }
}


//another example of error in effective value - PLUS should return NaN:

//public class Main {
//    public static void main(String[] args) {
//        Sheet sheet = new SheetImpl();
//        sheet.updateCellValueAndCalculate(2, 2, "Hello, World!");
//
//        Cell cell = sheet.getCell(2, 2);
//        cell.calculateEffectiveValue();
//        Object value = cell.getEffectiveValue().getValue();
//        System.out.println(value);
//
//        sheet.updateCellValueAndCalculate(3, 3, "5");
//        sheet.updateCellValueAndCalculate(1,1, "{plus, {REF,C3}, 2}");
//
//        cell = sheet.getCell(1, 1);
//
//        cell.calculateEffectiveValue();
//        Double result = cell.getEffectiveValue().extractValueWithExpectation(Double.class);
//        System.out.println("result: " + result);
//
//        Cell newCell = new CellImpl(3, 5, "{REF,B2}", 0, sheet);
//        newCell.calculateEffectiveValue();
//        System.out.println("The cell B2 has: " + newCell.getEffectiveValue().getValue());
//    }
//}


//example of error in effective value - PLUS should return NaN:

//public static void main(String[] args) {
//    Sheet sheet = new SheetImpl();
//    sheet.updateCellValueAndCalculate(2, 2, "Hello, World!");
//
//    Cell cell = sheet.getCell(2, 2);
//    cell.calculateEffectiveValue();
//    Object value = cell.getEffectiveValue().getValue();
//    System.out.println(value);
//
//    sheet.updateCellValueAndCalculate(1,1, "{plus, {REF,B2}, 2}");
//                          //example of error in effective value - should return NaN
//
//    cell = sheet.getCell(1, 1);
//
//    cell.calculateEffectiveValue();
//    Double result = cell.getEffectiveValue().extractValueWithExpectation(Double.class);
//    System.out.println("result: " + result);
//
//    Cell newCell = new CellImpl(3, 5, "{REF,B2}", 0, sheet);
//    newCell.calculateEffectiveValue();
//    System.out.println("The cell B2 has: " + newCell.getEffectiveValue().getValue());
//}