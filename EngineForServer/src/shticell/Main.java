package shticell;//package shticell;

import engine.api.EngineManagerForServer;
import engine.impl.EngineManagerForServerImpl;
import shticell.cell.api.Cell;
import shticell.range.RangesManager;
import shticell.sheet.api.Sheet;
import shticell.sheet.impl.SheetImpl;

public class Main {
//method for testing
    public static void main(String[] args) {
//        Sheet sheet = new SheetImpl("newSheet", 10, 10,15,30,1);
//
//
//        EngineManagerForServer manager = new EngineManagerForServerImpl();
//        sheet = sheet.updateCellValueAndCalculate(1, 1, "5", true);
//        Object  value;
//        Cell cell = sheet.getCell(1, 1);
//        value = cell.getCurrentEffectiveValue().getValue();
//        System.out.println("value of cell A1 is: " + value);
//
//        //sheet1 = manager.updateValueOfCellAndGetNewSheet(sheet1, 1, 1, "5");
//
//        try {
////         sheet = sheet.updateCellValueAndCalculate(1, 1, "5", true);
////         sheet = sheet.updateCellValueAndCalculate(2, 1, "{REF,A1}", true);
////         sheet = sheet.updateCellValueAndCalculate(3, 1, "{plus,{REF,A1},{Ref,a2}}", true);
////         sheet = sheet.updateCellValueAndCalculate(4, 1, "Hello", true);
////         RangesManager.createRangeFromTwoCoordinateStringsAndNameString(sheet, "newRange", "A1", "A5");
////         sheet = sheet.updateCellValueAndCalculate(6, 1, "{sum,newRange}", true);
////         sheet = sheet.updateCellValueAndCalculate(7, 1, "{avErage,newRange}", true);
////         cell = sheet.getCell(6, 1);
////         value = cell.getCurrentEffectiveValue().getValue();
////         System.out.println("value of cell A6 is: " + value);
//
//
//            sheet = sheet.updateCellValueAndCalculate(1, 1, "4", true);
//            sheet = sheet.updateCellValueAndCalculate(2, 1, "true", true);
//            sheet = sheet.updateCellValueAndCalculate(3, 1, "{IF,{EQUAL,{REF,A1},{REF,A2}},{REF,a1},{ref,a2}}", true);
//            sheet = sheet.updateCellValueAndCalculate(4, 1, "{EQUAL,{REF,A1},{REF,A2}}", true);
//            RangesManager rangesManager = sheet.getRangesManager();
//            rangesManager.createRangeFromTwoCoordinateStringsAndNameString(sheet, "newRange", "A1", "A5");
//            sheet = sheet.updateCellValueAndCalculate(6, 1, "{sum,newRange}", true);
//            sheet = sheet.updateCellValueAndCalculate(7, 1, "{avErage,newRange}", true);
//            cell = sheet.getCell(3, 1);
//            value = cell.getCurrentEffectiveValue().getValue();
//            System.out.println("value of cell A3 is: " + value);
//
//            cell = sheet.getCell(4, 1);
//            value = cell.getCurrentEffectiveValue().getValue();
//            System.out.println("value of cell A4 is: " + value);
//        }
//
//        catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
////         sheet = sheet.updateCellValueAndCalculate(8, 1, "{Percent,{ref,a2},200}", true);
////         cell = sheet.getCell(8, 1);
////         value = cell.getCurrentEffectiveValue().getValue();
////         System.out.println("value of cell A8 is: " + value);
//
////         String filePath = "C:\\java-ex2-files\\grades - Copy.xml";
////         File file = new File(filePath);
////         EngineManagerForServer engineManager = new EngineManagerForServerImpl();
////         try {
////             sheet = engineManager.getSheetFromFile(file);
////         }
////         catch (Exception e) {
////             System.out.println(e.getMessage());
////         }
//
////         System.out.println("The sheet is: " + sheet.getNameOfSheet());
//
////        Object  value;
////        Cell cell = sheet.getCell(3, 1);
////        value = cell.getCurrentEffectiveValue().getValue();
////        System.out.println("value of cell A3 is: " + value);
////        sheet = sheet.updateCellValueAndCalculate(1, 1, "5", true);
////        cell = sheet.getCell(3, 1);
////        value = cell.getCurrentEffectiveValue().getValue();
////        System.out.println("Changed A1 to 5. now the value of cell A3 is: " + value);
////
////       cell.calculateNewEffectiveValueAndDetermineIfItChanged();
////
////        try {
////            sheet = sheet.updateCellValueAndCalculate(2, 2, "{PLUS,{REF,C3},4}", true);
////            cell = sheet.getCell(2, 2);
////            cell.calculateNewEffectiveValueAndDetermineIfItChanged();
////            value = cell.getCurrentEffectiveValue().getValue();
////            System.out.println("value of cell B2: " + value);
////        }
////        catch (Exception e) {
////            System.out.println(e.getMessage());
////        }
////
////        try {
////            sheet = sheet.updateCellValueAndCalculate(3, 3, "{plus,{REF,A1},5}", true);
////            cell = sheet.getCell(3, 3);
////            cell.calculateNewEffectiveValueAndDetermineIfItChanged();
////        }
////        catch (Exception e) {
////            System.out.println(e.getMessage());
////        }
////
////
////
////        try {
////            sheet = sheet.updateCellValueAndCalculate(1, 1, "{plus,{REF,A1},3}", true);
////            cell = sheet.getCell(1, 1);
////            cell.calculateNewEffectiveValueAndDetermineIfItChanged();
////        }
////        catch (Exception e) {
////            System.out.println(e.getMessage());
////        }
////
////        Double result = cell.getCurrentEffectiveValue().extractValueWithExpectation(Double.class);
////        System.out.println("result: " + result);
////
////        sheet = sheet.updateCellValueAndCalculate(3,5,"{REF,A1}", true);
////        Cell newCell = sheet.getCell(3,5);
////        //newCell.calculateNewEffectiveValueAndDetermineIfItChanged();
////        System.out.println("The cell A1 has: " + newCell.getCurrentEffectiveValue().getValue());
////
////        sheet = sheet.updateCellValueAndCalculate(4,4, "{concat,{REF,C3}, up}", true);
////        Cell cell2 = sheet.getCell(4, 4);
////        cell2.calculateNewEffectiveValueAndDetermineIfItChanged();
////        value = cell2.getCurrentEffectiveValue().getValue();
////        System.out.println("The cell D4 has value: " + value);
    }
}
//
//
//
////    public static void main(String[] args) {
////        Sheet sheet = new SheetImpl();
////
////        sheet = sheet.updateCellValueAndCalculate(5, 5, "{REF,B2}", true);
////        Cell cell3 = sheet.getCell(5, 5);
////        try {
////            cell3.calculateNewEffectiveValueAndDetermineIfItChanged();
////            Object value3 = cell3.getCurrentEffectiveValue().getValue();
////            System.out.println(value3);
////        } catch (Exception e) {
////            System.out.println("The cell 5,5 is empty.");
////        }
////
////
////        try {
////            sheet = sheet.updateCellValueAndCalculate(51, 51, "{REF,Z51}", true);
////        }
////        catch (IllegalArgumentException e) {
////            System.out.println("The cell 51,51 is out of range.");
////        }
////        sheet = sheet.updateCellValueAndCalculate(1, 1, "{Plus,{REF,B2},5}", true);
////
////        Object  value;
////        Cell cell = sheet.getCell(1, 1);
////        cell.calculateNewEffectiveValueAndDetermineIfItChanged();
////        value = cell.getCurrentEffectiveValue().getValue();
////        System.out.println("value of cell A1: " + value);
////
////        try {
////           sheet = sheet.updateCellValueAndCalculate(2, 2, "{PLUS,{REF,C3},4}", true);
////            cell = sheet.getCell(2, 2);
////            cell.calculateNewEffectiveValueAndDetermineIfItChanged();
////            value = cell.getCurrentEffectiveValue().getValue();
////            System.out.println("value of cell B2: " + value);
////        }
////        catch (Exception e) {
////            System.out.println(e.getMessage());
////        }
////
////        try {
////            sheet = sheet.updateCellValueAndCalculate(3, 3, "{plus,{REF,A1},5}", true);
////            cell = sheet.getCell(3, 3);
////            cell.calculateNewEffectiveValueAndDetermineIfItChanged();
////        }
////        catch (Exception e) {
////            System.out.println(e.getMessage());
////        }
////
////
////
////        try {
////            sheet = sheet.updateCellValueAndCalculate(1, 1, "{plus,{REF,A1},3}", true);
////            cell = sheet.getCell(1, 1);
////            cell.calculateNewEffectiveValueAndDetermineIfItChanged();
////        }
////        catch (Exception e) {
////            System.out.println(e.getMessage());
////        }
////
////        Double result = cell.getCurrentEffectiveValue().extractValueWithExpectation(Double.class);
////        System.out.println("result: " + result);
////
////        sheet = sheet.updateCellValueAndCalculate(3,5,"{REF,A1}", true);
////        Cell newCell = sheet.getCell(3,5);
////        //newCell.calculateNewEffectiveValueAndDetermineIfItChanged();
////        System.out.println("The cell A1 has: " + newCell.getCurrentEffectiveValue().getValue());
////
////        sheet = sheet.updateCellValueAndCalculate(4,4, "{concat,{REF,C3}, up}", true);
////        Cell cell2 = sheet.getCell(4, 4);
////        cell2.calculateNewEffectiveValueAndDetermineIfItChanged();
////        value = cell2.getCurrentEffectiveValue().getValue();
////        System.out.println("The cell D4 has value: " + value);
////    }
////}
//
//
////    public static void main(String[] args) {
////        Sheet sheet = new SheetImpl();
////
////        sheet.updateCellValueAndCalculate(5, 5, "{REF,B2}", true);
////        Cell cell3 = sheet.getCell(5, 5);
////        try {
////            cell3.calculateNewEffectiveValueAndDetermineIfItChanged();
////            Object value3 = cell3.getCurrentEffectiveValue().getValue();
////            System.out.println(value3);
////        } catch (Exception e) {
////            System.out.println("The cell is empty.");
////        }
////
////
////        try {
////            sheet.updateCellValueAndCalculate(51, 51, "{REF,Z51}", true);
////        }
////        catch (IllegalArgumentException e) {
////            System.out.println("The cell is out of range.");
////        }
////        sheet.updateCellValueAndCalculate(2, 2, "Hello, World!", true);
////
////        Cell cell = sheet.getCell(2, 2);
////        cell.calculateNewEffectiveValueAndDetermineIfItChanged();
////        Object value = cell.getCurrentEffectiveValue().getValue();
////        System.out.println(value);
////
////        sheet.updateCellValueAndCalculate(3, 3, "6", true);
////
////        try {
////            sheet.updateCellValueAndCalculate(1, 1, "{kop, {REF,D3}, 2}", true);
////            cell = sheet.getCell(1, 1);
////            cell.calculateNewEffectiveValueAndDetermineIfItChanged();
////        }
////        catch (Exception e) {
////            System.out.println(e.getMessage());
////        }
////
////        try {
////            sheet.updateCellValueAndCalculate(1, 1, "{plus,5,2,6}", true);
////            cell = sheet.getCell(1, 1);
////            cell.calculateNewEffectiveValueAndDetermineIfItChanged();
////        }
////        catch (Exception e) {
////            System.out.println(e.getMessage());
////        }
////
////        try {
////            sheet.updateCellValueAndCalculate(1, 1, "{plus,{REF,B2},3}", true);
////            cell = sheet.getCell(1, 1);
////            cell.calculateNewEffectiveValueAndDetermineIfItChanged();
////        }
////        catch (Exception e) {
////            System.out.println(e.getMessage());
////        }
////
////        Double result = cell.getCurrentEffectiveValue().extractValueWithExpectation(Double.class);
////        System.out.println("result: " + result);
////
////        sheet.updateCellValueAndCalculate(3,5,"{REF,A1}", true);
////        Cell newCell = sheet.getCell(3,5);
////        //newCell.calculateNewEffectiveValueAndDetermineIfItChanged();
////        System.out.println("The cell A1 has: " + newCell.getCurrentEffectiveValue().getValue());
////
////        sheet.updateCellValueAndCalculate(4,4, "{concat,{REF,C3}, up}", true);
////        Cell cell2 = sheet.getCell(4, 4);
////        cell2.calculateNewEffectiveValueAndDetermineIfItChanged();
////        value = cell2.getCurrentEffectiveValue().getValue();
////        System.out.println("The cell D4 has value: " + value);
////    }
////}
//
//
////another example of error in effective value - PLUS should return NaN:
//
////public class Main {
////    public static void main(String[] args) {
////        Sheet sheet = new SheetImpl();
////        sheet.updateCellValueAndCalculate(2, 2, "Hello, World!");
////
////        Cell cell = sheet.getCell(2, 2);
////        cell.calculateNewEffectiveValueAndDetermineIfItChanged();
////        Object value = cell.getCurrentEffectiveValue().getValue();
////        System.out.println(value);
////
////        sheet.updateCellValueAndCalculate(3, 3, "5");
////        sheet.updateCellValueAndCalculate(1,1, "{plus, {REF,C3}, 2}");
////
////        cell = sheet.getCell(1, 1);
////
////        cell.calculateNewEffectiveValueAndDetermineIfItChanged();
////        Double result = cell.getCurrentEffectiveValue().extractValueWithExpectation(Double.class);
////        System.out.println("result: " + result);
////
////        Cell newCell = new CellImpl(3, 5, "{REF,B2}", 0, sheet);
////        newCell.calculateNewEffectiveValueAndDetermineIfItChanged();
////        System.out.println("The cell B2 has: " + newCell.getCurrentEffectiveValue().getValue());
////    }
////}
//
//
////example of error in effective value - PLUS should return NaN:
//
////public static void main(String[] args) {
////    Sheet sheet = new SheetImpl();
////    sheet.updateCellValueAndCalculate(2, 2, "Hello, World!");
////
////    Cell cell = sheet.getCell(2, 2);
////    cell.calculateNewEffectiveValueAndDetermineIfItChanged();
////    Object value = cell.getCurrentEffectiveValue().getValue();
////    System.out.println(value);
////
////    sheet.updateCellValueAndCalculate(1,1, "{plus, {REF,B2}, 2}");
////                          //example of error in effective value - should return NaN
////
////    cell = sheet.getCell(1, 1);
////
////    cell.calculateNewEffectiveValueAndDetermineIfItChanged();
////    Double result = cell.getCurrentEffectiveValue().extractValueWithExpectation(Double.class);
////    System.out.println("result: " + result);
////
////    Cell newCell = new CellImpl(3, 5, "{REF,B2}", 0, sheet);
////    newCell.calculateNewEffectiveValueAndDetermineIfItChanged();
////    System.out.println("The cell B2 has: " + newCell.getCurrentEffectiveValue().getValue());
////}