package com.adw1n.hubtraffic.respositories;

import com.adw1n.hubtraffic.models.GithubRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GithubRepositoryRepository extends CrudRepository<GithubRepository, Long> {
    GithubRepository findByName(String name);
}
