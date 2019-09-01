package com.recruit.githubrepositories.service.impl;

import com.google.common.collect.ImmutableMap;
import com.recruit.githubrepositories.api.dto.GithubRepositoryDetails;
import com.recruit.githubrepositories.api.exception.NotAcceptableException;
import com.recruit.githubrepositories.client.GitRepositoryClient;
import com.recruit.githubrepositories.service.GitRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class GithubRepositoryService implements GitRepositoryService {

    public GithubRepositoryService(
            @Qualifier("githubRepositoryClient") final GitRepositoryClient githubRepositoryClient
    ) {
        this.githubRepositoryClient = githubRepositoryClient;
    }

    private final GitRepositoryClient githubRepositoryClient;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    /**
     * @param owner
     * @param repositoryName
     * @return Response entity containing repository details in body and Etag in headers
     * Put response entity to cache only in case of 200 response
     */
    @Override
    public GithubRepositoryDetails getRepositoryDetails(@NotNull final String owner, @NotNull final String repositoryName) {
        ResponseEntity<GithubRepositoryDetails> response =
                Optional.ofNullable(githubRepositoryClient.getCachedRepositoryDetails(owner, repositoryName))
                        .map((cachedResponse) -> {
                            final String etag = cachedResponse.getHeaders().getETag();
                            final Map headers = ImmutableMap.of(HttpHeaders.IF_NONE_MATCH, etag);
                            final ResponseEntity<GithubRepositoryDetails> res = this.githubRepositoryClient.getRepositoryDetails(owner, repositoryName, headers);
                            if (HttpStatus.NOT_MODIFIED.compareTo(res.getStatusCode()) == 0) {
                                log.debug("Using cached response for repository: {}, owner: {}, last modified date: {}", repositoryName, owner, res.getHeaders().getLastModified());
                                return cachedResponse;
                            }
                            return res;
                        })
                        .orElseGet(() -> this.githubRepositoryClient.getRepositoryDetails(owner, repositoryName));


        GithubRepositoryDetails githubRepositoryDetails = response.getBody();

        final Set<ConstraintViolation<GithubRepositoryDetails>> violations = validator.validate(githubRepositoryDetails);
        if (!violations.isEmpty()) {
            throw new NotAcceptableException("Received repository details are not valid", violations);
        }
        return response.getBody();
    }

}
