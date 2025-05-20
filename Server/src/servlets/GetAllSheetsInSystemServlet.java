package servlets;

import com.google.gson.Gson;
import dto.management.info.SheetBasicInfoDto;
import engine.api.EngineManagerForServer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import constants.Constants;

import java.io.IOException;
import java.util.List;

public class GetAllSheetsInSystemServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = ServletUtils.getUsername(request);

        try {
            EngineManagerForServer engineManager = ServletUtils.getEngineManager(getServletContext());
            List<SheetBasicInfoDto> dataOfAllSheetsInSystem = engineManager.getDataOfAllSheetsInSystem(username);
            boolean wasThereAChangeInPermissions = engineManager.wasThereAChangeInSelectedUserPermissions(username);

            response.setHeader(Constants.NUM_OF_SHEETS_IN_SERVER, String.valueOf(dataOfAllSheetsInSystem.size()));
            response.setHeader(Constants.WAS_THERE_A_CHANGE_IN_PERMISSIONS, String.valueOf(wasThereAChangeInPermissions));

            response.setContentType("application/json");
            Gson gson = new Gson();
            String jsonResult = gson.toJson(dataOfAllSheetsInSystem.toArray());
            response.getWriter().write(jsonResult);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error trying to get all sheets in system: " + e.getMessage());
        }
    }
}
