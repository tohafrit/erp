package ru.korundm.configuration.filter;

import ru.korundm.helper.LoggedUser;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;

/**
 * Filter для работы с Principal
 * @author pakhunov_an
 * Date:   06.02.2019
 */
public final class LoggedUserFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        Principal principal = httpServletRequest.getUserPrincipal();
        if (principal != null) {
            LoggedUser.logIn(principal.getName());
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            LoggedUser.logOut();
        }
    }
}