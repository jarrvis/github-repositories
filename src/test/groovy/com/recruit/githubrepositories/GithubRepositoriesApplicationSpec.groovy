package com.recruit.githubrepositories

import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import spock.lang.Specification

import java.time.Duration

import static com.github.tomakehurst.wiremock.client.WireMock.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "api.github.url=http://localhost:8080/api.github.com/repos")
class GithubRepositoriesApplicationSpec extends Specification {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    @Autowired
    private WebTestClient webTestClient;


    def setup() {

        webTestClient = webTestClient
                .mutate()
                .responseTimeout(Duration.ofMillis(30000))
                .build();
    }

    def "should access api controller, request github api over http client and return repository details json"() {
        given:
            stubFor(get(urlMatching("/api.github.com/repos/.*"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json;charset=UTF-8")
                            .withBodyFile("github-repo-details-200.json")))

        and:
            def owner = "allegro"
            def repositoryName = "bigcache"

        when: 'requesting existing github repository'
            def result = webTestClient
                    .get()
                    .uri("/repositories/{owner}/{repo}", owner, repositoryName)
                    .exchange()
        then:
            result
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath('$.fullName').isEqualTo(String.format("%s/%s", owner, repositoryName))
                    .jsonPath('$.description').isEqualTo("Efficient cache for gigabytes of data written in Go.")
                    .jsonPath('$.cloneUrl').isEqualTo("https://github.com/allegro/bigcache.git")
                    .jsonPath('$.stars').isEqualTo(2527)
                    .jsonPath('$.createdAt').isEqualTo("2016-03-23")
    }

    def "should access api controller, request github api over http client and return not found status"() {
        given:
            stubFor(get(urlMatching("/api.github.com/repos/.*"))
                    .willReturn(aResponse()
                            .withStatus(404)
                            .withHeader("Content-Type", "application/json;charset=UTF-8")
                            .withBodyFile("github-repo-details-404.json")))
        and:
            def owner = "allegro"
            def repositoryName = "tinycache"

        when: 'requesting non existing github repository'
            def result = webTestClient
                    .get()
                    .uri("/repositories/{owner}/{repo}", owner, repositoryName)
                    .exchange()
        then:
            result
                    .expectStatus().isNotFound()

    }

    def "should access api controller, request github api over http client and return too many requests status"() {
        given:
            stubFor(get(urlMatching("/api.github.com/repos/.*"))
                    .willReturn(aResponse()
                            .withStatus(403)
                            .withHeader("Content-Type", "application/json;charset=UTF-8")
                            .withBodyFile("github-repo-details-403.json")))
        and:
            def owner = "allegro"
            def repositoryName = "bigcache"

        when: 'requesting existing github repository with too high rate'
            def result = webTestClient
                    .get()
                    .uri("/repositories/{owner}/{repo}", owner, repositoryName)
                    .exchange()
        then:
            result.expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)

    }
}