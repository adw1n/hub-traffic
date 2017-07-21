package com.adw1n.hubtraffic.utils;

import com.adw1n.hubtraffic.models.GithubRepositoryClones;
import com.adw1n.hubtraffic.models.GithubRepositoryViews;
import lombok.Data;

import java.util.List;

@Data
public class GithubClonesResponse {
    private Integer count;
    private Integer uniques;
    private List<GithubRepositoryClones> clones;


    public GithubClonesResponse() {
    }

    public GithubClonesResponse(Integer count, Integer uniques, List<GithubRepositoryClones> clones) {
        this.count = count;
        this.uniques = uniques;
        this.clones = clones;
    }
}
