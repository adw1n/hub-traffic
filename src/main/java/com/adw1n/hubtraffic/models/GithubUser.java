package com.adw1n.hubtraffic.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
public class GithubUser {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    @JsonIgnore
    private String token;

    @CreationTimestamp
    @Column(nullable = false)
    @JsonIgnore
    private Date joinDate;

    public GithubUser(String name, String token){
        this.name=name;
        this.token=token;
    }

    @Override
    public String toString() {
        return "GithubUser{" +
                "name='" + name + '\'' +
                ", joinDate=" + joinDate +
                '}';
    }
}
