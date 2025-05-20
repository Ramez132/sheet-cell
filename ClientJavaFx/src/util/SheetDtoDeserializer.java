package util;

import com.google.gson.*;
import dto.cell.CellDto;
import dto.coordinate.CoordinateDto;
import dto.sheet.SheetDto;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SheetDtoDeserializer implements JsonDeserializer<SheetDto> {

    @Override
    public SheetDto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Extract fields from the JSON
        String sheetName = jsonObject.get("sheetName").getAsString();
        int numOfRows = jsonObject.get("numOfRows").getAsInt();
        int numOfColumns = jsonObject.get("numOfColumns").getAsInt();
        int rowHeight = jsonObject.get("rowHeight").getAsInt();
        int columnWidth = jsonObject.get("columnWidth").getAsInt();
        int thisSheetVersion = jsonObject.get("thisSheetVersion").getAsInt();

        // Deserialize the coordinateToCellDtoMap with custom logic
        JsonObject coordinateToCellDtoMapJson = jsonObject.getAsJsonObject("coordinateToCellDtoMap");
        Map<CoordinateDto, CellDto> coordinateToCellDtoMap = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : coordinateToCellDtoMapJson.entrySet()) {
            String coordinateString = entry.getKey();
            JsonObject cellDtoJson = entry.getValue().getAsJsonObject();

            // Convert the string key (like "B2") to a CoordinateDto
            CoordinateDto coordinate = parseCoordinate(coordinateString);

            // Deserialize the CellDto object
            CellDto cellDto = context.deserialize(cellDtoJson, CellDto.class);

            coordinateToCellDtoMap.put(coordinate, cellDto);
        }

        return new SheetDto(sheetName, coordinateToCellDtoMap, numOfRows, numOfColumns, rowHeight, columnWidth, thisSheetVersion);
    }

    // Method to parse a string like "B2" to a CoordinateDto
    private CoordinateDto parseCoordinate(String coordinateString) {
        char columnChar = coordinateString.charAt(0);
        int column = columnChar - 'A' + 1;
        int row = Integer.parseInt(coordinateString.substring(1));
        return new CoordinateDto(row, column);
    }
}
