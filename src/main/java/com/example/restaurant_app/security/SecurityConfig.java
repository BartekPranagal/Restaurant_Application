package com.example.restaurant_app.security;

import com.example.restaurant_app.security.filter.CustomAuthenticationFilter;
import com.example.restaurant_app.security.filter.CustomAuthorizationFilter;
import com.example.restaurant_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .headers().frameOptions().sameOrigin().and()
                .csrf().disable()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
                .addFilter(new CustomAuthenticationFilter(authenticationManager()))
                .addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/api/menu", "/login", "api/addUser", "api/getPizzaRating/{id}").permitAll()
                .antMatchers("/h2-console/**", "/h2-console").permitAll()
                .antMatchers("/api/addOrder", "api/addRating", "api/orders").authenticated()
                .antMatchers("/api/users").hasAnyRole("ADMIN");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
}
