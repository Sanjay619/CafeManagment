package com.inn.cafee.JWT;

import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    JWTFileter jwtFileter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(this.customerUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());


        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

   @Bean
   public SecurityFilterChain setSecurityFilterChain(HttpSecurity http) throws Exception {
        http.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.disable())
               .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
               .authorizeHttpRequests((authz) -> authz
                       .requestMatchers("/user/signup", "/user/login", "/user/forgetPassword")
                       .permitAll()
                       .anyRequest()
                       .authenticated()
               )
               .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {})
               .sessionManagement((sessions) -> sessions
                       .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

               );

       http.addFilterBefore(jwtFileter, UsernamePasswordAuthenticationFilter.class);

       http.authenticationProvider(daoAuthenticationProvider());
       DefaultSecurityFilterChain filerChain = http.build();
       return filerChain;
    }


}
