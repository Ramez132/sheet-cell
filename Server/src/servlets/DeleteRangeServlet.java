package servlets;

import com.google.gson.Gson;
import dto.cell.CellDto;
import engine.api.EngineManagerForServer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import constants.Constants;
import utils.ServletUtils;

import java.io.IOException;

public class DeleteRangeServlet extends HttpServlet {

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sheetName = request.getParameter(Constants.SHEET_NAME);
        String rangeName = request.getParameter(Constants.RANGE_NAME);
        try {
            EngineManagerForServer engineManager = ServletUtils.getEngineManager(getServletContext());
            if  (engineManager.isSelectedRangeUsedInAnyCellWithRelevantFunction(sheetName, rangeName)) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write("The range " + rangeName + " is used in a cell/cells with relevant functions - can not delete it.");
                return;
            }
            engineManager.deleteRangeFromSelectedSheet(sheetName, rangeName);

            response.setStatus(HttpServletResponse.SC_NO_CONTENT);  // Set 204 No Content status code
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error processing recent request: " + e.getMessage());
        }
    }
}
