package com.adw1n.hubtraffic.controllers;

import com.adw1n.hubtraffic.utils.GithubAPI;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {
    @RequestMapping("/api/user")
    public Principal user(Principal principal) {
        GithubAPI.fetchUpdates(principal);
//        GithubUser user = GithubAPI.getUser(principal);
        return principal;
    }
}
