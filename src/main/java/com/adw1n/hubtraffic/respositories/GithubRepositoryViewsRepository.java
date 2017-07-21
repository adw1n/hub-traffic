package com.adw1n.hubtraffic.respositories;

import com.adw1n.hubtraffic.models.GithubRepository;
import com.adw1n.hubtraffic.models.GithubRepositoryViews;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GithubRepositoryViewsRepository extends CrudRepository<GithubRepositoryViews, Long> {
    List<GithubRepositoryViews> findByRepository(GithubRepository repository);
    GithubRepositoryViews findByRepositoryAndTimestamp(GithubRepository repository, Date timestamp);
}
