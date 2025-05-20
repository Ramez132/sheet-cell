package servlets;

import com.google.gson.Gson;
import dto.cell.CellDto;
import engine.api.EngineManagerForServer;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import constants.Constants;
import utils.ServletUtils;
import java.io.IOException;


public class GetCellDtoFromRecentSelectedSheetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String selectedSheetName = request.getParameter(Constants.SHEET_NAME);
        String rowNumberStr = request.getParameter(Constants.ROW_NUMBER);
        int rowNumber = Integer.parseInt(rowNumberStr);
        String columnNumberStr = request.getParameter(Constants.COLUMN_NUMBER);
        int columnNumber = Integer.parseInt(columnNumberStr);

        try {
            EngineManagerForServer engineManager = ServletUtils.getEngineManager(getServletContext());
            CellDto cellDTO = engineManager.getCellDtoFromRecentSelectedSheet(selectedSheetName, rowNumber, columnNumber);

            response.setContentType("application/json");
            Gson gson = new Gson();
            String jsonResult = gson.toJson(cellDTO);
            response.getWriter().write(jsonResult);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error processing recent request: " + e.getMessage());
        }
    }
}
