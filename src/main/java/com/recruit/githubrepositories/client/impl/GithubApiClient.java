package com.recruit.githubrepositories.client.impl;

import com.recruit.githubrepositories.client.GitApiClient;
import com.recruit.githubrepositories.configuration.ApiConfiguration;
import com.recruit.githubrepositories.configuration.GithubConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

import javax.validation.constraints.NotNull;

@Slf4j
@Component
public class GithubApiClient implements GitApiClient {


    public GithubApiClient(
            WebClient.Builder webClientBuilder,
            CacheManager cacheManager,
            @Qualifier("apiConfiguration") final ApiConfiguration apiConfiguration
    ) {
        this.apiConfiguration = apiConfiguration;
        this.githubConfig = this.apiConfiguration.getGithubConfig();

        this.cache = cacheManager.getCache("github_api_cache");

        HttpClient httpClient = HttpClient.create();
        if (this.githubConfig.isUsePoxy()) {
            httpClient = HttpClient.create().tcpConfiguration(tcpClient -> tcpClient.proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP)
                    .host(this.githubConfig.getProxyHost())
                    .port(this.githubConfig.getProxyPort())));
        }

        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        this.webClient = webClientBuilder
                .clientConnector(connector)
                .baseUrl(this.githubConfig.getUrl())
                .defaultHeader(HttpHeaders.ACCEPT, this.githubConfig.getAccept())
                .defaultHeader(HttpHeaders.USER_AGENT, this.githubConfig.getUserAgent())
                .filter(logRequest())
                .build();


        this.clientId = githubConfig.getClientId();
        this.clientSecret = githubConfig.getClientSecret();
        this.uri = githubConfig.getUri();
    }

    private final WebClient webClient;
    private final ApiConfiguration apiConfiguration;
    private final GithubConfig githubConfig;

    private final Cache cache;

    private final String clientId;
    private final String clientSecret;
    private final String uri;


    /**
     * @param owner
     * @param repositoryName
     * @return response entity from API
     * <p>
     * Successful response is put to cache. Spring cache abstraction cannot be used here since it does not support async condition:
     * @CachePut(value = "github_api_cache", key = "#owner + '_' + #repositoryName", condition = "#result != null && #result.block().statusCode().is2xxSuccessful()")
     *
     */
    @Override
    public Mono<ClientResponse> getRepositoryDetails(@NotNull String owner, @NotNull String repositoryName, String etag) {

        final Mono<ClientResponse> res = this.webClient.get()
                .uri(this.uri, owner, repositoryName, clientId, clientSecret)
                .ifNoneMatch(etag)
                .exchange();

        return res.doOnNext(response -> {
            if (response.statusCode().isError()) {
                throw WebClientResponseException.create(response.rawStatusCode(),
                        "Cannot get repository details, expected 2xx HTTP Status code", null, null, null
                );
            }

            if (response.statusCode().is2xxSuccessful()) {
                this.cache.put(owner + "_" + repositoryName, res);
            }
        });
    }


    /**
     * @param owner
     * @param repositoryName
     * @return response entity from cache. if missing in cache null is returned.
     * condition "#result != null" cannot be used here as null is always explicitly returned thus cache will not be checked
     * <p>
     * helper method to stick to Spring caching abstraction instead of using CacheManager explicitly.
     */
    @Override
    @Cacheable(value = "github_api_cache", key = "#owner + '_' + #repositoryName")
    public Mono<ClientResponse> getCachedRepositoryDetails(@NotNull String owner, @NotNull String repositoryName) {
        log.debug("Repository details not found in cache. Repo name: {}, owner: {}", repositoryName, owner);
        return null;
    }


    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers()
                    .forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return next.exchange(clientRequest);
        };
    }
}
