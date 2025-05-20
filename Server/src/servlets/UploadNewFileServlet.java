package servlets;

import com.google.gson.Gson;
import engine.api.EngineManagerForServer;
import dto.management.info.SheetBasicInfoDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import utils.ServletUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@MultipartConfig
public class UploadNewFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/plain;charset=UTF-8");
        Part filePart = request.getPart("file"); // "file" should match the form data part name from the client

        // Save the uploaded file temporarily on the server
        File tempFile = File.createTempFile("uploadedFile", ".xml");
        try (InputStream fileContent = filePart.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileContent.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        String username = ServletUtils.getUsername(request);
        EngineManagerForServer engineManager = ServletUtils.getEngineManager(getServletContext());

        try {
            // Pass the temp file to the engine
            SheetBasicInfoDto result = engineManager.tryToExtractSheetFromFileAndReturnBasicInfo(tempFile, username);

            // Clean up temporary file

            // Send the result back to the client
            response.setContentType("application/json");
            Gson gson = new Gson();
            String jsonResult = gson.toJson(result);
            response.getWriter().write(jsonResult);
        }
        catch (Exception e) {
            // Clean up temporary file
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error processing the file: " + e.getMessage());
        }
        finally {
            tempFile.delete();
        }
    }

}

