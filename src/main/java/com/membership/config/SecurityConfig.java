package com.membership.config;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.authorizeRequests()
            //.antMatchers("/test/anonymous").permitAll()
        
            .antMatchers("/api/badges/swipe").hasAnyRole("scanner")
            
            .antMatchers("/api/badges").hasAnyRole("admin")
            .antMatchers("/api/members").hasAnyRole("admin")
            .antMatchers("/api/memberships").hasAnyRole("admin")
            .antMatchers("/api/plans").hasAnyRole("admin")
            .antMatchers("/api/transactions").hasAnyRole("admin")
            .antMatchers("/api/timeslots").hasAnyRole("admin")
            .antMatchers("/api/activitytypes").hasAnyRole("admin")
            .antMatchers("/api/locations").hasAnyRole("admin")
            .antMatchers("/api/roles").hasAnyRole("admin")
            .antMatchers("/api").hasAnyRole("admin")
            .antMatchers("/intro/staff").hasAnyRole("user","admin")
            .antMatchers("/authentications").hasAnyRole("checker")
            .antMatchers("/intro/allusers").permitAll()

            
            .antMatchers("/intro/publics").anonymous();
        http.csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    public KeycloakConfigResolver KeycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }
}