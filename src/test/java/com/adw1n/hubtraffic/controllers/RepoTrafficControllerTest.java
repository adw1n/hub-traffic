package com.adw1n.hubtraffic.controllers;

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
import com.adw1n.hubtraffic.utils.GithubAPI;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(PowerMockRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.MOCK, classes = HubTrafficApplication.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@PowerMockIgnore({"javax.management.*"})
@Transactional
public class RepoTrafficControllerTest {
    @Autowired
    GithubUserRepository githubUserRepository;
    @Autowired
    GithubRepositoryRepository githubRepositoryRepository;
    @Autowired
    GithubRepositoryViewsRepository githubRepositoryViewsRepository;
    @Autowired
    GithubRepositoryClonesRepository githubRepositoryClonesRepository;


    MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webapp;

    GithubUser user;
    final ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
        mockMvc = webAppContextSetup(webapp).build();
        user = new GithubUser("Nemo", "1234");
        githubUserRepository.save(user);
    }

    private  List<RepoTrafficController.RepoTraffic> getTrafficInfoExample(GithubUser user){
        List<RepoTrafficController.RepoTraffic> expected = new ArrayList<>();

        String[] repoNames = new String[]{"repo-1", "repo-2"};
        for(String repoName: repoNames){
            GithubRepository repo = githubRepositoryRepository.findByNameAndUser(repoName, user);
            if(repo==null)
                repo = new GithubRepository(repoName, user);
            GithubRepositoryViews views;
            GithubRepositoryClones clones;
            if(repo.getId()!=null){
                views = githubRepositoryViewsRepository.findByRepository(repo).get(0);
                clones = githubRepositoryClonesRepository.findByRepository(repo).get(0);
            }
            else {
                 views = new GithubRepositoryViews(
                         new Date(),
                         ThreadLocalRandom.current().nextInt(1,100),
                         ThreadLocalRandom.current().nextInt(1,100),
                         repo);
                 clones = new GithubRepositoryClones
                         (new Date(),
                         ThreadLocalRandom.current().nextInt(1,100),
                         ThreadLocalRandom.current().nextInt(1,100),
                         repo);
            }
            expected.add(new RepoTrafficController.RepoTraffic(
                    repo,
                    Collections.singletonList(views),
                    Collections.singletonList(clones)));
        }
        return expected;
    }

    private  List<RepoTrafficController.RepoTraffic> createTrafficInfoExample(GithubUser user){
        List<RepoTrafficController.RepoTraffic> expected = getTrafficInfoExample(user);
        for(RepoTrafficController.RepoTraffic repoTraffic: expected){
            githubRepositoryRepository.save(repoTraffic.getName());
            githubRepositoryViewsRepository.save(repoTraffic.getViews());
            githubRepositoryClonesRepository.save(repoTraffic.getClones());
        }
        return expected;
    }


    @Test
    public void testApiRepositoryTrafficReturnsAllTrafficInfo() throws Exception{
        List<RepoTrafficController.RepoTraffic> expected = createTrafficInfoExample(user);
        GithubUser otherUser = new GithubUser("other user", "some token");
        githubUserRepository.save(otherUser);
        createTrafficInfoExample(otherUser);
        this.mockMvc.perform(
                get("/api/repository/traffic").principal(WithOAuth2AuthenticationSecurityContextFactory.getPrincipal(user)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expected)));
    }

    @Test
    @PrepareForTest({GithubAPI.class})
    public void testApiRepositoryTrafficDownloadsRepositoryInfoTheFirstTimeItsCalled() throws Exception{
        PowerMockito.spy(GithubAPI.class);
        PowerMockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                final GithubUser user = (GithubUser)(invocationOnMock.getArguments())[0];
                createTrafficInfoExample(user);
                return null;
            }
        }).when(GithubAPI.class, "fetchUpdates", Mockito.any());
        this.mockMvc.perform(
                get("/api/repository/traffic").principal(WithOAuth2AuthenticationSecurityContextFactory.getPrincipal(user)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(getTrafficInfoExample(user))));
        PowerMockito.verifyStatic(Mockito.times(1));
        GithubAPI.fetchUpdates(Mockito.any());
    }

    @Test
    @PrepareForTest({GithubAPI.class})
    public void testApiRepositoryTrafficDownloadsRepositoryInfoTheFirstTimeItsCalledNoTrafficInfoFetched() throws Exception{
        PowerMockito.spy(GithubAPI.class);
        PowerMockito.doNothing().when(GithubAPI.class, "fetchUpdates", Mockito.any());
        this.mockMvc.perform(
                get("/api/repository/traffic").principal(WithOAuth2AuthenticationSecurityContextFactory.getPrincipal(user)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("[]"));
        PowerMockito.verifyStatic(Mockito.times(1));
        GithubAPI.fetchUpdates(Mockito.any());
    }
}
