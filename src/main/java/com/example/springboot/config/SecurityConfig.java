package com.example.springboot.config;

import com.example.springboot.entity.User;
import com.example.springboot.security.AuthProviderUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthProviderUser authProviderUser;

    @Autowired
    public SecurityConfig(AuthProviderUser authProviderUser) {
        this.authProviderUser = authProviderUser;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/sign_in", "/sign_up").anonymous()
                .antMatchers("/note/**", "/my_profile/**").authenticated()
                .antMatchers("/admin/**").hasRole(User.ROLE_ADMIN)
                .and().csrf().disable()
                .formLogin()
                .loginPage("/sign_in")
                .usernameParameter("email")
                .failureUrl("/sign_in?error=true")
                .and().exceptionHandling().accessDeniedPage("/")
                .and().logout()
                .and().sessionManagement()
                .invalidSessionUrl("/")
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true)
                .sessionRegistry(sessionRegistry());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProviderUser);
    }
}
