package com.recruit.githubrepositories.api.controller

import com.recruit.githubrepositories.api.GitRepositoryController
import com.recruit.githubrepositories.api.dto.GithubRepositoryDetails
import com.recruit.githubrepositories.api.exception.NotFoundException
import com.recruit.githubrepositories.facade.GitRepositoryFacade
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import spock.mock.DetachedMockFactory
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

import java.time.LocalDate

@WebMvcTest(controllers = [GitRepositoryController])
class GitRepositoryControllerSpec extends Specification {

    @Autowired
    protected MockMvc mvc

    @Autowired
    GitRepositoryFacade gitRepositoryFacade

    def "should pass 'owner' and 'repositoryName' to facade and return 'OK' status"() {
        given:
        def owner = "jarrvis"
        def repositoryName = "st-lazy"
        and:
        gitRepositoryFacade.getGitRepositoryDetails(owner, repositoryName) >> new GithubRepositoryDetails(
                'jarrvis/st-lazy',
                'Web components for lazy loading - you can use them everywhere, without any dependency. Contains also @Lazy decorator for Stenciljs to call annotated method when component is scrolled to viewport. Web components are based on @Lazy',
                'https://github.com/jarrvis/st-lazy.git',
                11,
                LocalDate.parse("2019-01-29")
        )

        when:
        def result = mvc.perform(get(String.format('/repositories/%s/%s',owner, repositoryName)))
        then:
        result.andExpect(status().isOk())

        and:
        result.andExpect(jsonPath('fullName').value('jarrvis/st-lazy'))
        result.andExpect(jsonPath('$.description').value('Web components for lazy loading - you can use them everywhere, without any dependency. Contains also @Lazy decorator for Stenciljs to call annotated method when component is scrolled to viewport. Web components are based on @Lazy'))
        result.andExpect(jsonPath('$.cloneUrl').value('https://github.com/jarrvis/st-lazy.git'))
        result.andExpect(jsonPath('$.stars').value(11))
        result.andExpect(jsonPath('$.createdAt').value('2019-01-29'))
    }

    def "should pass 'owner' and 'repositoryName' to facade and return 'not found' status"() {
        given:
        def owner = "jarrvis"
        def repositoryName = "st-lazyyyy"
        and:
        gitRepositoryFacade.getGitRepositoryDetails(owner, repositoryName) >> {throw new NotFoundException ("Repository not found")}
        when:
        def result = mvc.perform(get(String.format('/repositories/%s/%s',owner, repositoryName)))
        then:
        result.andExpect(status().isNotFound())
    }

    @TestConfiguration
    static class StubConfig {
        DetachedMockFactory detachedMockFactory = new DetachedMockFactory()

        @Bean
        GitRepositoryFacade defaultGitRepositoryFacade() {
            return detachedMockFactory.Stub(GitRepositoryFacade)
        }
    }
}