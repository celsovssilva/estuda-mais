package com.estudamais.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return  httpSecurity
                .cors(cors ->cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req ->{
                    //auth
                    req.requestMatchers(HttpMethod.POST,"api/auth/login").permitAll();
                    req.requestMatchers(HttpMethod.POST,"api/auth/register").permitAll();
                    req.requestMatchers(HttpMethod.PUT,"api/auth/update").authenticated();
                    //checklist
                    req.requestMatchers(HttpMethod.POST,"api/checklist/create").authenticated();
                    req.requestMatchers(HttpMethod.GET,"api/checklist/getTasksByUser").authenticated();
                    req.requestMatchers(HttpMethod.PATCH,"api/checklist/{taskId}/toggle").authenticated();
                    req.requestMatchers(HttpMethod.DELETE,"api/checklist/delete/{taskId}").authenticated();
                    //Note
                    req.requestMatchers(HttpMethod.POST,"api/note/create").authenticated();
                    req.requestMatchers(HttpMethod.GET,"api/note/getByDateNote").authenticated();
                    req.requestMatchers(HttpMethod.GET,"api/note/getByUserNote").authenticated();
                    req.requestMatchers(HttpMethod.PUT,"api/note/update/{noteId}").authenticated();
                    req.requestMatchers(HttpMethod.DELETE,"api/checklist/delete/{taskId}").authenticated();
                    //schedule
                    req.requestMatchers(HttpMethod.POST,"api/schedule/create").authenticated();
                    req.requestMatchers(HttpMethod.GET,"api/schedule/getByUser").authenticated();
                    req.requestMatchers(HttpMethod.GET,"api/schedule/getByTypeSchedule").authenticated();
                    req.requestMatchers(HttpMethod.PUT,"api/schedule/update/{scheduleId}").authenticated();
                    req.requestMatchers(HttpMethod.DELETE,"api/schedule/delete/{scheduleId}").authenticated();
                    //Study
                    req.requestMatchers(HttpMethod.POST,"api/study/goal/save").authenticated();
                    req.requestMatchers(HttpMethod.GET,"api/study/goal").authenticated();
                    req.requestMatchers(HttpMethod.POST,"api/study/session/register").authenticated();
                    req.requestMatchers(HttpMethod.GET,"api/study/session/history").authenticated();
                    req.requestMatchers(HttpMethod.GET,"api/study/dashboard").authenticated();
                })
                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
