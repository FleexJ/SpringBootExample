package com.example.springboot.config;

import com.example.springboot.entity.User;
import com.example.springboot.security.AuthProviderUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthProviderUser authProviderUser;

//    @Autowired
//    public SecurityConfig(AuthProviderUser authProviderUser) {
//        this.authProviderUser = authProviderUser;
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/sign_in", "/sign_up").anonymous()
                .antMatchers("/create_note", "/my_notes", "/my_profile/**", "/delete_note", "/edit_note").authenticated()
                .antMatchers("/admin/**").hasRole(User.ROLE_ADMIN)
                .and().csrf().disable()
                .formLogin()
                .loginPage("/sign_in")
                .loginProcessingUrl("/sign_in/process")
                .usernameParameter("email")
                .failureUrl("/sign_in?error=true")
                .and().exceptionHandling().accessDeniedPage("/")
                .and().logout();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProviderUser);
    }
}
