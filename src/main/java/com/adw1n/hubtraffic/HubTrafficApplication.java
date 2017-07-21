package com.adw1n.hubtraffic;

import com.adw1n.hubtraffic.utils.GithubAPI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@SpringBootApplication
@EnableOAuth2Sso
@RestController
public class HubTrafficApplication  extends WebSecurityConfigurerAdapter {
    @RequestMapping("/user")
    public Principal user(Principal principal) {
        GithubAPI.fetchUpdates(principal);
//        GithubUser user = GithubAPI.getUser(principal);
        return principal;
    }




    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http.antMatcher("/**").authorizeRequests().antMatchers("/", "/login**").permitAll().anyRequest()
                .authenticated().and().logout().logoutSuccessUrl("/").permitAll().and().csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        // @formatter:on
    }

	public static void main(String[] args) {
		SpringApplication.run(HubTrafficApplication.class, args);
	}
}
