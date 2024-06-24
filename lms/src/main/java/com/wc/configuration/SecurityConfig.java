package com.wc.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter; // Add this import
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * The project security configuration is provided here for the application,
 * using @Configuration defines this class as the configuration class for the
 * application. @EnableWebSecurity will enable the web security for the
 * application and we can provide parameters to secure our web app from
 * different live scenarios. It extends WebSecurityConfigurerAdapter for the same
 * purpose.
 * 
 * 
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * The below instance variables are autowired and initialised from
     * application.properties file.
     * 
     */
    @Autowired
    private DataSource dataSource;

    @Value("${spring.queries.users-query}")
    private String usersQuery;

    @Value("${spring.queries.roles-query}")
    private String rolesQuery;

    /**
     * This method will check for the users for authentication provided with
     * username and password.
     * 
     * @param AuthenticationManagerBuilder
     * 
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
            .usersByUsernameQuery(usersQuery)
            .authoritiesByUsernameQuery(rolesQuery)
            .dataSource(dataSource)
            .passwordEncoder(passwordEncoder());
    }

    /**
     * This method catches all URLs in the application and checks if the user is
     * authentic and manages login, logout, error-like pages and also guides users
     * to role-specific URLs.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
	        .antMatchers("/", "/login", "/password-reset/**").permitAll()
	        .antMatchers("/admin/register").permitAll()
	        .antMatchers("/admin/**").hasAuthority("MANAGER")
            .anyRequest().authenticated().and().csrf().disable().formLogin()
            .loginPage("/login").failureUrl("/login?error=true")
            .defaultSuccessUrl("/user/home")
            .usernameParameter("email")
            .passwordParameter("password")
            .and().logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessUrl("/").and().exceptionHandling()
            .accessDeniedPage("/access-denied");
    }

    /**
     * To ignore security on particular things like resources, jar files, etc., this
     * method is used.
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/fonts/**", "/js/**", "/img/**");
    }

}
