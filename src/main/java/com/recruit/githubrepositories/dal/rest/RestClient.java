package com.recruit.githubrepositories.dal.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.Map;

@Slf4j
public class RestClient {

    private RestClient() {
    }

    private static RestClient instance = new RestClient();

    public static RestClient INSTANCE() {
        return instance;
    }

    public <REQ, RES> ResponseEntity<RES> POST(RestTemplate rt, REQ request, String url, Class<RES> type,
                                               Object... vars) {
        URI uri = new UriTemplate(url).expand(vars);
        return POST(rt, request, uri, type);
    }

    public <RES> ResponseEntity<RES> GET(RestTemplate rt, String url, Class<RES> type, Map<String, String> headers, Object... vars) {
        URI uri = new UriTemplate(url).expand(vars);
        return GET(rt, uri, headers, type);
    }

    public <RES> ResponseEntity<RES> DELETE(RestTemplate rt, String url, Class<RES> type, Object... vars) {
        URI uri = new UriTemplate(url).expand(vars);
        return DELETE(rt, uri, type);
    }

    public <REQ, RES> ResponseEntity<RES> PUT(RestTemplate rt, REQ request, String url, Class<RES> type,
                                              Object... vars) {
        URI uri = new UriTemplate(url).expand(vars);
        return PUT(rt, request, uri, type);
    }


    public <REQ, RES> ResponseEntity<RES> PUT(RestTemplate rt, REQ request, URI uri, Class<RES> type) {
        BodyBuilder bodyBuilder = putBodyBuilder(uri);
        RequestEntity<REQ> requestEntity = bodyBuilder.body(request);
        final ResponseEntity<RES> responseEntity = rt.exchange(requestEntity, type);
        return responseEntity;
    }

    public <RES> ResponseEntity<RES> DELETE(RestTemplate rt, URI uri, Class<RES> type) {
        BodyBuilder bodyBuilder = deleteBodyBuilder(uri);
        RequestEntity<?> requestEntity = bodyBuilder.build();
        final ResponseEntity<RES> responseEntity = rt.exchange(requestEntity, type);
        return responseEntity;
    }

    public <REQ, RES> ResponseEntity<RES> POST(RestTemplate rt, REQ request, URI uri, Class<RES> type) {
        BodyBuilder bodyBuilder = postBodyBuilder(uri);
        RequestEntity<REQ> requestEntity = bodyBuilder.body(request);
        final ResponseEntity<RES> responseEntity = rt.exchange(requestEntity, type);
        return responseEntity;
    }

    public <RES> ResponseEntity<RES> GET(RestTemplate rt, URI uri, Map<String,String> headers, Class<RES> type) {
        BodyBuilder bodyBuilder = getBodyBuilder(uri);
        if (headers != null) {
            headers.entrySet().forEach(header -> addHeader(bodyBuilder, header.getKey(), header.getValue()));
        }
        RequestEntity<Void> requestEntity = bodyBuilder.build();
        final ResponseEntity<RES> responseEntity = rt.exchange(requestEntity, type);
        return responseEntity;
    }

    private BodyBuilder deleteBodyBuilder(URI uri){
        BodyBuilder bodyBuilder = RequestEntity.method(HttpMethod.DELETE, uri);
        return bodyBuilder;
    }

    private BodyBuilder getBodyBuilder(URI uri){
        BodyBuilder bodyBuilder = RequestEntity.method(HttpMethod.GET, uri);
        bodyBuilder.accept(MediaType.APPLICATION_JSON).build();
        return bodyBuilder;
    }

    private BodyBuilder postBodyBuilder(URI uri){
        BodyBuilder bodyBuilder = RequestEntity.method(HttpMethod.POST, uri).contentType(MediaType.APPLICATION_JSON);
        return bodyBuilder;
    }

    private BodyBuilder putBodyBuilder(URI uri){
        BodyBuilder bodyBuilder = RequestEntity.method(HttpMethod.PUT, uri).contentType(MediaType.APPLICATION_JSON);
        return bodyBuilder;
    }

    private void addHeader(BodyBuilder bodyBuilder, String key, String value){
        if (key == null || value == null){
            log.warn("Null or empty key={}, value={}", key, value);
            return ;
        }
        log.debug("Adding header[key={}, value={}]", key, value);
        bodyBuilder.header(key, value);
    }



}
