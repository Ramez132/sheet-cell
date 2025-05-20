package servlets;

import com.google.gson.Gson;
import constants.Constants;
import dto.management.info.SheetAndRangesNamesDto;
import dto.sheet.SheetDto;
import engine.api.EngineManagerForServer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;

import java.io.IOException;
import java.util.List;


public class GetSheetOfSpecificVersionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String selectedSheetName = request.getParameter(Constants.SHEET_NAME);
        String versionNumber = request.getParameter(Constants.VERSION_NUMBER);
        int versionNum = Integer.parseInt(versionNumber);

        try {
        EngineManagerForServer engineManager = ServletUtils.getEngineManager(getServletContext());
        SheetDto sheetDTO = engineManager.getSheetOfSpecificVersion(selectedSheetName, versionNum);

        response.setContentType("application/json");
        Gson gson = new Gson();
        String jsonResult = gson.toJson(sheetDTO);
        response.getWriter().write(jsonResult);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error trying to get version number" + versionNumber + " : " + e.getMessage());
        }
    }
}
