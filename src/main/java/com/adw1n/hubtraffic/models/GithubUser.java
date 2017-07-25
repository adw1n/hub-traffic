package com.adw1n.hubtraffic.models;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class GithubUser {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(nullable = false)
    private String token; // TODO not serializable etc.

    @CreationTimestamp
    @Column(nullable = false)
    private Date joinDate;

    public GithubUser(){}
    public GithubUser(String name, String token){
        this.name=name;
        this.token=token;
    }
}
