package com.recruit.githubrepositories.api;

import com.recruit.githubrepositories.api.exception.NotAcceptableException;
import com.recruit.githubrepositories.api.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.naming.ServiceUnavailableException;

/**
 * Performs exception handling for all REST API controllers. This class provides exception handlers that respond to
 * possible exceptions with appropriate HTTP status codes for the client.
 * 
 *
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandlingControllerAdvice {


    /**
     * Exception handler for <i>NotFoundException</i>, translating error into HTTP status code 404.
     * 
     * @param ex
     *            NotFoundException
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public VndErrors handleNotFoundException(NotFoundException ex) {
        log.warn(ex.getMessage());
        return new VndErrors("error", ex.getMessage());
    }


    /**
     * Exception handler for <i>NotAcceptableException</i>, translating error into HTTP status code 406.
     * 
     * @param ex
     *            NotAcceptableException
     */
    @ExceptionHandler(NotAcceptableException.class)
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    public void handleNotAcceptableException(NotAcceptableException ex) {
        log.warn(ex.getMessage());
    }


    /**
     * Exception handler for <i>ServiceUnavailableException</i>, translating error into HTTP status code 503.
     * 
     * @param ex
     *            ServiceUnavailableException
     */
    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    public void handleServiceUnavailableException(ServiceUnavailableException ex) {
        log.warn(ex.getMessage());
    }

    /**
     * General fallback exception handler, translating all not otherwise caught errors into HTTP status code 503.
     * 
     * @param ex
     *            Exception
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    public void handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
    }

}
