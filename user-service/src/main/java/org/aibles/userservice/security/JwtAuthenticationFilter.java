package org.aibles.userservice.security;

import lombok.RequiredArgsConstructor;
import org.aibles.userservice.exception.JwtAuthenticationException;
import org.aibles.userservice.exception.LoginException;
import org.aibles.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = jwtProvider.getToken(servletRequest);
        try {
            if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {
                Authentication authentication = jwtProvider.getAuthentication(token);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                filterChain.doFilter(servletRequest, servletResponse);
            }
            else {
                SecurityContextHolder.clearContext();
                servletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            }
        } catch (JwtAuthenticationException e) {;
            SecurityContextHolder.clearContext();
            servletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }

}
