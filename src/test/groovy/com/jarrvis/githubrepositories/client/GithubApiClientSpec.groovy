package com.jarrvis.githubrepositories.client

import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.jarrvis.githubrepositories.client.impl.GithubApiClient
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import spock.lang.Specification

import java.time.LocalDate

import static com.github.tomakehurst.wiremock.client.WireMock.*


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "api.github.url=http://localhost:8080/api.github.com/repos")
class GithubApiClientSpec extends Specification {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private GithubApiClient githubApiClient;

    def "should throw #exception on #statusCode response while retrieving resource"() {
        given:
            stubFor(get(urlMatching("/api.github.com/repos/.*"))
                    .willReturn(aResponse()
                            .withStatus(statusCode)
                            .withHeader("Content-Type", "application/json;charset=UTF-8")))

        when: 'requesting github repository'
            githubApiClient.getRepositoryDetailsResponse('allegro', 'tinycache', null).block()

        then:
            thrown(exception)

        where:
            statusCode | exception
            404        | WebClientResponseException.NotFound
            403        | WebClientResponseException.Forbidden
    }

    def "should return json body with repository details with status 'ok'"() {
        given:
            stubFor(get(urlMatching("/api.github.com/repos/.*"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json;charset=UTF-8")
                            .withBodyFile("github-repo-details-200.json")))

        when: 'requesting github repository'
            def result = githubApiClient.getRepositoryDetailsResponse('allegro', 'bigcache', null).block()


        then:
            result.statusCode() == HttpStatus.OK
        and:
            def res = result.bodyToMono(com.jarrvis.githubrepositories.api.dto.GithubRepositoryDetails.class)
                    .block()
            res.full_name == "allegro/bigcache"
            res.description == "Efficient cache for gigabytes of data written in Go."
            res.clone_url == "https://github.com/allegro/bigcache.git"
            res.created_at == LocalDate.parse("2016-03-23")
            res.stargazers_count == 2527

    }

}