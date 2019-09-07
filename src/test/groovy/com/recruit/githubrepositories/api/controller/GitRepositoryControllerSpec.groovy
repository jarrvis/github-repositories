package com.recruit.githubrepositories.api.controller

import com.recruit.githubrepositories.api.GitRepositoryController
import com.recruit.githubrepositories.api.dto.response.RepositoryDetails

import com.recruit.githubrepositories.service.GitRepositoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [GitRepositoryController])
class GitRepositoryControllerSpec extends Specification {

    // Can also be done with WebTestClient
    @Autowired
    protected MockMvc mvc

    @Autowired
    GitRepositoryService gitRepositoryService

    def "should pass 'owner' and 'repositoryName' to service and return 'OK' status"() {
        given:
        def owner = "jarrvis"
        def repositoryName = "st-lazy"
        and:
        gitRepositoryService.getRepositoryDetails(owner, repositoryName) >>  Mono.just(new RepositoryDetails(
                'jarrvis/st-lazy',
                'Web components for lazy loading - you can use them everywhere, without any dependency. Contains also @Lazy decorator for Stenciljs to call annotated method when component is scrolled to viewport. Web components are based on @Lazy',
                'https://github.com/jarrvis/st-lazy.git',
                11,
                LocalDate.parse("2019-01-29"))
        )

        when:
        def response = mvc.perform(get(String.format('/repositories/%s/%s',owner, repositoryName))).andReturn()
        def result = mvc.perform(asyncDispatch(response))

        then:
        result.andExpect(status().isOk())

        and:
        result.andExpect(jsonPath('$.fullName').value('jarrvis/st-lazy'))
        result.andExpect(jsonPath('$.description').value('Web components for lazy loading - you can use them everywhere, without any dependency. Contains also @Lazy decorator for Stenciljs to call annotated method when component is scrolled to viewport. Web components are based on @Lazy'))
        result.andExpect(jsonPath('$.cloneUrl').value('https://github.com/jarrvis/st-lazy.git'))
        result.andExpect(jsonPath('$.stars').value(11))
        result.andExpect(jsonPath('$.createdAt').value('2019-01-29'))
    }

    def "should pass 'owner' and 'repositoryName' to service and return 'not found' status"() {
        given:
        def owner = "jarrvis"
        def repositoryName = "st-lazyyyy"
        and:
        gitRepositoryService.getRepositoryDetails(owner, repositoryName) >> {throw WebClientResponseException.create(404,
                "Cannot get repository details, expected 2xx HTTP Status code", null, null, null
        )}
        when:
        def result = mvc.perform(get(String.format('/repositories/%s/%s',owner, repositoryName)))
        then:
        result.andExpect(status().isNotFound())
    }

    def "should pass 'owner' and 'repositoryName' to service and return 'too many requests' status"() {
        given:
        def owner = "jarrvis"
        def repositoryName = "st-lazyyyy"
        and:
        gitRepositoryService.getRepositoryDetails(owner, repositoryName) >> {throw WebClientResponseException.create(403,
                "Cannot get repository details, expected 2xx HTTP Status code", null, null, null
        )}
        when:
        def result = mvc.perform(get(String.format('/repositories/%s/%s',owner, repositoryName)))
        then:
        result.andExpect(status().isTooManyRequests())
    }

    def "should pass 'owner' and 'repositoryName' to service and return 'service unavailable' status"() {
        given:
        def owner = "jarrvis"
        def repositoryName = "st-lazyyyy"
        and:
        gitRepositoryService.getRepositoryDetails(owner, repositoryName) >> {throw new IOException("Proxy error")}
        when:
        def result = mvc.perform(get(String.format('/repositories/%s/%s',owner, repositoryName)))
        then:
        result.andExpect(status().isServiceUnavailable())
    }

    @TestConfiguration
    static class StubConfig {
        DetachedMockFactory detachedMockFactory = new DetachedMockFactory()

        @Bean
        GitRepositoryService githubRepositoryService() {
            return detachedMockFactory.Stub(GitRepositoryService)
        }
    }
}