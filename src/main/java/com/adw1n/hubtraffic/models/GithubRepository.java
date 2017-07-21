package com.adw1n.hubtraffic.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class GithubRepository {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String name;
    @ManyToOne
    private GithubUser user;

    public GithubRepository(){}
    public GithubRepository(String name, GithubUser user){
        this.name=name;
        this.user=user;
    }
}
