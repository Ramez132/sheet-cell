package servlets;

import com.google.gson.Gson;
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
import java.util.List;

public class GetSheetWithSortedRangeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String selectedSheetName = request.getParameter(Constants.SHEET_NAME);
        Gson gson = new Gson();
        SortParametersDto sortParametersDto = gson.fromJson(request.getReader(), SortParametersDto.class);
        List<String> allColumnLettersToSortByAsStrings = sortParametersDto.allColumnLettersToSortByAsStrings();
        String newSortStartCoordinateStr = sortParametersDto.newSortStartCoordinateStr();
        String newSortEndCoordinateStr = sortParametersDto.newSortEndCoordinateStr();

        try {
            EngineManagerForServer engineManager = ServletUtils.getEngineManager(getServletContext());
            SheetWithSortedOrFilteredRangeDto sheetWithSortedRangeDto =
                    engineManager.getSheetDtoWithSortedArea(selectedSheetName, newSortStartCoordinateStr,
                                                            newSortEndCoordinateStr, allColumnLettersToSortByAsStrings);

            response.setContentType("application/json");
            String jsonResult = gson.toJson(sheetWithSortedRangeDto);
            response.getWriter().write(jsonResult);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error: " + e.getMessage());
        }
    }
}
