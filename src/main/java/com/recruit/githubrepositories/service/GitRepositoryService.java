package com.recruit.githubrepositories.service;

import com.recruit.githubrepositories.api.dto.GithubRepositoryDetails;

import javax.validation.constraints.NotNull;

public interface GitRepositoryService {

    GithubRepositoryDetails getRepositoryDetails (@NotNull final String owner, @NotNull final String repositoryName);

    }
