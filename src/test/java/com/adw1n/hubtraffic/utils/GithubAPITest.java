package com.adw1n.hubtraffic.utils;


import com.adw1n.hubtraffic.config.WithOAuth2AuthenticationSecurityContextFactory;
import com.adw1n.hubtraffic.models.GithubRepository;
import com.adw1n.hubtraffic.models.GithubRepositoryClones;
import com.adw1n.hubtraffic.models.GithubRepositoryViews;
import com.adw1n.hubtraffic.models.GithubUser;
import com.adw1n.hubtraffic.respositories.GithubRepositoryClonesRepository;
import com.adw1n.hubtraffic.respositories.GithubRepositoryRepository;
import com.adw1n.hubtraffic.respositories.GithubRepositoryViewsRepository;
import com.adw1n.hubtraffic.respositories.GithubUserRepository;
import com.google.common.collect.Iterables;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import javax.transaction.Transactional;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class GithubAPITest {
    @Autowired
    GithubUserRepository githubUserRepository;
    @Autowired
    GithubRepositoryRepository githubRepositoryRepository;
    @Autowired
    GithubRepositoryViewsRepository githubRepositoryViewsRepository;
    @Autowired
    GithubRepositoryClonesRepository githubRepositoryClonesRepository;


    private GithubUser user;
    final private DateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z (z)", Locale.US);

    @Before
    public void setup() {
        user = new GithubUser("Nemo", "1234");
        githubUserRepository.save(user);
    }

    @Test
    public void testGetRepositoryTrafficStats() throws Exception{
        GithubRepository repo = new GithubRepository("repo-1", user);
        githubRepositoryRepository.save(repo);
        String viewsURL = "https://api.github.com/repos/Nemo/repo-1/traffic/views?access_token=1234";
        String clonesURL = "https://api.github.com/repos/Nemo/repo-1/traffic/clones?access_token=1234";

        RestTemplate mock = Mockito.mock(RestTemplate.class);


        GithubViewsResponse viewsResponse = new GithubViewsResponse(
                5+8+17, 2+3+6 ,
                Arrays.asList(
                        new GithubRepositoryViews(formatter.parse("Sun Jan 01 2017 02:00:00 GMT+0200 (CEST)"), 5, 2, repo),
                        new GithubRepositoryViews(formatter.parse("Sun Jan 02 2017 02:00:00 GMT+0200 (CEST)"), 8, 3, repo),
                        new GithubRepositoryViews(formatter.parse("Sun Jan 03 2017 02:00:00 GMT+0200 (CEST)"), 17, 6, repo)));
        Mockito.when(mock.getForObject(viewsURL, GithubViewsResponse.class)).thenReturn(viewsResponse);
        GithubClonesResponse clonesResponse = new GithubClonesResponse(
                12+21, 1+3 ,
                Arrays.asList(
                        new GithubRepositoryClones(formatter.parse("Sun Jan 01 2017 02:00:00 GMT+0200 (CEST)"), 12, 1, repo),
                        new GithubRepositoryClones(formatter.parse("Sun Jan 02 2017 02:00:00 GMT+0200 (CEST)"), 21, 3, repo)));
        Mockito.when(mock.getForObject(clonesURL, GithubClonesResponse.class)).thenReturn(clonesResponse);



        GithubAPI.setRestTemplate(mock);
        GithubAPI.getRepositoryTrafficStats(user, repo);
        Assert.assertEquals(3,Iterables.size(githubRepositoryViewsRepository.findAll()));
        Assert.assertEquals(2,Iterables.size(githubRepositoryClonesRepository.findAll()));
    }


    @Test
    public void testGetUserRepositories()  throws Exception{

    }

    @Test
    public void testGetUser() throws Exception{
        Principal principal = WithOAuth2AuthenticationSecurityContextFactory.getPrincipal(user);
        GithubUser retrievedUser = GithubAPI.getUser(principal);
        Assert.assertEquals(user, retrievedUser);
    }

    @Test
    public void testGetToken() throws Exception{
        Principal principal = WithOAuth2AuthenticationSecurityContextFactory.getPrincipal(user);
        String token = GithubAPI.getToken(principal);
        Assert.assertEquals(user.getToken(), token);
    }
}
