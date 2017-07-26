package com.adw1n.hubtraffic.scheduled;

import com.adw1n.hubtraffic.models.GithubUser;
import com.adw1n.hubtraffic.respositories.GithubUserRepository;
import com.adw1n.hubtraffic.utils.GithubAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class TrafficInfoUpdater {
    @Autowired
    GithubUserRepository githubUserRepository;

    @Scheduled(fixedRate = 6*60*60*1000)
    public void updateTrafficInfo() {
        for(GithubUser user: githubUserRepository.findAll()){
            GithubAPI.fetchUpdates(user);
        }
    }
}
