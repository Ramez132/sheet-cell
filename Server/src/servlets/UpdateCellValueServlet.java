package servlets;

import com.google.gson.Gson;
import dto.sheet.SheetDto;
import engine.api.EngineManagerForServer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;

import java.io.*;

import constants.Constants;

import static constants.Constants.CURRENT_VERSION_NUM_IN_UPDATING_CLIENT;

public class UpdateCellValueServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/plain;charset=UTF-8");

        String username = ServletUtils.getUsername(request);
        String sheetName = request.getParameter(Constants.SHEET_NAME);
        int currentVersionNumInUpdatingClient = Integer.parseInt(request.getParameter(CURRENT_VERSION_NUM_IN_UPDATING_CLIENT));
        int rowNumber = Integer.parseInt(request.getParameter(Constants.ROW_NUMBER));
        int columnNumber = Integer.parseInt(request.getParameter(Constants.COLUMN_NUMBER));
        String newOriginalValueStr = ServletUtils.getRequestBodyAsString(request);

        EngineManagerForServer engineManager = ServletUtils.getEngineManager(getServletContext());

        try {
            // Pass the temp file to the engine
            SheetDto result = engineManager.updateCellValueInSelectedSheet
                            (sheetName, rowNumber, columnNumber, newOriginalValueStr,
                                username, currentVersionNumInUpdatingClient);

            // Send the result back to the client
            response.setContentType("application/json");
            Gson gson = new Gson();
            String jsonResult = gson.toJson(result);
            response.getWriter().write(jsonResult);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error: " + e.getMessage());
        }

    }
}
