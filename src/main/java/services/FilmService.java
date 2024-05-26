package services;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import dao.FilmDAO;
import models.Film;

/*
 * A service class for handling business logic related to film operations.
 * It integrates with the FilmDAO for database operations.
 */
public class FilmService {
	private FilmDAO filmDAO = new FilmDAO();

    /*
     * Conducts a general search for films based on a query that matches across multiple fields.
     */
	public List<Film> searchFilmsGeneral(String query) throws SQLException {
		return filmDAO.searchFilmsGeneral(query);
	}

    /*
     * Searches films based on specific fields provided through a map of parameters.
     */
	public List<Film> searchFilms(Map<String, String> searchParams) throws SQLException {
        return filmDAO.searchFilms(searchParams);
    }
	
    /*
     * Searches for films based on a specific field, defined by 'searchType'.
     */
	public List<Film> searchFilms(String searchQuery, String searchType) throws SQLException {
        return filmDAO.searchFilms(searchQuery, searchType);
    }

    /*
     * Determines the type of film retrieval required based on the request path and parameters, and executes the appropriate search or retrieval operation.
     */
	public List<Film> handleFilmRetrieval(String pathInfo, HttpServletRequest request) throws SQLException {
		Map<String, String> searchParams = collectSearchParams(request);

		if (pathInfo != null) {
			if (pathInfo.startsWith("/search")) {
				
				String searchQuery = request.getParameter("query");
				if (searchQuery != null && !searchQuery.isEmpty()) {
					return searchFilmsGeneral(searchQuery);
				} else {
				
					return searchFilms(searchParams);
				}
			} else if (pathInfo.startsWith("/films/")) {
				
				return searchFilms(searchParams);
			}
		}

		return filmDAO.getAllFilms();
	}

	/*
     * Collects search parameters from the request and builds a map of these parameters.
     * These include the id, title, director, year, and stars of a film.
     */
	public Map<String, String> collectSearchParams(HttpServletRequest request) {
		Map<String, String> params = new HashMap<>();
		String[] searchKeys = { "title", "director", "year", "stars", "id" };
		for (String key : searchKeys) {
			String value = request.getParameter(key);
			if (value != null) {
				params.put(key, value);
			}
		}
		return params;
	}

    /*
     * Retrieves all films from the database.
     */
	public List<Film> getAllFilms() throws SQLException {
        return filmDAO.getAllFilms();
    }

    /*
     * Inserts a new film into the database.
     */
	public void insertFilm(Film film) throws SQLException {
		filmDAO.insertFilm(film);
	}

    /*
     * Updates an existing film in the database.
     */
	public void updateFilm(Film film) throws SQLException {
		filmDAO.updateFilm(film);
	}

    /*
     * Deletes a film from the database based on the film ID.
     */
	public boolean deleteFilm(int id) throws SQLException {
		return filmDAO.deleteFilm(id);
	}
	
    /*
     * Retrieves films in a paginated manner based on the page number and size.
     */
	public List<Film> getFilmsPaginated(int page, int pageSize) throws SQLException {
	    return filmDAO.getFilmsPaginated(page, pageSize);
	}
}