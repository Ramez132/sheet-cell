package servlets;

import com.google.gson.Gson;
import constants.Constants;
import engine.api.EngineManagerForServer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;

import java.io.IOException;
import java.util.List;

public class GetUniqueValuesForFilteringServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String selectedSheetName = request.getParameter(Constants.SHEET_NAME);
        String filterAreaStartCoordinateStr = request.getParameter(Constants.LEFT_TOP_START_COORDINATE);
        String filterAreaEndCoordinateStr = request.getParameter(Constants.RIGHT_BOTTOM_END_COORDINATE);
        String stringWithLetterOfColumnToGetUniqueValuesToFilter = request.getParameter(Constants.COLUMN_CHAR_STRING).toUpperCase();

        try {
            EngineManagerForServer engineManager = ServletUtils.getEngineManager(getServletContext());
            List<String> uniqueValuesInSelectedColumn =
                    engineManager.getUniqueValuesForFilteringInSelectedColumnAndRelevantArea
                    (selectedSheetName, filterAreaStartCoordinateStr,
                            filterAreaEndCoordinateStr, stringWithLetterOfColumnToGetUniqueValuesToFilter);

            Gson gson = new Gson();
            response.setContentType("application/json");
            String jsonResult = gson.toJson(uniqueValuesInSelectedColumn);
            response.getWriter().write(jsonResult);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error: " + e.getMessage());
        }
    }
}
