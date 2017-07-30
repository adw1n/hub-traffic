package com.adw1n.hubtraffic.scheduled;

import com.adw1n.hubtraffic.HubTrafficApplication;
import com.adw1n.hubtraffic.models.GithubRepository;
import com.adw1n.hubtraffic.models.GithubUser;
import com.adw1n.hubtraffic.respositories.GithubUserRepository;
import com.adw1n.hubtraffic.utils.GithubAPI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.HttpException;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import javax.transaction.Transactional;
import java.util.*;


@RunWith(PowerMockRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.MOCK, classes = HubTrafficApplication.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@PowerMockIgnore({"javax.management.*"})
@Transactional
public class TrafficInfoUpdaterTest {
    @Autowired
    private TrafficInfoUpdater trafficInfoUpdater;

    @Autowired
    private GithubUserRepository githubUserRepository;

    @Test
    @PrepareForTest({GithubAPI.class})
    public void testScheduledJobUpdateTrafficInfoWorksWhenOneOfTheUsersInvalidatedHisToken() throws Exception{
        GithubUser user1 = new GithubUser("HariSeldon", "1234");
        GithubUser user2 = new GithubUser("DorsVenabili", "abcd");
        GithubUser user3 = new GithubUser("CleonI", "zzzzz");
        githubUserRepository.save(user1);
        githubUserRepository.save(user2);
        githubUserRepository.save(user3);

        HttpException invalidCredentialsException = new HttpException(
                "Bad credentials", 401, "documentation_url", "https://developer.github.com/v3");

        List<GithubRepository> user1Repos = Arrays.asList(
                new GithubRepository("a", user1),
                new GithubRepository("b", user1));
        List<GithubRepository> user3Repos = Arrays.asList(
                new GithubRepository("zzz", user3),
                new GithubRepository("yyy", user3),
                new GithubRepository("xxx", user3));


        PowerMockito.spy(GithubAPI.class);

        PowerMockito.doReturn(user1Repos).when(GithubAPI.class, "getUserRepositories", Mockito.eq(user1));
        PowerMockito.doThrow(invalidCredentialsException).when(GithubAPI.class, "getUserRepositories", Mockito.eq(user2));
        PowerMockito.doReturn(user3Repos).when(GithubAPI.class, "getUserRepositories", Mockito.eq(user3));

        PowerMockito.doNothing().when(GithubAPI.class, "getRepositoryTrafficStats", Mockito.eq(user1), Mockito.any());
        PowerMockito.doNothing().when(GithubAPI.class, "getRepositoryTrafficStats", Mockito.eq(user3), Mockito.any());


        trafficInfoUpdater.updateTrafficInfo();


        PowerMockito.verifyPrivate(GithubAPI.class, Mockito.times(1))
                .invoke("getUserRepositories", user1);
        PowerMockito.verifyPrivate(GithubAPI.class, Mockito.times(1))
                .invoke("getUserRepositories", user2);
        PowerMockito.verifyPrivate(GithubAPI.class, Mockito.times(1))
                .invoke("getUserRepositories", user3);

        for(GithubRepository repo: user1Repos){
            PowerMockito.verifyPrivate(GithubAPI.class, Mockito.times(1))
                    .invoke("getRepositoryTrafficStats",user1, repo);
        }
        for(GithubRepository repo: user3Repos){
            PowerMockito.verifyPrivate(GithubAPI.class, Mockito.times(1))
                    .invoke("getRepositoryTrafficStats",user3, repo);
        }
        PowerMockito.verifyPrivate(GithubAPI.class, Mockito.times(0))
                .invoke("getRepositoryTrafficStats",Mockito.eq(user2), Mockito.any());
    }
}
