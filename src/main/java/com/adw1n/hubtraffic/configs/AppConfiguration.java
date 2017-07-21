package com.adw1n.hubtraffic.configs;

import com.adw1n.hubtraffic.respositories.GithubRepositoryClonesRepository;
import com.adw1n.hubtraffic.respositories.GithubRepositoryRepository;
import com.adw1n.hubtraffic.respositories.GithubRepositoryViewsRepository;
import com.adw1n.hubtraffic.respositories.GithubUserRepository;
import com.adw1n.hubtraffic.utils.GithubAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class AppConfiguration {
    @Autowired
    GithubUserRepository githubUserRepository;
    @Autowired
    GithubRepositoryRepository githubRepositoryRepository;
    @Autowired
    GithubRepositoryViewsRepository githubRepositoryViewsRepository;
    @Autowired
    GithubRepositoryClonesRepository githubRepositoryClonesRepository;
    @Autowired
    RestTemplateBuilder builder;

    @PostConstruct
    private void init(){
        GithubAPI.setGithubUserRepository(githubUserRepository);
        GithubAPI.setGithubRepositoryRepository(githubRepositoryRepository);
        GithubAPI.setGithubRepositoryViewsRepository(githubRepositoryViewsRepository);
        GithubAPI.setGithubRepositoryClonesRepository(githubRepositoryClonesRepository);
        GithubAPI.setRestTemplate(builder.build());
    }
}
