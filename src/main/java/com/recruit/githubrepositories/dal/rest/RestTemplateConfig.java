package com.recruit.githubrepositories.dal.rest;

import com.google.common.collect.ImmutableList;
import com.recruit.githubrepositories.interceptor.LoggingRequestInterceptor;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class RestTemplateConfig {

    private final HttpClientBuilder httpClientBuilder = HttpClients.custom();

    public RestTemplateConfig(
            @Qualifier("loggingRequestInterceptor") final LoggingRequestInterceptor loggingRequestInterceptor
            ) {
        this.loggingRequestInterceptor = loggingRequestInterceptor;
    }

    private final LoggingRequestInterceptor loggingRequestInterceptor;

    @Bean
    @Qualifier("defaultRestTemplate")
    public RestTemplate restTemplateForGithub() {
        final List<ClientHttpRequestInterceptor> interceptors = ImmutableList.of(loggingRequestInterceptor);

        final RestTemplate restTemplate = new RestTemplate();
        httpClientBuilder.useSystemProperties();
        final HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClientBuilder.build());
        final BufferingClientHttpRequestFactory bufferingClientHttpRequestFactory = new BufferingClientHttpRequestFactory(httpComponentsClientHttpRequestFactory);
        restTemplate.setRequestFactory(bufferingClientHttpRequestFactory);
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }
}