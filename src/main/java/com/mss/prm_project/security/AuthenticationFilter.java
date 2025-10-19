package com.mss.prm_project.security;

import com.mss.prm_project.service.JwtService;
import com.mss.prm_project.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.debug("AUTH-FILTER uri={} servletPath={} bypass={}",
                request.getRequestURI(), request.getServletPath(), isBypassToken(request));

        if (isBypassToken(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = resolveBearerToken(request);

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (redisService.isRevoked(jwt)) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "unauthorized", "Token has been revoked");
            log.warn("Rejected revoked access token");
            return;
        }

        try {
            Date exp = jwtService.extractExpiration(jwt);
            if (exp == null || exp.before(new Date())) {
                writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "unauthorized", "Token is expired");
                return;
            }
        } catch (Exception e) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "unauthorized", "Invalid token");
            return;
        }

        try {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String email = jwtService.extractEmail(jwt);
                if (email != null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "unauthorized", "Invalid token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isBypassToken(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/api/auth/")
                || uri.startsWith("/swagger-ui/")
                || uri.startsWith("/v3/api-docs")
                || uri.equals("/swagger-ui.html");
    }

    private String resolveBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null) return null;
        String h = header.trim();
        if (h.length() >= 7 && h.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return h.substring(7).trim();
        }
        return null;
    }

    private void writeJsonError(HttpServletResponse response, int status, String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + error + "\",\"message\":\"" + message + "\"}");
    }
}

