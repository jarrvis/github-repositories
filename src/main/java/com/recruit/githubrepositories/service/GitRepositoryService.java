package com.recruit.githubrepositories.service;

import com.recruit.githubrepositories.api.dto.response.RepositoryDetails;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

public interface GitRepositoryService {

    Mono<RepositoryDetails> getRepositoryDetails(@NotNull final String owner, @NotNull final String repositoryName);

}
