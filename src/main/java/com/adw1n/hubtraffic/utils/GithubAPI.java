package com.adw1n.hubtraffic.utils;

import com.adw1n.hubtraffic.models.GithubRepository;
import com.adw1n.hubtraffic.models.GithubRepositoryClones;
import com.adw1n.hubtraffic.models.GithubRepositoryViews;
import com.adw1n.hubtraffic.models.GithubUser;
import com.adw1n.hubtraffic.respositories.GithubRepositoryClonesRepository;
import com.adw1n.hubtraffic.respositories.GithubRepositoryRepository;
import com.adw1n.hubtraffic.respositories.GithubRepositoryViewsRepository;
import com.adw1n.hubtraffic.respositories.GithubUserRepository;
import lombok.Data;
import lombok.Setter;
import org.kohsuke.github.GHRepository;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.kohsuke.github.GitHub;
import org.springframework.web.client.RestTemplate;

@Data
public class GithubAPI {
    @Setter static GithubUserRepository githubUserRepository;
    @Setter static GithubRepositoryRepository githubRepositoryRepository;
    @Setter static GithubRepositoryViewsRepository githubRepositoryViewsRepository;
    @Setter static GithubRepositoryClonesRepository githubRepositoryClonesRepository;
    @Setter static RestTemplate restTemplate;




    public static void fetchUpdates(Principal principal){
        GithubUser user = getUser(principal);
        String token = getToken(principal);
        try {
            for(GithubRepository repo: getUserRepositories(user, token)) {
                getRepositoryTrafficStats(user, token, repo.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static List<GithubRepository> getUserRepositories(GithubUser user, String token) throws IOException {
        GitHub github = GitHub.connect(user.getName(), token);
        List<GithubRepository> repositories = new ArrayList<>();
        for(Map.Entry<String, GHRepository> pair: github.getUser(user.getName()).getRepositories().entrySet()){
            String repoName = pair.getKey();
            GithubRepository repo = githubRepositoryRepository.findByName(repoName);
            if(repo==null){
                repo = new GithubRepository(repoName, user);
                githubRepositoryRepository.save(repo);
            }
            repositories.add(repo);
        }
        return repositories;
    }



    private static void getRepositoryTrafficStats(GithubUser user, String token, String repositoryName){
        String viewsURL = "https://api.github.com/repos/"+user.getName()+"/"+repositoryName+"/traffic/views?access_token="+token;
        String clonesURL = "https://api.github.com/repos/"+user.getName()+"/"+repositoryName+"/traffic/clones?access_token="+token;
        GithubRepository repo = githubRepositoryRepository.findByName(repositoryName);
        GithubViewsResponse viewsResponse = restTemplate.getForObject(viewsURL, GithubViewsResponse.class);
        for(GithubRepositoryViews views: viewsResponse.getViews()){
            GithubRepositoryViews repoViews = githubRepositoryViewsRepository.findByRepositoryAndTimestamp(repo, views.getTimestamp());
            // TODO the data can change - eg. I checked at monday at 4am and then at 12am and the values changed
            if(repoViews==null){
                views.setRepository(repo);
                githubRepositoryViewsRepository.save(views);
            }
        }
        GithubClonesResponse clonesResponse = restTemplate.getForObject(clonesURL, GithubClonesResponse.class);
        for(GithubRepositoryClones clones: clonesResponse.getClones()){
            GithubRepositoryClones repoClones = githubRepositoryClonesRepository.findByRepositoryAndTimestamp(repo, clones.getTimestamp());
            if(repoClones==null){
                repoClones.setRepository(repo);
                githubRepositoryClonesRepository.save(repoClones);
            }
        }
    }


    public static GithubUser getUser(Principal principal){
        String name = principal.getName();
        GithubUser user = githubUserRepository.findByName(name);
        if(user==null){
            user = new GithubUser(name);
            githubUserRepository.save(user);
        }
        return user;
    }


    public static String getToken(Principal principal){
        String token = ((OAuth2AuthenticationDetails) ((OAuth2Authentication) principal).getDetails()).getTokenValue();
        return token;
    }
}
