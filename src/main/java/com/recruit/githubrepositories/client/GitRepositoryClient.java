package com.recruit.githubrepositories.client;

import com.recruit.githubrepositories.api.dto.GithubRepositoryDetails;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.NotNull;
import java.util.Map;

public interface GitRepositoryClient {

    ResponseEntity<GithubRepositoryDetails> getRepositoryDetails (@NotNull final String owner, @NotNull final String repositoryName);

    ResponseEntity<GithubRepositoryDetails> getRepositoryDetails (@NotNull final String owner, @NotNull final String repositoryName, Map<String, String> headers);

    ResponseEntity<GithubRepositoryDetails> getCachedRepositoryDetails(@NotNull final String owner, @NotNull final String repositoryName);
}
