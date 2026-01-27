package net.doha.microservice_planaction.CONFIG;


import lombok.RequiredArgsConstructor;
import net.doha.microservice_planaction.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
     private final JwtAuthenticationFilter jwtAuthFilter;

     @Bean
     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
          http
                  .csrf(AbstractHttpConfigurer::disable) // Désactivé car on utilise JWT
                  .authorizeHttpRequests(auth -> auth
                          .requestMatchers("/api/v1/actions/public/**").permitAll() // Routes publiques si besoin
                          .anyRequest().authenticated() // Tout le reste nécessite un token
                  )
                  .sessionManagement(session -> session
                          .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Pas de session côté serveur
                  )
                  .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

          return http.build();
     }


}
