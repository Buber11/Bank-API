package main.BankApp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import main.BankApp.service.bucket.BucketService;
import main.BankApp.service.session.SessionService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final SessionService sessionService;
    private final BucketService bucketService;




    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (Arrays.stream(SecurityConfiguration.PUBLIC_ENDPOINTS).anyMatch(requestURI::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtService.extractToken(request);

        String ip = sessionService.getClientIp(request);
        String userAgent = sessionService.getUserAgent(request);

        if (token != null) {
            if(bucketService.tryConsume(token)){
                try {
                    final String jwt = token;
                    final String username = jwtService.extractUsername(jwt);

                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                    if (username != null && authentication == null) {
                        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                        if (jwtService.isTokenValid(jwt, userDetails)) {
                            long userId = jwtService.extractUserId(jwt);
                            if(sessionService.checkSession(userId,ip,userAgent)){
                                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                SecurityContextHolder.getContext().setAuthentication(authToken);
                                long id = jwtService.extractUserId(jwt);
                                request.setAttribute("id", id);
                                request.setAttribute("session_id", sessionService.getSessionId(id));
                            }
                        }
                    }
                } catch (Exception exception) {
                    handlerExceptionResolver.resolveException(request, response, null, exception);
                    return;
                }
            }else {
                response.setStatus(429);
            }
        }
        filterChain.doFilter(request, response);
    }
}