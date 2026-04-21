//package tunequest.configuration.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import java.util.List;
//
//
//@EnableWebSecurity
//@EnableMethodSecurity(jsr250Enabled = true)
//@Configuration
//public class WebConfig {
////
////    @Bean
////    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
////        httpSecurity
//////                .csrf(AbstractHttpConfigurer::disable)
//////                .formLogin(AbstractHttpConfigurer::disable)
////                .sessionManagement(configurer ->
////                        configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
////                .authorizeHttpRequests(registry ->
////                        registry.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()                 // CORS pre-flight requests should be public
////                                .requestMatchers(HttpMethod.POST, "/spotifylogin/login", "/spotifylogin/**").permitAll()  // Creating a student and login are public// Swagger is also public (In "real life" it would only be public in non-production environments)
////                                .anyRequest().authenticated()                                             // Everything else --> authentication required, which is Spring security's default behaviour
////                );
////               // .exceptionHandling(configure -> configure.authenticationEntryPoint(authenticationEntryPoint))
////                //.addFilterBefore(authenticationRequestFilter, UsernamePasswordAuthenticationFilter.class);
////        return httpSecurity.build();
////    }
//
//
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**") // Allow all endpoints
//                        .allowedOrigins("http://localhost:3000") // Allow frontend origin
//                        //                                , "https://tunequestapigateway.azure-api.net", "https://purple-smoke-0c75a6403.5.azurestaticapps.net/") // Allow requests from frontend and api gateway
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow these HTTP methods
//                        .allowedHeaders("*") // Allow all headers
//                        .allowCredentials(true); // Allow credentials (cookies)
//            }
//        };
//    }
//
////    @Bean
////    public CorsConfigurationSource corsConfigurationSource() {
////        CorsConfiguration configuration = new CorsConfiguration();
////        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Allow your frontend origin
////        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allow HTTP methods
////        configuration.setAllowedHeaders(List.of("*")); // Allow all headers
////        configuration.setAllowCredentials(true); // Allow cookies
////
////        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
////        source.registerCorsConfiguration("/**", configuration);
////
////        return source;
////    }
//
//}

package tunequest.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Allow all endpoints
                        .allowedOrigins("http://localhost:3000", "https://tunequestapigateway.azure-api.net", "https://purple-smoke-0c75a6403.5.azurestaticapps.net/") // Allow requests from frontend and api gateway
                        .allowedMethods("GET", "POST") // Allow HTTP methods
                        .allowedHeaders("*"); // Allow all headers
            }
        };
    }
}
