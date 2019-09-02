package com.recruit.githubrepositories.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotEmpty;

@Data
@ConfigurationProperties(prefix = "api.github")
public class GithubConfig {
    /**
     * Configuration lass to encapsulate all config values to setup the REST interface to the Github API.
     */

    @NotEmpty
    private String url;

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
}
