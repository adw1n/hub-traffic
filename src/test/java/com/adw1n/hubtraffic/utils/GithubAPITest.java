package com.adw1n.hubtraffic.utils;


import com.adw1n.hubtraffic.HubTrafficApplication;
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
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import javax.transaction.Transactional;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@RunWith(PowerMockRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.MOCK, classes = HubTrafficApplication.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@PowerMockIgnore({"javax.management.*"})
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
        Whitebox.invokeMethod(GithubAPI.class, "getRepositoryTrafficStats", user, repo);
        Assert.assertEquals(3,Iterables.size(githubRepositoryViewsRepository.findAll()));
        Assert.assertEquals(2,Iterables.size(githubRepositoryClonesRepository.findAll()));
    }


    @Test
    @PrepareForTest({GitHub.class})
    public void testGetUserRepositories() throws Exception{
        List<GithubRepository> expected = new ArrayList<>();

        GitHub gitHub = Mockito.mock(GitHub.class);
        PowerMockito.mockStatic(GitHub.class);
        PowerMockito.when(GitHub.connect(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(gitHub);
        GHUser ghUser = Mockito.mock(GHUser.class);
        Mockito.when(gitHub.getUser(user.getName())).thenReturn(ghUser);
        Map<String, GHRepository> repositories = new HashMap<>();
        for(String repoName: Arrays.asList("a", "b", "c", "D")){
            GHRepository repoMock = Mockito.mock(GHRepository.class);
            repositories.put(repoName, repoMock);
            expected.add(new GithubRepository(repoName, user));
        }
        Mockito.when(ghUser.getRepositories()).thenReturn(repositories);

        List<GithubRepository> userRepos = Whitebox.invokeMethod(GithubAPI.class,"getUserRepositories", user);
        Assert.assertEquals(expected.size(), userRepos.size());
        Assert.assertEquals(expected.size(), Iterables.size(githubRepositoryRepository.findAll()));
        for(GithubRepository expectedRepo: expected){
            Assert.assertNotNull(githubRepositoryRepository.findByNameAndUser(expectedRepo.getName(), user));
        }
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
        String token = Whitebox.invokeMethod(GithubAPI.class, "getToken", principal);
        Assert.assertEquals(user.getToken(), token);
    }
}
