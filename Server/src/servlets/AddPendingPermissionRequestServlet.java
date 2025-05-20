package servlets;

import constants.Constants;
import engine.api.EngineManagerForServer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;

import java.io.IOException;

public class AddPendingPermissionRequestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sheetName = request.getParameter(Constants.SHEET_NAME);
        String username = request.getParameter(Constants.USERNAME);
        String permissionLevelRequested = request.getParameter(Constants.PERMISSION_LEVEL_REQUESTED);

        try {
            EngineManagerForServer engineManager = ServletUtils.getEngineManager(getServletContext());
            engineManager.addNewPendingPermissionRequest(sheetName, username, permissionLevelRequested.toUpperCase());

            response.setStatus(HttpServletResponse.SC_CREATED);  // Set 201 Created status code
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error processing recent request: " + e.getMessage());
        }
    }
}
