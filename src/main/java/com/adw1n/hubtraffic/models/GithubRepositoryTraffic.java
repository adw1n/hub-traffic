package com.adw1n.hubtraffic.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class GithubRepositoryTraffic {
    // TODO (timestamp, repository) unique
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private Date timestamp;
    private Integer count;
    private Integer uniques;
    @ManyToOne
    private GithubRepository repository;

    public GithubRepositoryTraffic(){}

    public GithubRepositoryTraffic(Date timestamp, Integer count, Integer uniques, GithubRepository repository) {
        this.timestamp = timestamp;
        this.count = count;
        this.uniques = uniques;
        this.repository = repository;
    }
}
