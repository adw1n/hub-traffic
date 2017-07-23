package com.adw1n.hubtraffic.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames = {"name" , "user_id"})})
public class GithubRepository {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private GithubUser user;
    // TODO creation date (when no clones/visitors start drawing from creation date to now)
    public GithubRepository(){}
    public GithubRepository(String name, GithubUser user){
        this.name=name;
        this.user=user;
    }
}
