package com.recruit.githubrepositories.client.impl;

import com.recruit.githubrepositories.api.dto.GithubRepositoryDetails;
import com.recruit.githubrepositories.client.GitRepositoryClient;
import com.recruit.githubrepositories.configuration.ApiConfiguration;
import com.recruit.githubrepositories.dal.rest.RestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Slf4j
@Component
public class GithubRepositoryClient implements GitRepositoryClient {


    public GithubRepositoryClient(
            @Qualifier("apiConfiguration") final ApiConfiguration apiConfiguration,
            @Qualifier("defaultRestTemplate") final RestTemplate restTemplate
    ) {
        this.apiConfiguration = apiConfiguration;
        this.restTemplate = restTemplate;
    }

    private final ApiConfiguration apiConfiguration;
    private final RestTemplate restTemplate;

    /**
     *
     * @param owner
     * @param repositoryName
     * @return response entity from API
     *
     * additionally response is put to cache (only if status is 200)
     */
    @Override
    @CachePut(value = "github_api_cache", key = "#owner + '_' + #repositoryName", condition = "#result != null && #result.getStatusCode().value() == 200")
    public ResponseEntity<GithubRepositoryDetails> getRepositoryDetails(@NotNull String owner, @NotNull String repositoryName) {
        final String url = this.apiConfiguration.getGithubConfig().getUrl();
        return RestClient.INSTANCE().GET(this.restTemplate, url, GithubRepositoryDetails.class, null, owner, repositoryName);
    }

    /**
     *
     * @param owner
     * @param repositoryName
     * @param headers
     * @return response entity from API
     *
     * additionally response is put to cache (only if status is 200)
     */
    @Override
    @CachePut(value = "github_api_cache", key = "#owner + '_' + #repositoryName", condition = "#result != null && #result.getStatusCode().value() == 200")
    public ResponseEntity<GithubRepositoryDetails> getRepositoryDetails(@NotNull String owner, @NotNull String repositoryName, Map<String, String> headers) {
        final String url = this.apiConfiguration.getGithubConfig().getUrl();
        return RestClient.INSTANCE().GET(this.restTemplate, url, GithubRepositoryDetails.class, headers, owner, repositoryName);
    }

    /**
     *
     * @param owner
     * @param repositoryName
     * @return response entity from cache. if missing in cache null is returned.
     * condition "#result != null" cannot be used here as null is always explicitly returned thus cache will not be checked
     *
     * helper method to make use of Spring caching abstraction instead of using CacheManager explicitly.
     */
    @Override
    @Cacheable(value = "github_api_cache", key = "#owner + '_' + #repositoryName")
    public ResponseEntity<GithubRepositoryDetails> getCachedRepositoryDetails(@NotNull String owner, @NotNull String repositoryName) {
        log.debug("Repository details not found in cache. Repo name: {}, owner: {}", repositoryName, owner);
        return null;
    }
}
