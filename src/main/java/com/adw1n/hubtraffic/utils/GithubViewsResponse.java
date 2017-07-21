package com.adw1n.hubtraffic.utils;

import com.adw1n.hubtraffic.models.GithubRepositoryViews;
import lombok.Data;

import java.util.List;

@Data
public class GithubViewsResponse {
    private Integer count;
    private Integer uniques;
    private List<GithubRepositoryViews> views;


    public GithubViewsResponse() {
    }

    public GithubViewsResponse(Integer count, Integer uniques, List<GithubRepositoryViews> views) {
        this.count = count;
        this.uniques = uniques;
        this.views = views;
    }
}
