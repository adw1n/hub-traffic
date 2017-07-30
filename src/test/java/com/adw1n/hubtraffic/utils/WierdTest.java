package com.adw1n.hubtraffic.utils;

import com.adw1n.hubtraffic.HubTrafficApplication;
import com.adw1n.hubtraffic.models.GithubRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.*;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(PowerMockRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.MOCK, classes = HubTrafficApplication.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@PowerMockIgnore({"javax.management.*"})
@Transactional
public class WierdTest {
    @Autowired
    GithubUserRepository githubUserRepository;
    @Autowired
    GithubRepositoryRepository githubRepositoryRepository;
    @Autowired
    GithubRepositoryViewsRepository githubRepositoryViewsRepository;
    @Autowired
    GithubRepositoryClonesRepository githubRepositoryClonesRepository;

    GithubUser user;

    @Before
    public void setup() {
        user = new GithubUser("Nemo", "1234");
        githubUserRepository.save(user);
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


        List<GithubRepository> userRepos = GithubAPI.getUserRepositories(user);
        Assert.assertEquals(expected.size(), userRepos.size());
        Assert.assertEquals(expected.size(), Iterables.size(githubRepositoryRepository.findAll()));
        for(GithubRepository expectedRepo: expected){
            Assert.assertNotNull(githubRepositoryRepository.findByNameAndUser(expectedRepo.getName(), user));
        }
    }
}
