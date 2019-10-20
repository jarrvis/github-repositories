package com.jarrvis.githubrepositories.patterns;

import io.vavr.Tuple;
import io.vavr.Tuple5;
import io.vavr.match.annotation.Patterns;
import io.vavr.match.annotation.Unapply;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

@Patterns
class GithubRepositories {

    @Unapply
    static Tuple5<Integer, String, HttpHeaders, ByteArrayInputStream, Charset> WebClientResponseException(
            WebClientResponseException ex) {
        return Tuple.of(
                ex.getRawStatusCode(),
                ex.getMessage(),
                ex.getHeaders(),
                null,
                null
        );
    }

}
