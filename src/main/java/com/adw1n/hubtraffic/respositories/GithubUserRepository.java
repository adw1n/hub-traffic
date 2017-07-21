package com.adw1n.hubtraffic.respositories;

import com.adw1n.hubtraffic.models.GithubUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GithubUserRepository extends CrudRepository<GithubUser, Long> {
    GithubUser findByName(String name);
}
