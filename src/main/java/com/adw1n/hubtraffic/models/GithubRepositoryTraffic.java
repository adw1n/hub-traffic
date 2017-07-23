package com.adw1n.hubtraffic.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames = {"timestamp" , "repository_id"})})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class GithubRepositoryTraffic {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private Date timestamp;
    @Column(nullable = false)
    private Integer count;
    @Column(nullable = false)
    private Integer uniques;

    @ManyToOne
    @JoinColumn(name="repository_id", nullable=false)
    private GithubRepository repository;

    public GithubRepositoryTraffic(){}

    public GithubRepositoryTraffic(Date timestamp, Integer count, Integer uniques, GithubRepository repository) {
        this.timestamp = timestamp;
        this.count = count;
        this.uniques = uniques;
        this.repository = repository;
    }
}
