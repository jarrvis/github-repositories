package com.recruit.githubrepositories.service.impl;

import com.recruit.githubrepositories.api.dto.GithubRepositoryDetails;
import com.recruit.githubrepositories.api.dto.response.RepositoryDetails;
import com.recruit.githubrepositories.client.GitApiClient;
import com.recruit.githubrepositories.converters.GitRepositoryMapper;
import com.recruit.githubrepositories.service.GitRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class GithubRepositoryService implements GitRepositoryService {

    public GithubRepositoryService(
            @Qualifier("githubApiClient") final GitApiClient githubRepositoryClient
    ) {
        this.githubRepositoryClient = githubRepositoryClient;
    }

    private final GitApiClient githubRepositoryClient;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    /**
     * @param owner
     * @param repositoryName
     * @return Response entity containing repository details in body and Etag in headers
     * Put response entity to cache only in case of 200 response
     */
    @Override
    public Mono<RepositoryDetails> getRepositoryDetails(@NotNull final String owner, @NotNull final String repositoryName) {
        return Optional.ofNullable(githubRepositoryClient.getCachedRepositoryDetails(owner, repositoryName))
                .map((cachedResponse) -> cachedResponse.flatMap(res -> {
                    final Mono<ClientResponse> apiResponse = this.githubRepositoryClient.getRepositoryDetails(owner, repositoryName, res.headers().asHttpHeaders().getETag());
                    return apiResponse.flatMap(r -> {
                        if (HttpStatus.NOT_MODIFIED.compareTo(r.statusCode()) == 0) {
                            log.debug("Using cached response for repository: {}, owner: {}", repositoryName, owner);
                            return cachedResponse;
                        }
                        return apiResponse;
                    });
                }))
                .orElseGet(() -> this.githubRepositoryClient.getRepositoryDetails(owner, repositoryName, null))
                .flatMap(clientResponse -> clientResponse.bodyToMono(GithubRepositoryDetails.class))
                .doOnNext(t -> {
                    final Set<ConstraintViolation<GithubRepositoryDetails>> violations = validator.validate(t);
                    if (!violations.isEmpty()) {
                        throw new IllegalStateException("Github API response not valid");
                    }
                })
                .map(GitRepositoryMapper.INSTANCE::githubRepositoryDetailsToRepositoryDetails);

    }

}
