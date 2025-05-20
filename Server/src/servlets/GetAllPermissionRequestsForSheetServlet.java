package servlets;

import com.google.gson.Gson;
import constants.Constants;
import dto.permission.PermissionRequestDto;
import engine.api.EngineManagerForServer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;

import java.io.IOException;
import java.util.List;

public class GetAllPermissionRequestsForSheetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sheetName = request.getParameter(Constants.SHEET_NAME);

        try {
            EngineManagerForServer engineManager = ServletUtils.getEngineManager(getServletContext());
            List<PermissionRequestDto> allPermissionsRequestsForSheet = engineManager.getPendingAndDecidedPermissionRequestsDtoList(sheetName);
            int numOfPendingPermissionRequests = engineManager.getNumOfPendingPermissionRequestsForSelectedSheet(sheetName);

            response.setHeader(Constants.NUM_OF_ALL_PERMISSION_REQUESTS_FOR_SELECTED_SHEET, String.valueOf(allPermissionsRequestsForSheet.size()));
            response.setHeader(Constants.NUM_OF_PENDING_PERMISSION_REQUESTS_FOR_SELECTED_SHEET, String.valueOf(numOfPendingPermissionRequests));

            response.setContentType("application/json");
            Gson gson = new Gson();
            String jsonResult = gson.toJson(allPermissionsRequestsForSheet.toArray());
            response.getWriter().write(jsonResult);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error trying to get all permissions requests for sheet " + sheetName + ": " + e.getMessage());
        }
    }
}
