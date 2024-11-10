package main.BankApp.security;


import main.BankApp.model.user.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    String[] endpointsPermitAll = {
            "api/v1/auth/login",
            "api/v1/auth/signup",
            "api/v1/currency/**"
    };

    String[] clientEndpoints = {
            "api/v1/accounts",
            "api/v1/accounts/*/transaction/*",
            "api/v1/transactions",
            "api/v1/contacts",
            "api/v1/user",
            "api/v1/accounts/{account-number}/currency",
            "api/v1/transactions-group",
    };
    String[] companyEndpoints = {
//            "api/v1/transactions-group",
    };
    String[] workersEndpoints = {
            "/api/v1/users",
            "api/v1/users/{id}/{status}",
            "api/v1/users/{id}",
    };


    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(e -> e
                        .requestMatchers(endpointsPermitAll).permitAll()
                        .requestMatchers(clientEndpoints).hasAnyRole("CLIENT","COMPANY")
                        .requestMatchers(companyEndpoints).hasRole("COMPANY")
                        .requestMatchers(workersEndpoints).hasRole("WORKER")
                        .anyRequest().authenticated() )
                        .sessionManagement(e-> e.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        httpSecurity.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
                        .authenticationProvider(authenticationProvider)
                        .addFilterBefore(jwtAuthenticationFilter,UsernamePasswordAuthenticationFilter.class);
        httpSecurity.httpBasic(Customizer.withDefaults());
        httpSecurity.cors(cors -> corsConfigurationSource());

        return httpSecurity.build();

//        http.csrf()
//                .disable()
//                .authorizeHttpRequests()
//                .requestMatchers("/auth/**")
//                .permitAll()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authenticationProvider(authenticationProvider)
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000","http://192.168.31.101:3000","https://192.168.31.101:8443"));
        configuration.setAllowedMethods(List.of("GET","POST","PATCH","PUT","DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**",configuration);

        return source;
    }
}