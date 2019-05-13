package com.searchvids.config;

import com.searchvids.service.security.UserDetailsServiceImplementation;
import com.searchvids.service.security.jwt.JwtAuthEntryPoint;
import com.searchvids.service.security.jwt.JwtAuthTokenFilter;
import com.searchvids.service.security.jwt.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsServiceImplementation service;
    private JwtAuthEntryPoint unauthorizedHandler;
    private JwtProvider provider;

    public WebSecurityConfig(UserDetailsServiceImplementation service,
                             JwtAuthEntryPoint unauthorizedHandler, JwtProvider provider) {
        this.service = service;
        this.unauthorizedHandler = unauthorizedHandler;
        this.provider = provider;
    }

    @Bean
    public JwtAuthTokenFilter tokenFilter() {
        return new JwtAuthTokenFilter(provider, service);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(service)
                .passwordEncoder(encoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf()
                .disable()

                .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()

                .and()
                .authorizeRequests()
                .antMatchers("/h2-console/*").permitAll()

                .and()
                .authorizeRequests()
                .anyRequest().authenticated()

                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.headers().frameOptions().disable();

        http.addFilterBefore(tokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
