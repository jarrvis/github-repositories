package com.jarrvis.githubrepositories.api;


import com.jarrvis.githubrepositories.api.dto.response.RepositoryDetails;
import com.jarrvis.githubrepositories.service.GitRepositoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import javax.naming.ServiceUnavailableException;


/**
 * REST API endpoint for git repositories related operations
 */
@Slf4j
@RestController
@RequestMapping(value = "/repositories")
public class GitRepositoryController {

    public GitRepositoryController(
            final GitRepositoryService gitRepositoryService) {
        this.gitRepositoryService = gitRepositoryService;
    }

    private final GitRepositoryService gitRepositoryService;

    /**
     * REST operation to fetch git repo details based on owner and repository name
     * 
     * @param owner          Git repo owner name
     * @param repositoryName Git repository name
     * @return
     */
    @RequestMapping(value = "/{owner}/{repository-name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Fetches git repository details based on repository name and owner", response = RepositoryDetails.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Repository details found"),
            @ApiResponse(code = 404, message = "Repository not found"),
            @ApiResponse(code = 429, message = "In case of too high requests rate"),
            @ApiResponse(code = 503, message = "In case of external api connection issues")
    })
    public Mono<RepositoryDetails> getRepository(
            @ApiParam(value = "The username on git repository", required = true) @PathVariable("owner") String owner,
            @ApiParam(value = "The name of git repository", required = true) @PathVariable("repository-name") String repositoryName) throws Exception {

        log.debug("Fetch repository details for user: {}, repository: {}", owner, repositoryName);
        return Try.of(() -> this.gitRepositoryService.getRepositoryDetails(owner, repositoryName))
                .getOrElseThrow((exp) -> {
                    if (exp instanceof WebClientResponseException){
                        return (WebClientResponseException) exp;
                    }
                    return new ServiceUnavailableException("External api connection issue");
                });

    }

}
