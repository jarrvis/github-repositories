package com.recruit.githubrepositories.facade;

import com.recruit.githubrepositories.api.dto.GithubRepositoryDetails;

public interface GitRepositoryFacade {

    GithubRepositoryDetails getGitRepositoryDetails(String owner, String repositoryName);
}
