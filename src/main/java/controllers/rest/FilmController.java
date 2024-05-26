package controllers.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Film;
import services.FilmService;
import util.RequestHelper;
import util.RequestParser;
import util.ResponseHandler;

/*
 * Servlet to handle API requests related to Films, providing methods
 * to insert, retrieve, update, and delete films from the database.
 * Exposes endpoints under '/filmapi/*'.
 */
@WebServlet(name = "FilmController", urlPatterns = {"/filmapi/*"})
public class FilmController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private FilmService filmService = new FilmService();
	private RequestHelper requestHelper = new RequestHelper();
	private ResponseHandler responseHandler = new ResponseHandler();
	private RequestParser requestParser = new RequestParser();

	/*
     * Handles the HTTP GET request method to search or list films.
     */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    System.out.println("GET request received at: " + request.getPathInfo());

	    try {
	        if (request.getPathInfo() != null && request.getPathInfo().contains("/search")) {
	            String query = request.getParameter("query");
	            String type = request.getParameter("type");
	            
	            List<Film> films;
	            if (query != null && !query.isEmpty()) {
	                if (type != null && !type.isEmpty()) {
	                    // Specified search on a field like title, year, director, stars.
	                    films = filmService.searchFilms(query, type);
	                } else {
	                    // General search across multiple fields
	                    films = filmService.searchFilmsGeneral(query);
	                }
	            } else {
	                // Return all films if no query is specified
	                films = filmService.getAllFilms();
	            }

	            if (films.isEmpty()) {
	                System.out.println("No films found.");
	                response.sendError(HttpServletResponse.SC_NOT_FOUND, "No films found.");
	            } else {
	                responseHandler.writeResponse(request, response, films);
	            }
	        } else {
	            // Handle other GET requests or direct to a not found handler
	            List<Film> films = filmService.getAllFilms();
	            responseHandler.writeResponse(request, response, films);
	        }
	    } catch (SQLException e) {
	        System.err.println("SQL Exception: " + e.getMessage());
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
	    } catch (Exception e) {
	        System.err.println("General Exception: " + e.getMessage());
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad request: " + e.getMessage());
	    } finally {
	        response.getWriter().flush();
	    }
	}

	
	/*
	 * Handles the HTTP POST request method to insert a new film.
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("POST request received: " + request.getPathInfo());
		try {
			Film film = requestParser.parseRequestBody(request, request.getContentType());
			if (film != null) {
				filmService.insertFilm(film);
				System.out.println("Film inserted: " + film.getId());
				response.setStatus(HttpServletResponse.SC_CREATED);
				responseHandler.writeResponse(request, response, Collections.singletonList(film));
			} else {
				System.out.println("Invalid film data received");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid film data");
			}
		} catch (SQLException e) {
			System.err.println("SQL Exception: " + e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("General Exception: " + e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error processing request: " + e.getMessage());
		}
	}

	
	/*
	 * Handles the HTTP PUT request method to update an existing film.
	 */
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("PUT request received: " + request.getPathInfo());
		String pathInfo = request.getPathInfo();

		if (pathInfo == null || pathInfo.equals("/")) {
			System.out.println("Invalid path info or missing film ID");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing film ID in the request path.");
			return;
		}

		// Path format is "/films/10001"
		String[] pathParts = pathInfo.split("/");
		if (pathParts.length < 3 || !pathParts[1].equals("films")) {
			System.out.println("Invalid path format");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid film ID path format.");
			return;
		}

		int id;
		try {
			id = Integer.parseInt(pathParts[2]);
		} catch (NumberFormatException e) {
			System.err.println("Number Format Exception for film ID: " + e.getMessage());
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid film ID format");
			return;
		}

		String contentType = request.getContentType();
		if (contentType == null
				|| (!contentType.contains("application/json") && !contentType.contains("application/xml")
						&& !contentType.contains("text/plain") && !contentType.contains("application/x-yaml"))) {
			System.out.println("Unsupported Content-Type: " + contentType);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported Content-Type: " + contentType);
			return;
		}

		try {
			Film film = requestParser.parseRequestBody(request, contentType);
			if (film != null) {
				film.setId(id);
				filmService.updateFilm(film);
				System.out.println("Film updated: " + film.getId());
				response.setStatus(HttpServletResponse.SC_OK);
				responseHandler.writeResponse(request, response, Collections.singletonList(film));
			} else {
				System.out.println("Invalid film data received");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid film data");
			}
		} catch (SQLException e) {
			System.err.println("SQL Exception: " + e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("General Exception: " + e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error processing request: " + e.getMessage());
		}
	}
	
	/*
	 * Handles the HTTP DELETE request method to delete an existing film.
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("DELETE request received: " + request.getPathInfo());
		String pathInfo = request.getPathInfo();

		if (pathInfo == null || !pathInfo.startsWith("/")) {
			System.out.println("Invalid path info or missing film ID");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing film ID in the request path.");
			return;
		}

		String[] pathParts = pathInfo.split("/");
		if (pathParts.length < 3 || !pathParts[1].equals("films")) {
			System.out.println("Invalid path format");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid film ID path format.");
			return;
		}

		int id;
		try {
			id = Integer.parseInt(pathParts[2]);
		} catch (NumberFormatException e) {
			System.err.println("Number Format Exception for film ID: " + e.getMessage());
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid film ID format");
			return;
		}

		try {
			boolean deleted = filmService.deleteFilm(id);
			if (deleted) {
				System.out.println("Film deleted successfully with ID: " + id);
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
				String format = responseHandler.determineFormat(request);
				response.setContentType(responseHandler.getResponseType(format) + "; charset=UTF-8");
				Map<String, Object> result = new HashMap<>();
				result.put("message", "Film deleted successfully.");
				responseHandler.writeResponse(request, response, result);
			} else {
				System.out.println("No film found with ID: " + id);
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "No film found with specified ID");
			}
		} catch (SQLException e) {
			System.err.println("SQL Exception during film deletion: " + e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("General Exception: " + e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error processing request: " + e.getMessage());
		}
	}
}