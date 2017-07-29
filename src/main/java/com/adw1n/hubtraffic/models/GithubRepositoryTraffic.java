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
//@Table(uniqueConstraints={@UniqueConstraint(columnNames = {"timestamp" , "repository_id"})})
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


    // long story short, OnDelete does not work in the inherited class (dunno why)
    // meaning when I let hibernate create the DDL, it uses ON DELETE NO ACTION for the foreign key constraint which sucks
    // for now I copy pasted the repository field to both GithubRepositoryViews and GithubRepositoryClones
//    @ManyToOne
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JoinColumn(name="repository_id", nullable=false)
//    private GithubRepository repository;


    public GithubRepositoryTraffic(Date timestamp, Integer count, Integer uniques) {
        this.timestamp = timestamp;
        this.count = count;
        this.uniques = uniques;
    }
//    public GithubRepositoryTraffic(Date timestamp, Integer count, Integer uniques, GithubRepository repository) {
//        this.timestamp = timestamp;
//        this.count = count;
//        this.uniques = uniques;
//        this.repository = repository;
//    }
}
