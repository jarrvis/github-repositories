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
import java.util.Set;

@Slf4j
@Service
public class GithubRepositoryService implements GitRepositoryService {

    public GithubRepositoryService(
            @Qualifier("githubApiClient") final GitApiClient githubRepositoryClient,
            final GitRepositoryMapper gitRepositoryMapper
    ) {
        this.githubRepositoryClient = githubRepositoryClient;
        this.gitRepositoryMapper = gitRepositoryMapper;
    }

    private final GitApiClient githubRepositoryClient;
    private final GitRepositoryMapper gitRepositoryMapper;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    /**
     * For given owner and repository name it will:
     * <ul>
     * <li>Check if response from Github API is already in cache</li>
     * <li>If response is in cache: it will do a request to Github API with <br>If-None-Match</br> header containing the value of <br>etag</> header from cached response</li>
     * <li>If response has 304 status then cached response will be used for further processing</li>
     * <li>If response has 200 status then current response will be used for further processing</li>
     * <li>In case response is not in cache (orElseGet section): Github API is requested</li>
     * <li>Response is processed: body is validated amd  mapped to DTO</li>
     * <li>Exceptions returned by api client are passed for processing in higher level</li>
     * </ul>
     *
     * @param owner
     * @param repositoryName
     * @return Repository details wrapped in Mono
     */
    @Override
    public Mono<RepositoryDetails> getRepositoryDetails(@NotNull final String owner, @NotNull final String repositoryName) {
        return this.getRepositoryDetailsResponse(owner, repositoryName)
                .flatMap(clientResponse -> clientResponse.bodyToMono(GithubRepositoryDetails.class))
                .doOnNext(t -> {
                    final Set<ConstraintViolation<GithubRepositoryDetails>> violations = validator.validate(t);
                    if (!violations.isEmpty()) {
                        throw new IllegalStateException("Github API response not valid");
                    }
                })
                .map(gitRepositoryMapper::githubRepositoryDetailsToRepositoryDetails);
    }

    /**
     * Get repository details response from cache or from Github API if not present in cache or not up to date
     *
     * @param owner
     * @param repositoryName
     * @return Repository details response wrapped in Mono
     */
    private Mono<ClientResponse> getRepositoryDetailsResponse(@NotNull String owner, @NotNull String repositoryName) {
        final Mono<ClientResponse> cachedResponseMono = githubRepositoryClient.getCachedRepositoryDetailsResponse(owner, repositoryName);
        if (cachedResponseMono != null) {
            return cachedResponseMono.flatMap(cachedResponse -> {
                final Mono<ClientResponse> apiResponse = this.githubRepositoryClient.getRepositoryDetailsResponse(owner, repositoryName, cachedResponse.headers().asHttpHeaders().getETag());
                return apiResponse.flatMap(r -> {
                    if (HttpStatus.NOT_MODIFIED == r.statusCode()) {
                        log.debug("Using cached response for repository: {}, owner: {}", repositoryName, owner);
                        return cachedResponseMono;
                    }
                    return apiResponse;
                });
            });
        } else {
            return this.githubRepositoryClient.getRepositoryDetailsResponse(owner, repositoryName, null);
        }
    }
}
