package com.adw1n.hubtraffic.controllers;

import com.adw1n.hubtraffic.config.WithOAuth2Authentication;
import com.adw1n.hubtraffic.config.WithOAuth2AuthenticationSecurityContextFactory;
import com.adw1n.hubtraffic.models.GithubUser;
import com.adw1n.hubtraffic.respositories.GithubUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;



@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {
    @Autowired
    GithubUserRepository githubUserRepository;
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webapp;

    @Before
    public void setup() {
        mvc = webAppContextSetup(webapp).build();
    }
    final ObjectMapper mapper = new ObjectMapper();

    @Test
    @WithOAuth2Authentication
    public void testApiGithubUserRetrievesExistingUser() throws Exception {
        GithubUser user = new GithubUser("Nemo", "1234");
        githubUserRepository.save(user);
        this.mvc.perform(
            get("/api/githubUser").principal(WithOAuth2AuthenticationSecurityContextFactory.getPrincipal(user)))
            .andDo(print()).andExpect(status().isOk())
            .andExpect(content().string(containsString(mapper.writeValueAsString(user))));
    }

    @Test
    @WithOAuth2Authentication
    public void testApiGithubUserCreatesNewUser() throws Exception {

        GithubUser user = new GithubUser("adw1n", "abcde");
        this.mvc.perform(
                get("/api/githubUser").principal(WithOAuth2AuthenticationSecurityContextFactory.getPrincipal(user)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString(mapper.writeValueAsString(user))));
        List<GithubUser> users = (List<GithubUser>) githubUserRepository.findAll();
        Assert.assertEquals(1,users.size());
        GithubUser createdUser = users.get(0);
        Assert.assertEquals(user.getName(), createdUser.getName());
        Assert.assertEquals(user.getToken(), createdUser.getToken());
    }
}
