package com.jarrvis.githubrepositories.service


import com.jarrvis.githubrepositories.client.impl.GithubApiClient
import com.jarrvis.githubrepositories.converters.GitRepositoryMapper
import com.jarrvis.githubrepositories.service.impl.GithubRepositoryService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientResponse
import reactor.core.publisher.Mono
import spock.lang.Specification

import java.time.LocalDate

class GithubRepositoryServiceSpec extends Specification {

    private GithubRepositoryService githubRepositoryService
    private GithubApiClient githubApiClient


    def setup() {
        githubApiClient = Mock(GithubApiClient)
        githubRepositoryService = new GithubRepositoryService(githubApiClient, GitRepositoryMapper.INSTANCE)
    }

    def "Github repository service should return RepositoryDetails"() {
        given:
            def owner = "jarrvis"
            def repositoryName = "st-lazy"
            def githubRepositoryDetails = com.jarrvis.githubrepositories.api.dto.GithubRepositoryDetails.builder()
                    .full_name("test name")
                    .description("test description")
                    .clone_url("https://test/clone")
                    .created_at(LocalDate.now())
                    .stargazers_count(0)
                    .build()

        when:
            ClientResponse apiResponse = Mock(ClientResponse)
            apiResponse.bodyToMono(com.jarrvis.githubrepositories.api.dto.GithubRepositoryDetails.class) >> Mono.just(githubRepositoryDetails)
            githubApiClient.getCachedRepositoryDetailsResponse(_ as String, _ as String) >> null
            githubApiClient.getRepositoryDetailsResponse(_ as String, _ as String, null) >> Mono.just(apiResponse)

        and:
            def result = githubRepositoryService.getRepositoryDetails(owner, repositoryName).block()

        then:
            result.fullName == "test name"
            result.description == "test description"
            result.cloneUrl == "https://test/clone"
            result.createdAt == LocalDate.now()
            result.stars == 0
    }

    def "Github repository service should return RepositoryDetails from cache"() {
        given:
            def owner = "jarrvis"
            def repositoryName = "st-lazy"
            def githubRepositoryDetails = com.jarrvis.githubrepositories.api.dto.GithubRepositoryDetails.builder()
                    .full_name("cached test name")
                    .description("cached test description")
                    .clone_url("https://test/clone")
                    .created_at(LocalDate.now())
                    .stargazers_count(0)
                    .build()

            ClientResponse cachedResponse = Spy(ClientResponse.create(HttpStatus.OK).header(HttpHeaders.ETAG, "test").build())
            ClientResponse apiResponse = Mock(ClientResponse)

        when:
            apiResponse.statusCode() >> HttpStatus.NOT_MODIFIED
            cachedResponse.bodyToMono(com.jarrvis.githubrepositories.api.dto.GithubRepositoryDetails.class) >> Mono.just(githubRepositoryDetails)
            githubApiClient.getRepositoryDetailsResponse(_ as String, _ as String, _ as String) >> Mono.just(apiResponse)
            githubApiClient.getCachedRepositoryDetailsResponse(_ as String, _ as String) >> Mono.just(cachedResponse)

        and:
            def result = githubRepositoryService.getRepositoryDetails(owner, repositoryName).block()

        then:
            result.fullName == "cached test name"
            result.description == "cached test description"
            result.cloneUrl == "https://test/clone"
            result.createdAt == LocalDate.now()
            result.stars == 0
    }

}