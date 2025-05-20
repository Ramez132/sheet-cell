package servlets;

import com.google.gson.Gson;
import constants.Constants;
import dto.cell.CellDto;
import engine.api.EngineManagerForServer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;

import java.io.IOException;


public class GetVersionNumOfRecentSelectedSheetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String selectedSheetName = request.getParameter(Constants.SHEET_NAME);

        try {
            EngineManagerForServer engineManager = ServletUtils.getEngineManager(getServletContext());
            Integer recentVersionNum = engineManager.getVersionNumOfRecentSelectedSheet(selectedSheetName);

            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");

            String responseBody = recentVersionNum.toString();
            response.getWriter().write(responseBody);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error processing recent request: " + e.getMessage());
        }
    }
}
