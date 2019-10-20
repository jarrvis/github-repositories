package com.jarrvis.githubrepositories.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

/**
 * Maps all Spring configuration properties prefixed with "api" to this class and its nested subclasses. JSR 303 bean
 * validation annotations are used to ensure reasonable values are injected.
 *
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "api")
@EnableConfigurationProperties(GithubConfig.class)
public class ApiConfiguration {

    public ApiConfiguration(
            GithubConfig githubConfig
    ){
        this.githubConfig = githubConfig;
    }

    @NotNull
    private GithubConfig githubConfig;

    private boolean usePoxy;

    private boolean proxyHost;

    private boolean proxyPort;

}
