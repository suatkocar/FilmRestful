package util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/*
 * Utility class for processing and extracting parameters from HttpServletRequest objects,
 * particularly useful for querying based on user inputs.
 */
public class RequestHelper {

    /*
     * Extracts search parameters from the HttpServletRequest based on pre-defined keys.
     * Only includes parameters that are not null and not blank to the returned map.
     *
     * @param request The HttpServletRequest from which to extract parameters.
     * @return A map containing the viable search keys and their corresponding values.
     */
    public Map<String, String> extractSearchParams(HttpServletRequest request) {
        Map<String, String> searchParams = new HashMap<>();
        String[] searchKeys = { "title", "director", "year", "stars", "review" };
        for (String key : searchKeys) {
            String value = request.getParameter(key);
            if (value != null && !value.isBlank()) { // Checks to ensure that the parameter contains some data
                searchParams.put(key, value.trim()); // Trims whitespace and add to map
            }
        }
        return searchParams;
    }
}