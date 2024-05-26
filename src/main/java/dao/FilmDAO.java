package dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jakarta.xml.bind.annotation.XmlRootElement;
import models.Film;

/*
 * DAO class for managing database operations for the Film entities.
 * Provides functionality to connect to a database and perform CRUD operations.
 */
@XmlRootElement
public class FilmDAO {
	 private String jdbcUrl;
	 private String jdbcUser;
	 private String jdbcPassword;
	
    /*
     * Initialises the JDBC driver.
     */
	public FilmDAO() {
		 try {
	            Properties props = new Properties();
	            InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
	            props.load(input);
	            this.jdbcUrl = props.getProperty("jdbcUrl");
	            this.jdbcUser = props.getProperty("jdbcUser");
	            this.jdbcPassword = props.getProperty("jdbcPassword");
	            Class.forName("com.mysql.cj.jdbc.Driver");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    public Connection getConnection() throws SQLException {
	        return DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
	    }

	private Film getNextFilm(ResultSet rs) throws SQLException {
		return new Film(rs.getInt("id"), rs.getString("title"), rs.getInt("year"), rs.getString("director"),
				rs.getString("stars"), rs.getString("review"));
	}

    /*
     * Retrieves all films from the database and returns them as a list.
     */
	public ArrayList<Film> getAllFilms() throws SQLException {
		String sql = "SELECT * FROM films";
		ArrayList<Film> films = new ArrayList<>();
		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				films.add(getNextFilm(rs));
			}
		} catch (SQLException e) {
			System.err.println("SQL Error: " + e.getMessage());
			throw e;
		}
		return films;
	}

    /*
     * Retrieves a single film by its ID from the database.
     */
	public Film getFilmByID(int id) throws SQLException {
		String sql = "SELECT * FROM films WHERE id = ?";
		Film film = null;
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, id);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					film = getNextFilm(rs);
				}
			}
		} catch (SQLException e) {
			System.err.println("SQL Error: " + e.getMessage());
			throw e;
		}
		return film;
	}

    /*
     * Inserts a film into the database based on a Film object.
     */
	public void insertFilm(Film film) throws SQLException {
		String sql = "INSERT INTO films (title, year, director, stars, review) VALUES (?, ?, ?, ?, ?)";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, film.getTitle());
			pstmt.setInt(2, film.getYear());
			pstmt.setString(3, film.getDirector());
			pstmt.setString(4, film.getStars());
			pstmt.setString(5, film.getReview());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Insert Error: " + e.getMessage());
			throw e;
		}
	}

    /*
     * Updates an existing film in the database.
     */
	public void updateFilm(Film film) throws SQLException {
		String sql = "UPDATE films SET title = ?, year = ?, director = ?, stars = ?, review = ? WHERE id = ?";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, film.getTitle());
			pstmt.setInt(2, film.getYear());
			pstmt.setString(3, film.getDirector());
			pstmt.setString(4, film.getStars());
			pstmt.setString(5, film.getReview());
			pstmt.setInt(6, film.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Update Error: " + e.getMessage());
			throw e;
		}
	}

    /*
     * Deletes a film from the database by ID.
     */
	public boolean deleteFilm(int id) throws SQLException {
		String sql = "DELETE FROM films WHERE id = ?";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, id);
			int affectedRows = pstmt.executeUpdate();
			return affectedRows > 0;
		} catch (SQLException e) {
			System.err.println("Delete Error: " + e.getMessage());
			throw e;
		}
	}

    /*
     * Searches films by various attributes using a map of search criteria.
     */
	public List<Film> searchFilms(Map<String, String> searchParams) {
	    List<Film> films = new ArrayList<>();
	    StringBuilder sql = new StringBuilder("SELECT * FROM films WHERE 1=1");

	    searchParams.forEach((key, value) -> {
	        if (!value.isEmpty()) {
	            sql.append(" AND ").append(key).append(" LIKE ?");
	        }
	    });

	    try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
	        int index = 1;
	        for (String key : searchParams.keySet()) {
	            if (!searchParams.get(key).isEmpty()) {
	                pstmt.setString(index++, "%" + searchParams.get(key) + "%");
	            }
	        }
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            films.add(getNextFilm(rs));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    }
	    return films;
	}

	
    /*
     * Searches for films using a general query that matches multiple fields.
     */
	public List<Film> searchFilmsGeneral(String query) throws SQLException {
	    List<Film> films = new ArrayList<>();
	    String sql = "SELECT * FROM films WHERE id LIKE ? or title LIKE ? OR director LIKE ? OR stars LIKE ? OR CAST(year AS CHAR) LIKE ?";

	    try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        String searchQuery = "%" + query + "%";
	        pstmt.setString(1, searchQuery);
	        pstmt.setString(2, searchQuery);
	        pstmt.setString(3, searchQuery);
	        pstmt.setString(4, searchQuery);
	        pstmt.setString(5, searchQuery);

	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            films.add(getNextFilm(rs));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    }
	    return films;
	}
	
	/*
	 * Searches for films using a specific query type and value.
	 */
    public List<Film> searchFilms(String searchQuery, String searchType) throws SQLException {
        List<Film> films = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM films");

        switch (searchType) {
            case "All":
                sql.append(" WHERE id LIKE ? OR title LIKE ? OR director LIKE ? OR stars LIKE ? OR CAST(year AS CHAR) LIKE ?");
                break;
            case "ID":
                sql.append(" WHERE id = ?");
                break;
            case "Title":
                sql.append(" WHERE title LIKE ?");
                break;
            case "Director":
                sql.append(" WHERE director LIKE ?");
                break;
            case "Year":
                sql.append(" WHERE year = ?");
                break;
            case "Stars":
                sql.append(" WHERE stars LIKE ?");
                break;
            default:
                return getAllFilms();
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            if (searchType.equals("All")) {
                String wildcardQuery = "%" + searchQuery + "%";
                pstmt.setString(1, wildcardQuery);
                pstmt.setString(2, wildcardQuery);
                pstmt.setString(3, wildcardQuery);
                pstmt.setString(4, wildcardQuery);
                pstmt.setString(5, wildcardQuery);
            } else {
                if ("Year".equals(searchType) || "ID".equals(searchType)) {
                    pstmt.setInt(1, Integer.parseInt(searchQuery));
                } else {
                    pstmt.setString(1, "%" + searchQuery + "%");
                }
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Film film = getNextFilm(rs);
                    films.add(film);
                }
            }
        }

        return films;
    }

	/*
	 * Retrieves a paginated list of films from the database.
	 */
	public List<Film> getFilmsPaginated(int page, int pageSize) throws SQLException {
	    List<Film> films = new ArrayList<>();
	    String sql = "SELECT * FROM films LIMIT ?, ?";
	    int offset = (page - 1) * pageSize;

	    try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, offset);
	        pstmt.setInt(2, pageSize);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                films.add(getNextFilm(rs));
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("SQL Error: " + e.getMessage());
	        throw e;
	    }
	    return films;
	}

}