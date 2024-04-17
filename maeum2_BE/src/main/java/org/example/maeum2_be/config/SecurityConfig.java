package org.example.maeum2_be.config;


import org.example.maeum2_be.utils.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    public static class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
            builder.addFilter(new JwtAuthenticationFilter(authenticationManager));
            super.configure(builder);
        }
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
            http.csrf((csrf) -> disable());
            http.cors(Customizer.withDefaults());


            http.sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
            ));

            http.formLogin(AbstractHttpConfigurer::disable);


            return http.build();

    }
        
    }



}
