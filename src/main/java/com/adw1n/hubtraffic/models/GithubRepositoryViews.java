package com.adw1n.hubtraffic.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class GithubRepositoryViews {
    // TODO (timestamp, repository) unique
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private Date timestamp;
    private Integer count;
    private Integer uniques;
    @ManyToOne
    private GithubRepository repository;

    public GithubRepositoryViews(){}

    public GithubRepositoryViews(Date timestamp, Integer count, Integer uniques, GithubRepository repository) {
        this.timestamp = timestamp;
        this.count = count;
        this.uniques = uniques;
        this.repository = repository;
    }
}
