package com.adw1n.hubtraffic.controllers;

import com.adw1n.hubtraffic.models.GithubRepository;
import com.adw1n.hubtraffic.models.GithubRepositoryViews;
import com.adw1n.hubtraffic.models.GithubUser;
import com.adw1n.hubtraffic.respositories.GithubRepositoryRepository;
import com.adw1n.hubtraffic.respositories.GithubRepositoryViewsRepository;
import com.adw1n.hubtraffic.utils.GithubAPI;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Data
@RestController
public class RepoViewsController {
    @Autowired
    private final GithubRepositoryRepository githubRepositoryRepository;
    @Autowired
    private final GithubRepositoryViewsRepository githubRepositoryViewsRepository;

    @RequestMapping(value = "/api/repository/views/{repositoryName}", method = RequestMethod.GET)
    public ResponseEntity<List<GithubRepositoryViews>> getViews(@PathVariable("repositoryName") String repositoryName, Principal principal){
        GithubUser user = GithubAPI.getUser(principal);
        if(user==null)
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        GithubRepository githubRepository = githubRepositoryRepository.findByNameAndUser(repositoryName, user);
        if(githubRepository==null)
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        List<GithubRepositoryViews> repoViews = githubRepositoryViewsRepository.findByRepository(githubRepository);
        return ResponseEntity.ok(repoViews);
    }
}
