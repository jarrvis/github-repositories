package com.recruit.githubrepositories.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Maps all Spring configuration properties prefixed with "api" to this class and its nested subclasses. JSR 303 bean
 * validation annotations are used to ensure reasonable values are injected.
 *
 */
@Data
@Configuration
@EnableConfigurationProperties(GithubConfig.class)
public class ApiConfiguration {

    public ApiConfiguration(
            GithubConfig githubConfig
    ){
        this.githubConfig = githubConfig;
    }

    private GithubConfig githubConfig;

    public GithubConfig getGithubConfig() {
        return githubConfig;
    }

    public void setGithubConfig(GithubConfig githubConfig) {
        this.githubConfig = githubConfig;
    }
}
