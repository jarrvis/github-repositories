package com.jarrvis.githubrepositories.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotEmpty;

/**
 * Configuration class to encapsulate all config values to setup the REST interface to the Github API.
 */
@Data
@ConfigurationProperties(prefix = "api.github")
public class GithubConfig {

    @NotEmpty
    private String url;

    @NotEmpty
    private String uri;

    @NotEmpty
    private String cacheName;

    @NotEmpty
    private String clientId;

    @NotEmpty
    private String clientSecret;

    @NotEmpty
    private String apiVersion;

    @NotEmpty
    private String accept;

    @NotEmpty
    private String userAgent;

    @NotEmpty
    private boolean usePoxy;

    @NotEmpty
    private String proxyHost;

    @NotEmpty
    private int proxyPort;
}
