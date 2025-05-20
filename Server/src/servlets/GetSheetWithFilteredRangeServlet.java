package servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import constants.Constants;
import dto.sheet.SheetWithSortedOrFilteredRangeDto;
import dto.sort.SortParametersDto;
import engine.api.EngineManagerForServer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class GetSheetWithFilteredRangeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        String selectedSheetName = request.getParameter(Constants.SHEET_NAME);
        String filterAreaStartCoordinateStr = request.getParameter(Constants.LEFT_TOP_START_COORDINATE);
        String filterAreaEndCoordinateStr = request.getParameter(Constants.RIGHT_BOTTOM_END_COORDINATE);
        char ColumnLetterForFiltering = request.getParameter(Constants.COLUMN_CHAR_STRING).toUpperCase().charAt(0);

        Type listType = new TypeToken<List<String>>(){}.getType();
        List<String> uniqueValuesInSelectedColumn = gson.fromJson(request.getReader(), listType);

        try {
            EngineManagerForServer engineManager = ServletUtils.getEngineManager(getServletContext());
            SheetWithSortedOrFilteredRangeDto sheetWithFilteredRangeDto =
                    engineManager.getSheetDtoWithFilteredArea(selectedSheetName, filterAreaStartCoordinateStr,
                            filterAreaEndCoordinateStr, ColumnLetterForFiltering,uniqueValuesInSelectedColumn);

            response.setContentType("application/json");
            String jsonResult = gson.toJson(sheetWithFilteredRangeDto);
            response.getWriter().write(jsonResult);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error: " + e.getMessage());
        }
    }
}
