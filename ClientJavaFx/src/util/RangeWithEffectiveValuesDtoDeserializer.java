package util;

import com.google.gson.*;
import dto.coordinate.CoordinateDto;
import dto.range.RangeDto;
import dto.range.RangeWithEffectiveValuesDto;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RangeWithEffectiveValuesDtoDeserializer implements JsonDeserializer<RangeWithEffectiveValuesDto> {

    @Override
    public RangeWithEffectiveValuesDto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Deserialize RangeDto part
        JsonObject rangeDtoJson = jsonObject.getAsJsonObject("rangeDto");
        String rangeName = rangeDtoJson.get("rangeName").getAsString();
        CoordinateDto topLeftStartCoordinate = context.deserialize(rangeDtoJson.get("topLeftStartCoordinate"), CoordinateDto.class);
        CoordinateDto bottomRightEndCoordinate = context.deserialize(rangeDtoJson.get("bottomRightEndCoordinate"), CoordinateDto.class);

        // Deserialize allCoordinatesDtoThatBelongToThisRange
        JsonArray coordinatesArray = rangeDtoJson.getAsJsonArray("allCoordinatesDtoThatBelongToThisRange");
        Set<CoordinateDto> allCoordinatesDtoThatBelongToThisRange = new HashSet<>();
        for (JsonElement element : coordinatesArray) {
            CoordinateDto coordinate = context.deserialize(element, CoordinateDto.class);
            allCoordinatesDtoThatBelongToThisRange.add(coordinate);
        }

        // Get row and column start/end values
        int rowStart = rangeDtoJson.get("rowStart").getAsInt();
        int rowEnd = rangeDtoJson.get("rowEnd").getAsInt();
        int columnStart = rangeDtoJson.get("columnStart").getAsInt();
        int columnEnd = rangeDtoJson.get("columnEnd").getAsInt();

        // Create the RangeDto object
        RangeDto rangeDto = new RangeDto(rangeName, topLeftStartCoordinate, bottomRightEndCoordinate, allCoordinatesDtoThatBelongToThisRange, rowStart, rowEnd, columnStart, columnEnd);

        // Deserialize allEffectiveValuesStrings part
        JsonObject allEffectiveValuesStringsJson = jsonObject.getAsJsonObject("allEffectiveValuesStrings");
        Map<CoordinateDto, String> allEffectiveValuesStrings = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : allEffectiveValuesStringsJson.entrySet()) {
            String coordinateKey = entry.getKey();
            String valueString = entry.getValue().getAsString();

            // Convert the string key to a CoordinateDto
            CoordinateDto coordinate = parseCoordinate(coordinateKey);

            allEffectiveValuesStrings.put(coordinate, valueString);
        }

        // Return the RangeWithEffectiveValuesDto
        return new RangeWithEffectiveValuesDto(rangeDto, allEffectiveValuesStrings);
    }

    // Helper method to parse a coordinate string like "B2" into a CoordinateDto
    private CoordinateDto parseCoordinate(String coordinateString) {
        char columnChar = coordinateString.charAt(0);
        int column = columnChar - 'A' + 1;
        int row = Integer.parseInt(coordinateString.substring(1));
        return new CoordinateDto(row, column);
    }
}
