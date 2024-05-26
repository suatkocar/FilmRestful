package util;

import java.util.List;

import models.Film;

/*
 * Utility class to convert Film objects to and from plain text format.
 * Implements the Singleton pattern to ensure that only one instance is used globally.
 */
public class TextConverter {

    private static TextConverter instance = null;

 // Private constructor to restrict instantiation.
    private TextConverter() {}

    /*
     * Provides a global access point to the single instance of the TextConverter,
     * with a thread-safe double-checked locking mechanism.
     */
    public static TextConverter getInstance() {
        if (instance == null) {
            synchronized (TextConverter.class) {
                if (instance == null) {
                    instance = new TextConverter();
                }
            }
        }
        return instance;
    }

    /*
     * Converts an object to its textual representation, handling both Film objects and lists of Films.
     * @param object The object to be converted to text.
     * @return A string representing the object in plain text.
     */
    public String convertToText(Object object) {
        if (object instanceof Film) {
            Film film = (Film) object;
            return String.format("Id: %d%nTitle: %s%nYear: %d%nDirector: %s%nStars: %s%nReview: %s%n",
                film.getId(),
                film.getTitle(),
                film.getYear(),
                film.getDirector(),
                film.getStars(),
                film.getReview());
        } else if (object instanceof List<?>) {
            StringBuilder sb = new StringBuilder();
            List<?> list = (List<?>) object;
            for (Object item : list) {
                sb.append(convertToText(item));
                sb.append("\n---\n");
            }
            return sb.toString(); // Trims to remove the last separator
        }
        return object.toString(); // Uses Object's toString method as a fallback
    }

    /*
     * Parses a plain text string to reconstruct a Film object.
     * @param text The textual representation of a Film.
     * @return A Film object parsed from provided text.
     */
    public Film convertFromText(String text) {
        Film film = new Film();
        String[] lines = text.split("\n");
        for (String line : lines) {
            int colonIndex = line.indexOf(':');
            if (colonIndex != -1) {
                String key = line.substring(0, colonIndex).trim().toLowerCase();
                String value = line.substring(colonIndex + 1).trim();
                switch(key) {
                    case "title":
                        film.setTitle(value);
                        break;
                    case "year":
                        try {
                            film.setYear(Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            System.err.println("Year parsing error: " + e.getMessage());
                        }
                        break;
                    case "director":
                        film.setDirector(value);
                        break;
                    case "stars":
                        film.setStars(value);
                        break;
                    case "review":
                        film.setReview(value);
                        break;
                }
            }
        }
        return film; // Return the populated film object
    }
}