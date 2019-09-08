package com.recruit.githubrepositories.client;

import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

public interface GitApiClient {

    Mono<ClientResponse> getRepositoryDetailsResponse(@NotNull String owner, @NotNull String repositoryName, String etag);

    Mono<ClientResponse> getCachedRepositoryDetailsResponse(@NotNull final String owner, @NotNull final String repositoryName);
}
