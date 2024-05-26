package util;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import models.Film;
import models.Films;

/*
 * Utility class for parsing the request bodies and converting content between formats
 * depending on the content type or desired response format specified.
 */
public class RequestParser {

    /*
     * Parses the request body based on its content type and converts it to a Film object.
     * @param request The HTTP Servlet request from which to read the request body.
     * @param contentType The content type of the request, used to determine the parsing strategy.
     * @return A Film object parsed from the request body, or null if parsing fails.
     * @throws IOException If there is an error reading from the request.
     */
    public Film parseRequestBody(HttpServletRequest request, String contentType) throws IOException {
    	// Read the entire request body into a single string.
    	String requestData = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        System.out.println("Received request data: " + requestData);

        if (contentType.contains("application/json")) {
            try {
            	// Parse JSON to a Film object.
                Film film = JsonConverter.getInstance().convertFromJson(requestData, Film.class);
                if (film == null) {
                    System.err.println("Failed to parse JSON data into Film object");
                }
                return film;
            } catch (Exception e) {
                System.err.println("JSON parsing error: " + e.getMessage());
            }
        } else if (contentType.contains("application/xml")) {
        	// Parse XML to a Film object.
            return XmlConverter.getInstance().convertFromXml(requestData, Film.class);
        } else if (contentType.contains("text/plain")) {
        	// Convert plain text to a Film object.
            return TextConverter.getInstance().convertFromText(requestData);
        } else if (contentType.contains("application/x-yaml")) {
        	// Convert YAML to a Film object.
            return YamlConverter.getInstance().convertFromYaml(requestData);
        }
        return null;
    }

    /*
     * Serialises the provided data into the specified format.
     * @param data The data object to be serialised.
     * @param format The desired output format ("json", "xml", "text", "yaml").
     * @return A string representation of the data in the requested format.
     */
    public String serializeData(Object data, String format) {
        switch (format) {
            case "xml":
                return XmlConverter.getInstance().convertToXml(data instanceof List ? new Films((List<Film>) data) : data);
            case "text":
                return TextConverter.getInstance().convertToText(data);
            case "yaml":
                return YamlConverter.getInstance().convertToYaml(data);
            default:
                return JsonConverter.getInstance().convertToJson(data);
        }
    }
}