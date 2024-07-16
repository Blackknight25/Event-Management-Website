/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Filter.java to edit this template
 */
package filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author choijiwon
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/event/*"})
public class AuthenticationFilter implements Filter {

    public AuthenticationFilter() {
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        if (session == null) {
            //did not attempt try to login before so there's no session
            //so confirmed not logged in
            ((HttpServletResponse) response).sendRedirect(req.getContextPath() + "/index.xhtml");
        } else {
            //check whether the userId in the session object is > 0
            Long userId = (Long) session.getAttribute("userId");

            if (userId == null || userId <= 0) {
                //not logged in
                ((HttpServletResponse) response).sendRedirect(req.getContextPath() + "/index.xhtml");
            } else {
                //means log in successfully
                chain.doFilter(request, response);

            }
        }
    }
}
