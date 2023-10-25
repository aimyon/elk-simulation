package com.sim.elkSim.websocket;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Component
public class CorsFilter implements Filter {
    private final List<String> allowOrigins = Arrays.asList("http://localhost:8080");

    public void destroy() {

    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (req instanceof HttpServletRequest && res instanceof HttpServletResponse) {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            String origin = request.getHeader("origin");
            response.setHeader("Access-Control-Allow-Origin", allowOrigins.contains(origin) ? origin : "*");
            response.setHeader("Vary", "Origin");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,OPTIONS,DELETE");
            response.setHeader("Access-Control-Allow-Headers", "Origin,X-Requested-With, Content-Type, Accept, X-CSRF-TOKEN");
        }
        chain.doFilter(req, res);
    }

    public void init(FilterConfig filterConfig) {

    }
}
