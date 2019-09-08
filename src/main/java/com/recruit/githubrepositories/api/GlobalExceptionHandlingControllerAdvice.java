package com.recruit.githubrepositories.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.naming.ServiceUnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.run;

/**
 * Performs exception handling for all REST API controllers. This class provides exception handlers that respond to
 * possible exceptions with appropriate HTTP status codes for the client.
 * 
 *
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandlingControllerAdvice {



    @ExceptionHandler(WebClientResponseException.class)
    @ResponseBody
    public VndErrors handleNestedServletException(HttpServletRequest req, HttpServletResponse resp,
                                               WebClientResponseException ex) {

         Match(ex).of(
                Case($(e -> HttpStatus.NOT_FOUND.compareTo(ex.getStatusCode()) == 0),
                        o -> run(() -> resp.setStatus(HttpStatus.NOT_FOUND.value()))),
                Case($(e -> HttpStatus.FORBIDDEN.compareTo(ex.getStatusCode()) == 0),
                        o -> run(() -> resp.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()))),
                Case($(), o -> run(() -> resp.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value())))
        );

        log.warn(ex.getMessage());
        return new VndErrors("error", ex.getMessage());
    }


    /**
     * General fallback exception handler, translating all not otherwise caught errors into HTTP status code 503.
     *
     * @param ex
     *            Exception
     */
    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    public VndErrors handleServiceUnavailableException(ServiceUnavailableException ex) {
        log.error(ex.getMessage(), ex);
        return new VndErrors("error", ex.getMessage());
    }


    /**
     * General fallback exception handler, translating all not otherwise caught errors into HTTP status code 503.
     * 
     * @param ex
     *            Exception
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    public VndErrors handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new VndErrors("error", ex.getMessage());

    }

}
