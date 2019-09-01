package com.recruit.githubrepositories.facade.impl;

import com.recruit.githubrepositories.api.dto.GithubRepositoryDetails;
import com.recruit.githubrepositories.facade.GitRepositoryFacade;
import com.recruit.githubrepositories.service.GitRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Added only for open implementation: other services can be added later e.g. Bitbucket, Gitlab
 */
@Component
@Slf4j
public class DefaultGitRepositoryFacade implements GitRepositoryFacade {

    public DefaultGitRepositoryFacade(
            @Qualifier("githubRepositoryService") final GitRepositoryService gitRepositoryService) {
        this.gitRepositoryService = gitRepositoryService;
    }

    private final GitRepositoryService gitRepositoryService;

    @Override
    public GithubRepositoryDetails getGitRepositoryDetails(String owner, String repositoryName) {
        return this.gitRepositoryService.getRepositoryDetails(owner, repositoryName);
    }
}
