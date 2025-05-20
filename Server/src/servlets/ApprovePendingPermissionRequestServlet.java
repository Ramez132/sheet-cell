package servlets;

import constants.Constants;
import engine.api.EngineManagerForServer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;

import java.io.IOException;

public class ApprovePendingPermissionRequestServlet extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sheetName = request.getParameter(Constants.SHEET_NAME);
        String username = request.getParameter(Constants.USERNAME);
        String permissionLevelRequested = request.getParameter(Constants.PERMISSION_LEVEL_REQUESTED);
        int numOfRequestForSheet = Integer.parseInt(request.getParameter(Constants.NUM_OF_REQUEST_FOR_SHEET));

        try {
            EngineManagerForServer engineManager = ServletUtils.getEngineManager(getServletContext());
            engineManager.approveSelectedPermissionRequest(sheetName, username, permissionLevelRequested.toUpperCase(), numOfRequestForSheet);

            response.setStatus(HttpServletResponse.SC_OK);  // Set 200 OK status code
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error processing recent request: " + e.getMessage());
        }
    }
}
