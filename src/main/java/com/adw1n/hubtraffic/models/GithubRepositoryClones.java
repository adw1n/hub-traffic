package com.adw1n.hubtraffic.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class GithubRepositoryClones extends GithubRepositoryViews{
    public GithubRepositoryClones() {
    }
}
