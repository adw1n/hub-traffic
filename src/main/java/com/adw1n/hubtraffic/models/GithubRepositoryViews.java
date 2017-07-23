package com.adw1n.hubtraffic.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class GithubRepositoryViews extends GithubRepositoryTraffic{
    public GithubRepositoryViews(){}

    public GithubRepositoryViews(Date timestamp, Integer count, Integer uniques, GithubRepository repository) {
        super(timestamp, count, uniques, repository);
    }
}
