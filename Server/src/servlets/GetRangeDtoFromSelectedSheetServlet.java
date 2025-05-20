package servlets;

import com.google.gson.Gson;
import dto.range.RangeDto;
import engine.api.EngineManagerForServer;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import constants.Constants;
import utils.ServletUtils;
import java.io.IOException;

public class GetRangeDtoFromSelectedSheetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String selectedSheetName = request.getParameter(Constants.SHEET_NAME);
        String rangeName = request.getParameter(Constants.RANGE_NAME);

        try {
            EngineManagerForServer engineManager = ServletUtils.getEngineManager(getServletContext());
            RangeDto rangeDto = engineManager.getRangeDtoFromSelectedSheet(selectedSheetName, rangeName);

            response.setContentType("application/json");
            Gson gson = new Gson();
            String jsonResult = gson.toJson(rangeDto);
            response.getWriter().write(jsonResult);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error processing recent request: " + e.getMessage());
        }
    }
}
