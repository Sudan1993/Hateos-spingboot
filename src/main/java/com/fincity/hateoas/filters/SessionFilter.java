package com.fincity.hateoas.filters;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SessionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    /**
     * check for session storage key present
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String sessionToken = (String) httpServletRequest.getSession().getAttribute("token");
        if(sessionToken == null) {
            httpServletResponse.sendRedirect("/register");
        }
        chain.doFilter(request, response);
    }

    /**
     * invalidate the session token
     */
    @Override
    public void destroy() {}
}
