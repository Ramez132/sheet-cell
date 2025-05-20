package servlets;

import com.google.gson.Gson;
import constants.Constants;
import dto.range.RangeDto;
import engine.api.EngineManagerForServer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;

import java.io.IOException;
import java.util.List;

public class GetAllRangesNamesForSelectedSheetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String selectedSheetName = request.getParameter(Constants.SHEET_NAME);

        try {
            EngineManagerForServer engineManager = ServletUtils.getEngineManager(getServletContext());
            List<String> allRangesNamesFromSelectedSheet = engineManager.getAllRangesNamesFromSelectedSheet(selectedSheetName);

            response.setContentType("application/json");
            Gson gson = new Gson();
            String jsonResult = gson.toJson(allRangesNamesFromSelectedSheet.toArray());
            response.getWriter().write(jsonResult);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error processing recent request: " + e.getMessage());
        }
    }
}
