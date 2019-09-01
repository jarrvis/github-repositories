package com.recruit.githubrepositories.api;


import com.recruit.githubrepositories.api.dto.response.RepositoryDetails;
import com.recruit.githubrepositories.api.exception.NotFoundException;
import com.recruit.githubrepositories.converters.GitRepositoryMapper;
import com.recruit.githubrepositories.facade.GitRepositoryFacade;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;


/**
 * REST API endpoint for git repositories related operations
 */
@Slf4j
@RestController
@RequestMapping(value = "/repositories")
public class GitRepositoryController {

    public GitRepositoryController(
            @Qualifier("defaultGitRepositoryFacade") final GitRepositoryFacade gitRepositoryFacade) {
        this.gitRepositoryFacade = gitRepositoryFacade;
    }

    private final GitRepositoryFacade gitRepositoryFacade;

    /**
     * REST operation to fetch git repo details based on owner and repository name
     *
     * @param owner          Git repo owner name
     * @param repositoryName Git repository name
     */
    @RequestMapping(value = "/{owner}/{repository-name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Fetches git repository details based on repository name and owner",
            notes = "Returns 404 status in case: <ol>\"\n" +
                    "+ \"<li>Repository was not found for owner and name passed</li>\" " +
                    "+ \"<li>HTTP connection failure</li>\"" +
                    "</ol>", response = RepositoryDetails.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Repository details found"),
            @ApiResponse(code = 404, message = "Repository not found"),
            @ApiResponse(code = 422, message = "In case of validation errors on the provided data")})
    public ResponseEntity<RepositoryDetails> getRepository(
            @ApiParam(value = "The username on git repository", required = true) @PathVariable("owner") @NotBlank String owner,
            @ApiParam(value = "The name of git repository", required = true) @PathVariable("repository-name") @NotBlank String repositoryName) {

        log.debug("Fetch repository details for user: {}, repository: {}", owner, repositoryName);

        RepositoryDetails repositoryDetails =
                Try.of(() -> this.gitRepositoryFacade.getGitRepositoryDetails(owner, repositoryName))
                        .map(repoDetails -> GitRepositoryMapper.INSTANCE.githubRepositoryDetailsToRepositoryDetails(repoDetails))
                        .getOrElseThrow(() -> new NotFoundException("repository not found"));

        return ResponseEntity.ok(repositoryDetails);
    }

}
