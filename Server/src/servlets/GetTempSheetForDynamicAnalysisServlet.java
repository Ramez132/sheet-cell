package servlets;

import com.google.gson.Gson;
import constants.Constants;
import dto.sheet.SheetDto;
import engine.api.EngineManagerForServer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;

import java.io.IOException;

public class GetTempSheetForDynamicAnalysisServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String selectedSheetName = request.getParameter(Constants.SHEET_NAME);
        String username = ServletUtils.getUsername(request);
        int rowNumber = Integer.parseInt(request.getParameter(Constants.ROW_NUMBER));
        int columnNumber = Integer.parseInt(request.getParameter(Constants.COLUMN_NUMBER));
        String newOriginalValueStr = ServletUtils.getRequestBodyAsString(request);

        try {
            EngineManagerForServer engineManager = ServletUtils.getEngineManager(getServletContext());
            SheetDto sheetDTO = engineManager.getTempSheetForDynamicAnalysis
                    (selectedSheetName, rowNumber, columnNumber, newOriginalValueStr, username);

            response.setContentType("application/json");
            Gson gson = new Gson();
            String jsonResult = gson.toJson(sheetDTO);
            response.getWriter().write(jsonResult);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error trying to get the temp sheet for dynamic analysis: " + e.getMessage());
        }
    }
}
