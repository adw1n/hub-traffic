package com.adw1n.hubtraffic.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames = {"name" , "user_id"})})
public class GithubRepository {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String name;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE) //https://stackoverflow.com/questions/7197181/jpa-unidirectional-many-to-one-and-cascading-delete
    @JoinColumn(name="user_id", nullable=false)
    private GithubUser user;

    public GithubRepository(String name, GithubUser user){
        this.name=name;
        this.user=user;
    }
}
