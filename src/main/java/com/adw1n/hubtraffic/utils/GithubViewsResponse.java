package com.adw1n.hubtraffic.utils;

import com.adw1n.hubtraffic.models.GithubRepositoryViews;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GithubViewsResponse {
    private Integer count;
    private Integer uniques;
    private List<GithubRepositoryViews> views;
}
