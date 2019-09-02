package com.recruit.githubrepositories.interceptor;

import com.recruit.githubrepositories.configuration.ApiConfiguration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class GithubApiRequestInterceptor implements ClientHttpRequestInterceptor {

    private ApiConfiguration apiConfiguration;

    public GithubApiRequestInterceptor(final ApiConfiguration apiConfiguration) {
        this.apiConfiguration = apiConfiguration;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().add("User-Agent", this.apiConfiguration.getGithubConfig().getUserAgent());
        request.getHeaders().add("Accept", this.apiConfiguration.getGithubConfig().getAccept());
        return execution.execute(request, body);
    }
}
