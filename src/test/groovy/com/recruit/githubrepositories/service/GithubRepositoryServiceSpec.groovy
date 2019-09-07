package com.recruit.githubrepositories.service

import com.recruit.githubrepositories.api.dto.GithubRepositoryDetails
import com.recruit.githubrepositories.client.impl.GithubApiClient
import com.recruit.githubrepositories.service.impl.GithubRepositoryService
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
        githubRepositoryService = new GithubRepositoryService(githubApiClient)
    }

    def "Github repository service should return RepositoryDetails"() {
        given:
        def owner = "jarrvis"
        def repositoryName = "st-lazy"
        def githubRepositoryDetails = GithubRepositoryDetails.builder()
                .full_name("test name")
                .description("test description")
                .clone_url("https://test/clone")
                .created_at(LocalDate.now())
                .stargazers_count(0)
                .build()

        when:
        ClientResponse apiResponse = Mock(ClientResponse)
        apiResponse.bodyToMono(GithubRepositoryDetails.class) >> Mono.just(githubRepositoryDetails)
        githubApiClient.getCachedRepositoryDetails(_ as String, _ as String) >> null
        githubApiClient.getRepositoryDetails(_ as String, _ as String, null) >> Mono.just(apiResponse)

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
        def githubRepositoryDetails = GithubRepositoryDetails.builder()
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
        cachedResponse.bodyToMono(GithubRepositoryDetails.class) >> Mono.just(githubRepositoryDetails)
        githubApiClient.getRepositoryDetails(_ as String, _ as String, _ as String) >> Mono.just(apiResponse)
        githubApiClient.getCachedRepositoryDetails(_ as String, _ as String) >> Mono.just(cachedResponse)

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