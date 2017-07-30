package com.adw1n.hubtraffic.controllers;

import com.adw1n.hubtraffic.config.WithOAuth2Authentication;
import com.adw1n.hubtraffic.config.WithOAuth2AuthenticationSecurityContextFactory;
import com.adw1n.hubtraffic.models.GithubRepository;
import com.adw1n.hubtraffic.models.GithubRepositoryClones;
import com.adw1n.hubtraffic.models.GithubRepositoryViews;
import com.adw1n.hubtraffic.models.GithubUser;
import com.adw1n.hubtraffic.respositories.GithubRepositoryClonesRepository;
import com.adw1n.hubtraffic.respositories.GithubRepositoryRepository;
import com.adw1n.hubtraffic.respositories.GithubRepositoryViewsRepository;
import com.adw1n.hubtraffic.respositories.GithubUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;



@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserControllerTest {
    @Autowired
    GithubUserRepository githubUserRepository;
    @Autowired
    GithubRepositoryRepository githubRepositoryRepository;
    @Autowired
    GithubRepositoryViewsRepository githubRepositoryViewsRepository;
    @Autowired
    GithubRepositoryClonesRepository githubRepositoryClonesRepository;


    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webapp;


    GithubUser user;
    final ObjectMapper mapper = new ObjectMapper();


    @Before
    public void setup() {
        mvc = webAppContextSetup(webapp).build();
        user = new GithubUser("Nemo", "1234");
    }

    private void checkThatNothingIsInDatabase(){
        Assert.assertEquals(0, Iterables.size(githubUserRepository.findAll()));
        Assert.assertEquals(0, Iterables.size(githubRepositoryRepository.findAll()));
        Assert.assertEquals(0, Iterables.size(githubRepositoryViewsRepository.findAll()));
        Assert.assertEquals(0, Iterables.size(githubRepositoryClonesRepository.findAll()));
    }

    @Test
    public void testApiUserGETReturnsUsername() throws Exception {
        githubUserRepository.save(user);
        this.mvc.perform(
                get("/api/user").principal(WithOAuth2AuthenticationSecurityContextFactory.getPrincipal(user)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(user.getName()));
    }

    @Test
    public void testApiUserDELETEDeletesUserAndAllOfHisTrafficInfo() throws Exception {
        githubUserRepository.save(user);
        String[] repoNames = new String[]{"repo-1", "repo-2"};
        for(String repoName: repoNames){
            GithubRepository repo = new GithubRepository(repoName, user);
            githubRepositoryRepository.save(repo);
            githubRepositoryViewsRepository.save(new GithubRepositoryViews(new Date(), 5, 3, repo));
            githubRepositoryClonesRepository.save(new GithubRepositoryClones(new Date(), 4, 2, repo));
        }
        this.mvc.perform(
                MockMvcRequestBuilders.delete("/api/user")
                        .principal(WithOAuth2AuthenticationSecurityContextFactory.getPrincipal(user))
        )
                .andDo(print()).andExpect(status().isOk());
        checkThatNothingIsInDatabase();
    }

    @Test
    public void testApiGithubUserRetrievesExistingUser() throws Exception {
        githubUserRepository.save(user);
        this.mvc.perform(
            get("/api/githubUser").principal(WithOAuth2AuthenticationSecurityContextFactory.getPrincipal(user)))
            .andDo(print()).andExpect(status().isOk())
            .andExpect(content().string(mapper.writeValueAsString(user)));
    }

    @Test
    public void testApiGithubUserCreatesNewUser() throws Exception {
        this.mvc.perform(
                get("/api/githubUser").principal(WithOAuth2AuthenticationSecurityContextFactory.getPrincipal(user)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(user)));
        List<GithubUser> users = (List<GithubUser>) githubUserRepository.findAll();
        Assert.assertEquals(1,users.size());
        GithubUser createdUser = users.get(0);
        Assert.assertEquals(user.getName(), createdUser.getName());
        Assert.assertEquals(user.getToken(), createdUser.getToken());
    }

    @Test
    public void testApiUserRepositories() throws Exception{
        githubUserRepository.save(user);
        String[] repoNames = new String[]{"repo-1", "repo-2"};
        ArrayList<GithubRepository> repos = new ArrayList<>();
        for(String repoName: repoNames){
            GithubRepository repo = new GithubRepository(repoName, user);
            githubRepositoryRepository.save(repo);
            repos.add(repo);
            githubRepositoryViewsRepository.save(new GithubRepositoryViews(new Date(), 5, 3, repo));
            githubRepositoryClonesRepository.save(new GithubRepositoryClones(new Date(), 4, 2, repo));
        }

        this.mvc.perform(
                get("/api/userRepositories").principal(WithOAuth2AuthenticationSecurityContextFactory.getPrincipal(user)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(repos)));

    }
}
