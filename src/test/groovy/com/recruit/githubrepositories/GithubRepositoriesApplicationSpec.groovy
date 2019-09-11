package com.recruit.githubrepositories

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import spock.lang.Specification

import java.time.Duration

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GithubRepositoriesApplicationSpec extends Specification {

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
            def owner = "jarrvis"
            def repositoryName = "st-lazy"

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
                    .jsonPath('$.description').isNotEmpty()
                    .jsonPath('$.cloneUrl').isNotEmpty()
                    .jsonPath('$.stars').isNotEmpty()
                    .jsonPath('$.createdAt').isNotEmpty()
    }

    def "should access api controller, request github api over http client and return not found status"() {
        given:
            def owner = "jarrvis"
            def repositoryName = "st-lazyyyy"

        when: 'requesting not existing github repository'
            def result = webTestClient
                    .get()
                    .uri("/repositories/{owner}/{repo}", owner, repositoryName)
                    .exchange()
        then:
            result
                    .expectStatus().isNotFound()
    }

}