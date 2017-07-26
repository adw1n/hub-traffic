package com.adw1n.hubtraffic.utils;

import com.adw1n.hubtraffic.models.GithubRepository;
import com.adw1n.hubtraffic.models.GithubRepositoryClones;
import com.adw1n.hubtraffic.models.GithubRepositoryViews;
import com.adw1n.hubtraffic.models.GithubUser;
import com.adw1n.hubtraffic.respositories.GithubRepositoryClonesRepository;
import com.adw1n.hubtraffic.respositories.GithubRepositoryRepository;
import com.adw1n.hubtraffic.respositories.GithubRepositoryViewsRepository;
import com.adw1n.hubtraffic.respositories.GithubUserRepository;
import com.adw1n.hubtraffic.scheduled.TrafficInfoUpdater;
import lombok.Data;
import lombok.Setter;
import org.kohsuke.github.GHRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Data
public class GithubAPI {
    @Setter static GithubUserRepository githubUserRepository;
    @Setter static GithubRepositoryRepository githubRepositoryRepository;
    @Setter static GithubRepositoryViewsRepository githubRepositoryViewsRepository;
    @Setter static GithubRepositoryClonesRepository githubRepositoryClonesRepository;
    @Setter static RestTemplate restTemplate;


    private static final Logger log = LoggerFactory.getLogger(GithubAPI.class);

    @Async
    public static void fetchUpdates(GithubUser user){
        try {
            for(GithubRepository repo: getUserRepositories(user)) {
                getRepositoryTrafficStats(user, repo);
            }
            log.info("Updated traffic info for user {}", user.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private static List<GithubRepository> getUserRepositories(GithubUser user) throws IOException {
        GitHub github = GitHub.connect(user.getName(), user.getToken());
        List<GithubRepository> repositories = new ArrayList<>();
        for(Map.Entry<String, GHRepository> pair: github.getUser(user.getName()).getRepositories().entrySet()){
            String repoName = pair.getKey();
            GithubRepository repo = githubRepositoryRepository.findByNameAndUser(repoName, user);
            if(repo==null){
                repo = new GithubRepository(repoName, user);
                githubRepositoryRepository.save(repo);
            }
            repositories.add(repo);
        }
        return repositories;
    }



    private static void getRepositoryTrafficStats(GithubUser user, GithubRepository repo){
        String viewsURL = "https://api.github.com/repos/"+user.getName()+"/"+repo.getName()+"/traffic/views?access_token="+user.getToken();
        String clonesURL = "https://api.github.com/repos/"+user.getName()+"/"+repo.getName()+"/traffic/clones?access_token="+user.getToken();
        GithubViewsResponse viewsResponse = restTemplate.getForObject(viewsURL, GithubViewsResponse.class);
        for(GithubRepositoryViews views: viewsResponse.getViews()){
            System.out.println(repo.getName());
            GithubRepositoryViews repoViews = githubRepositoryViewsRepository.findByRepositoryAndTimestamp(repo, views.getTimestamp());
            if(repoViews==null){
                views.setRepository(repo);
                githubRepositoryViewsRepository.save(views);
            }
            else {
                // data was updated - eg. last time we checked at 6am,
                // now is 12am and since then 10 more people viewed the repository
                if(!repoViews.getCount().equals(views.getCount()) ||
                    !repoViews.getUniques().equals(views.getUniques())) {
                    repoViews.setCount(views.getCount());
                    repoViews.setUniques(views.getUniques());
                    githubRepositoryViewsRepository.save(repoViews);
                }
            }
        }
        GithubClonesResponse clonesResponse = restTemplate.getForObject(clonesURL, GithubClonesResponse.class);
        for(GithubRepositoryClones clones: clonesResponse.getClones()){
            GithubRepositoryClones repoClones = githubRepositoryClonesRepository.findByRepositoryAndTimestamp(repo, clones.getTimestamp());
            if(repoClones==null){
                clones.setRepository(repo);
                githubRepositoryClonesRepository.save(clones);
            }
            else if(!repoClones.getCount().equals(clones.getCount()) ||
                    !repoClones.getUniques().equals(clones.getUniques())){
                repoClones.setCount(clones.getCount());
                repoClones.setUniques(clones.getUniques());
                githubRepositoryClonesRepository.save(repoClones);
            }
        }
    }


    public static GithubUser getUser(Principal principal){
        String name = principal.getName();
        GithubUser user = githubUserRepository.findByName(name);
        if(user==null){
            String token = getToken(principal);
            user = new GithubUser(name, token);
            githubUserRepository.save(user);
            System.out.println("user created");
        }
        return user;
    }


    public static String getToken(Principal principal){
        String token = ((OAuth2AuthenticationDetails) ((OAuth2Authentication) principal).getDetails()).getTokenValue();
        return token;
    }
}
