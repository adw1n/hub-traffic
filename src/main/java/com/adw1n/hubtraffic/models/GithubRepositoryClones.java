package com.adw1n.hubtraffic.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames = {"timestamp" , "repository_id"})})
public class GithubRepositoryClones extends GithubRepositoryTraffic{
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name="repository_id", nullable=false)
    private GithubRepository repository;

    public GithubRepositoryClones(Date timestamp, Integer count, Integer uniques, GithubRepository repository) {
        super(timestamp, count, uniques);
        this.repository = repository;
    }
}
