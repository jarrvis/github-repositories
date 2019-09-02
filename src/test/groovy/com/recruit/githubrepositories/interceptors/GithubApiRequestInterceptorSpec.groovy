package com.recruit.githubrepositories.interceptors

import com.recruit.githubrepositories.configuration.ApiConfiguration
import com.recruit.githubrepositories.configuration.GithubConfig
import com.recruit.githubrepositories.interceptor.GithubApiRequestInterceptor
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import spock.lang.Specification

class GithubApiRequestInterceptorSpec extends Specification {

    HttpRequest request
    ApiConfiguration apiConfiguration
    ClientHttpRequestExecution execution

    def setup() {
        request = Mock(HttpRequest)
        apiConfiguration = Mock(ApiConfiguration)
        execution = Mock(ClientHttpRequestExecution)
        GithubConfig githubConfig = Mock(GithubConfig)
        HttpHeaders headers = new HttpHeaders()

        request.getHeaders() >> headers

        apiConfiguration.getGithubConfig() >> githubConfig
        githubConfig.getAccept() >> 'application/vnd.github.v3+json'
        githubConfig.getUserAgent() >> 'allegro-recruit'
    }

    def "should add 'Accept and 'User-Agent' headers to request"() {
        given:
        GithubApiRequestInterceptor githubApiRequestInterceptor = new GithubApiRequestInterceptor(apiConfiguration)

        when:
        githubApiRequestInterceptor.intercept(request, null, execution)

        then:
        request.getHeaders().getFirst('Accept') == 'application/vnd.github.v3+json'
        request.getHeaders().getFirst('User-Agent') == 'allegro-recruit'
    }
}