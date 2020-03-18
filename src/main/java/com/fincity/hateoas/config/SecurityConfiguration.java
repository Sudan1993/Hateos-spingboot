package com.fincity.hateoas.config;

import com.fincity.hateoas.filters.SessionFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/*").
                permitAll();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/register",
                "/confirm");
    }

    /**
     * Enable filters only for the /cars/* pattern
     * @return
     */

    @Bean
    public FilterRegistrationBean<SessionFilter> sessionFilterRegistration() {

        FilterRegistrationBean<SessionFilter> registration = new FilterRegistrationBean<>(sessionFilter());
        registration.setUrlPatterns(Collections.singleton("/cars/*"));
        return registration;
    }

    @Bean(name = "someFilter")
    public SessionFilter sessionFilter() {
        return new SessionFilter();
    }
}
