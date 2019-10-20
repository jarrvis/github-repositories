package com.jarrvis.githubrepositories.client.impl;

import com.jarrvis.githubrepositories.client.GitApiClient;
import com.jarrvis.githubrepositories.configuration.ApiConfiguration;
import com.jarrvis.githubrepositories.configuration.GithubConfig;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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

/**
 * Main class for accessing Github API. All methods are non-blocking
 * It creates a default web client with:
 * <ul>
 *    <li>headers: >Accept : <br>application/vnd.github.v3+json</br> and User-Agent: <br>allegro-recruit</br></li>
 *    <li><clientId and clientSecret of existing Github OAuth app</li>
 * </ul>
 *
 * Puts to cache all 200 responses. Additionaly provides methods to read cached responses
 */
@Slf4j
@Component
public class GithubApiClient implements GitApiClient {


    public GithubApiClient(
            final WebClient.Builder webClientBuilder,
            final CacheManager cacheManager,
            @Qualifier("apiConfiguration") final ApiConfiguration apiConfiguration
    ) {
        this.apiConfiguration = apiConfiguration;
        this.githubConfig = this.apiConfiguration.getGithubConfig();
        this.cache = cacheManager.getCache(this.githubConfig.getCacheName());
        this.clientId = githubConfig.getClientId();
        this.clientSecret = githubConfig.getClientSecret();
        this.uri = githubConfig.getUri();

        HttpClient httpClient = this.githubConfig.isUsePoxy() ?
                HttpClient.create()
                        .tcpConfiguration(tcpClient -> tcpClient.proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP)
                                .host(this.githubConfig.getProxyHost())
                                .port(this.githubConfig.getProxyPort())))
                :
                HttpClient.create();


        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        this.webClient = webClientBuilder
                .clientConnector(connector)
                .baseUrl(this.githubConfig.getUrl())
                .defaultHeader(HttpHeaders.ACCEPT, this.githubConfig.getAccept())
                .defaultHeader(HttpHeaders.USER_AGENT, this.githubConfig.getUserAgent())
                .filter(logRequest())
                .build();

    }

    private final WebClient webClient;
    private final ApiConfiguration apiConfiguration;
    private final GithubConfig githubConfig;

    private final Cache cache;

    private final String clientId;
    private final String clientSecret;
    private final String uri;


    /**
     *  Successful response is put to cache. Spring cache abstraction cannot be used here since it does not yet support async condition:
     *  <p>@CachePut(value = "github_api_cache", key = "#owner + '_' + #repositoryName", condition = "#result != null && #result.<br>block()</br>.statusCode().is2xxSuccessful()")
     *  </p>
     *
     * @param owner
     * @param repositoryName
     * @return response entity from API
    */
    @Override
    public Mono<ClientResponse> getRepositoryDetailsResponse(@NotNull String owner, @NotNull String repositoryName, String etag) {

        final Mono<ClientResponse> res = this.webClient
                .get()
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
     * @return Mono client response from cache. if missing in cache null is returned.
     */
    @Override
    public Mono<ClientResponse> getCachedRepositoryDetailsResponse(@NotNull String owner, @NotNull String repositoryName) {
        return Try.of(() -> (Mono<ClientResponse>) this.cache.get(owner + "_" + repositoryName).get())
                .onSuccess((res) -> log.debug("Repository details not found in cache. Repo name: {}, owner: {}", repositoryName, owner))
                .getOrNull();
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
