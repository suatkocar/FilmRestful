package util;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Film;
import models.Films;

/*
 * Utility class that handles responses for servlet, managing serialisation based on request headers and parameters,
 * and writing the formatted response to HttpServletResponse.
 */
public class ResponseHandler {

    /*
     * Writes a serialised data response to the HttpServletResponse object,
     * formatting data based on the request's indicated preference (format parameter or Accept header).
     * Defaults to JSON if no format is specified.
     * @param request The HttpServletRequest which may contain format specification.
     * @param response The HttpServletResponse to which the data is to be written.
     * @param data The data object that needs to be serialised and written to the response.
     * @throws IOException If there is an error writing the response.
     */
    public void writeResponse(HttpServletRequest request, HttpServletResponse response, Object data)
            throws IOException {
        String format = determineFormat(request);
        String serializedData = serializeData(data, format);
        response.setContentType(getResponseType(format) + "; charset=UTF-8");
        response.getWriter().write(serializedData);
        System.out.println("Response written: " + serializedData); // Log response data
    }

    /*
     * Determines the desired response format based on URL parameters or the "Accept" header.
     * Defaults to JSON if no format is specified.
     * @param request The HttpServletRequest which may contain format specification.
     * @return The identified format as a String.
     */
    public String determineFormat(HttpServletRequest request) {
        String formatParam = request.getParameter("format");
        if (formatParam != null) {
            return formatParam;
        }
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null) {
            if (acceptHeader.contains("application/xml")) {
                return "xml";
            } else if (acceptHeader.contains("text/plain")) {
                return "text";
            } else if (acceptHeader.contains("application/x-yaml")) {
                return "yaml";
            }
        }
        return "json"; // Default to JSON if no format is specified or recognised
    }

    /*
     * Returns the MIME type associated with the specified format, for use in setting the Content-Type header.
     * @param format The desired response format.
     * @return The Content-Type MIME string corresponding to the format.
     */
    public String getResponseType(String format) {
        switch (format) {
        case "xml":
            return "application/xml";
        case "text":
            return "text/plain";
        case "yaml":
            return "application/x-yaml";
        default:
            return "application/json";
        }
    }

    /*
     * Serialises the given data to the specified format, handling conversion exceptions.
     * @param data The data to be serialised (can be a list or a single object).
     * @param format The serialisation format ("xml", "json", "text", "yaml").
     * @return A string of the serialised data.
     */
    @SuppressWarnings("unchecked")
	private String serializeData(Object data, String format) {
        try {
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
        } catch (Exception e) {
            System.err.println("Serialization error for format " + format + ": " + e);
            return null;
        }
    }
}