package com.recruit.githubrepositories.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

@Slf4j
@Component
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {
    
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = null;
        long startTime = System.currentTimeMillis();
        try {
            logRequest(request, body);
            response = execution.execute(request, body);
        } finally {
            logResponse(response, System.currentTimeMillis() - startTime);
        }
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) throws IOException {
        if (!log.isDebugEnabled()) {
            return;
        }
        log.debug("URI         : {}", request.getURI());
        log.debug("Method      : {}", request.getMethod());
        log.debug("Headers     : {}", request.getHeaders());
        log.debug("Request body: {}", new String(body, "UTF-8"));
    }

    private void logResponse(ClientHttpResponse response, long processingTime) throws IOException {
        if (!log.isDebugEnabled()) {
            return;
        }

        if (response == null) {
            log.debug("Finished with empty response!");
            return;
        }
        log.debug("Status code  : {}", response.getStatusCode());
        log.debug("Status text  : {}", response.getStatusText());
        log.debug("Response time: {}", processingTime);
        log.debug("Headers      : {}", response.getHeaders());
        log.debug("Response body: {}", StreamUtils.copyToString(response.getBody(), determineCharset(response.getHeaders())));
    }

    protected Charset determineCharset(HttpHeaders headers) {
        MediaType contentType = headers.getContentType();
        if (contentType != null) {
            try {
                Charset charSet = contentType.getCharset();
                if (charSet != null) {
                    return charSet;
                }
            } catch (UnsupportedCharsetException e) {
                //go for default value on the bottom
            }
        }
        return StandardCharsets.UTF_8;
    }


}

