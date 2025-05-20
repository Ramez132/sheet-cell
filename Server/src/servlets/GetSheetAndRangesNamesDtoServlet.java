package servlets;

import com.google.gson.Gson;
import dto.management.info.SheetAndRangesNamesDto;
import engine.api.EngineManagerForServer;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import dto.sheet.SheetDto;

import constants.Constants;
import utils.ServletUtils;
import java.io.IOException;
import java.util.List;


public class GetSheetAndRangesNamesDtoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String selectedSheetName = request.getParameter(Constants.SHEET_NAME);
        String userName = request.getParameter(Constants.USERNAME);

        try {
        EngineManagerForServer engineManager = ServletUtils.getEngineManager(getServletContext());
        SheetDto sheetDTO = engineManager.getLastVersionSheetDto(selectedSheetName);
        List<String> rangesNames = engineManager.getAllRangeNamesForSelectedSheet(selectedSheetName);
        SheetAndRangesNamesDto sheetAndRangesNamesDto = new SheetAndRangesNamesDto(sheetDTO, rangesNames);

        String permissionLevelForUser = engineManager.getPermissionLevelForSelectedUserAndSheet(selectedSheetName, userName);
        response.setHeader(Constants.CURRENT_PERMISSION_LEVEL, permissionLevelForUser);

        response.setContentType("application/json");
        Gson gson = new Gson();
        String jsonResult = gson.toJson(sheetAndRangesNamesDto);
        response.getWriter().write(jsonResult);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error processing requested info of sheet " + selectedSheetName +" : " + e.getMessage());
        }
    }
}
