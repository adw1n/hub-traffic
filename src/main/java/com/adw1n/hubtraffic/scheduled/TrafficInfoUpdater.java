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

    private static final Logger log = LoggerFactory.getLogger(TrafficInfoUpdater.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 6*60*60*1000)
    public void updateTrafficInfo() {
        for(GithubUser user: githubUserRepository.findAll()){
            GithubAPI.fetchUpdates(user);
            log.info("Updated traffic info for user {} at {}", user.getName(), dateFormat.format(new Date()));
        }

    }
}
