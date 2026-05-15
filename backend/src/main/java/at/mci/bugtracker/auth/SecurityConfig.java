package at.mci.bugtracker.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(
            HttpSecurity http,
            SessionAuthFilter sessionAuthFilter,
            @Value("${app.cors.allowed-origin:}") String extraAllowedOrigin) throws Exception {

        List<String> allowedOrigins = new ArrayList<>();
        allowedOrigins.add("http://localhost:5173");
        if (extraAllowedOrigin != null && !extraAllowedOrigin.isBlank()) {
            allowedOrigins.add(extraAllowedOrigin);
        }

        http
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // CSRF deaktiviert — Schutz erfolgt durch HttpOnly-Session-Cookie + SameSite=Lax
                // + CORS-Whitelist. Begründung siehe TASKS.md T021 (CSRF-Entscheidung).
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(req -> {
                    CorsConfiguration c = new CorsConfiguration();
                    c.setAllowedOrigins(allowedOrigins);
                    c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                    c.setAllowCredentials(true);
                    c.setAllowedHeaders(List.of("*", "X-XSRF-TOKEN"));
                    c.setExposedHeaders(List.of("Set-Cookie"));
                    return c;
                }))

                // Smart entry point: JSON-401 for /api/** and friends, redirect to /login for HTML pages.
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint((req, res, e) -> {
                            if (isApi(req.getRequestURI())) {
                                writeJson(res, 401, "Unauthorized");
                            } else {
                                res.sendRedirect("/login");
                            }
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            if (isApi(req.getRequestURI())) {
                                writeJson(res, 403, "Forbidden");
                            } else {
                                res.sendRedirect("/dashboard?error=forbidden");
                            }
                        }))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                // Public auth API endpoints
                                "/api/auth/login",
                                "/api/auth/register",
                                // Health/Info für Fly-Health-Check (unauth → nur {"status":"UP"})
                                "/actuator/health",
                                "/actuator/info",
                                // Public HTML routes
                                "/",
                                "/login",
                                "/register"
                        ).permitAll()
                        .anyRequest().authenticated())

                .addFilterBefore(sessionAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private static boolean isApi(String uri) {
        return uri != null && (uri.startsWith("/api/") || uri.startsWith("/v3/api-docs") || uri.equals("/healthz"));
    }

    private static void writeJson(jakarta.servlet.http.HttpServletResponse response, int status, String message)
            throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}