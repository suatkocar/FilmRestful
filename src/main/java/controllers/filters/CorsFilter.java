package controllers.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * Filter for managing Cross-Origin Resource Sharing (CORS),
 * allowing or restricting cross-origin requests in a Java web application.
 */
@WebFilter(filterName = "CorsFilter", urlPatterns = "/*")
public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Method called when the filter is initially created - typically used for any setup or configuration.
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Sets the CORS policy on the response headers.
        response.setHeader("Access-Control-Allow-Origin", "*"); // Allows requests from any origin.
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, X-Custom-Format");

        // Handle pre-flight requests (OPTIONS) by returning appropriate headers and a 200 status code.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Continues the filter chain for non-OPTIONS requests.
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {
        // Method used for cleanup activities, e.g., releasing resources or logging finalisation.
    }
}