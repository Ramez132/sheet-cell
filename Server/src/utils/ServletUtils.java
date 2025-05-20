package utils;

import engine.api.EngineManagerForServer;
import engine.impl.EngineManagerForServerImpl;
import engine.users.UserManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class ServletUtils {

	private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
	private static final String ENGINE_MANAGER_ATTRIBUTE_NAME = "engineManager";

	/*
	Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
	the actual fetch of them is remained un-synchronized for performance POV
	 */
	private static final Object userManagerLock = new Object();
	private static final Object engineManagerLock = new Object();

	public static UserManager getUserManager(ServletContext servletContext) {

		synchronized (userManagerLock) {
			if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
			}
		}
		return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
	}

	public static EngineManagerForServer getEngineManager(ServletContext servletContext) {
		synchronized (engineManagerLock) {
			if (servletContext.getAttribute(ENGINE_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(ENGINE_MANAGER_ATTRIBUTE_NAME, new EngineManagerForServerImpl());
			}
		}
		return (EngineManagerForServer) servletContext.getAttribute(ENGINE_MANAGER_ATTRIBUTE_NAME);
	}

	public static String getUsername(HttpServletRequest request) {
		return SessionUtils.getUsername(request);
	}

	public static String getRequestBodyAsString(HttpServletRequest request) throws IOException {
		// Read the body of the request
		StringBuilder stringBuilder = new StringBuilder();
		String line;

		// BufferedReader to read the InputStream
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
			boolean firstLine = true;
			while ((line = reader.readLine()) != null) {
				// Append a newline only if it's not the first line
				if (!firstLine) {
					stringBuilder.append("\n");
				}
				stringBuilder.append(line);
				firstLine = false;
			}
		}

		// Get the body as a string
		return stringBuilder.toString();
	}
}
