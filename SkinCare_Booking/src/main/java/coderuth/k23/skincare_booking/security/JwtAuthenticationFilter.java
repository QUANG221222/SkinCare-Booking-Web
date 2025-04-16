package coderuth.k23.skincare_booking.security;

import coderuth.k23.skincare_booking.jwt.JwtUtil;
import coderuth.k23.skincare_booking.services.AuthService;
import coderuth.k23.skincare_booking.services.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    // Constructor Injection
    public JwtAuthenticationFilter(JwtUtil jwtTokenUtil, UserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String accessToken = jwtTokenUtil.getTokenFromCookies(request, "accessToken");
            String refreshToken = jwtTokenUtil.getTokenFromCookies(request, "refreshToken");

            if (accessToken != null && jwtTokenUtil.validateToken(accessToken)) {
                String username = jwtTokenUtil.getUsernameFromToken(accessToken);

                // Use UserDetailsService to load user details
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            else if (jwtTokenUtil.isTokenExpired(accessToken)
                    && refreshToken != null && jwtTokenUtil.validateToken(refreshToken)) {

                try {
                    // Gọi service để tạo và gửi lại access token mới
                    String newAccessToken = refreshTokenService.refreshToken(request, response);


                    if (newAccessToken != null && jwtTokenUtil.validateToken(newAccessToken)) {
                        String username = jwtTokenUtil.getUsernameFromToken(newAccessToken);

                        if (username != null && !username.isEmpty()) {
                            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                            // Gán Authentication mới vào SecurityContext
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);

                            logger.info("Authentication refreshed for user: {}", username);
                        } else {
                            logger.error("Username extracted from new access token is null or empty");
                        }
                    } else {
                        logger.error("New access token is invalid or null");
                    }
                } catch (Exception e) {
                    logger.error("Error during refresh token process: {}", e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }
}
