package com.adw1n.hubtraffic.controllers;

import com.adw1n.hubtraffic.models.GithubRepository;
import com.adw1n.hubtraffic.models.GithubUser;
import com.adw1n.hubtraffic.respositories.GithubRepositoryRepository;
import com.adw1n.hubtraffic.respositories.GithubUserRepository;
import com.adw1n.hubtraffic.utils.GithubAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    GithubRepositoryRepository githubRepositoryRepository;
    @Autowired
    GithubUserRepository githubUserRepository;


    @RequestMapping(path="/api/user", method= RequestMethod.GET)
    public Principal getUser(Principal principal) {
        GithubUser user = GithubAPI.getUser(principal);
        return principal;
    }

    @RequestMapping(path="/api/user", method= RequestMethod.DELETE)
    public void deleteUser(Principal principal) {
        GithubUser user = GithubAPI.getUser(principal);
        githubUserRepository.delete(user);
    }

    @RequestMapping("/api/githubUser")
    public GithubUser githubUser(Principal principal) {
        GithubUser user = GithubAPI.getUser(principal);
        return user;
    }

    @RequestMapping("/api/userRepositories")
    public List<GithubRepository> userRepositories(Principal principal) {
        GithubUser user = GithubAPI.getUser(principal);
        return githubRepositoryRepository.findByUser(user);
    }

}
