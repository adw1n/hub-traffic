package com.adw1n.hubtraffic.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class GithubUser {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;


    public GithubUser(){}
    public GithubUser(String name){
        this.name=name;
    }
}
