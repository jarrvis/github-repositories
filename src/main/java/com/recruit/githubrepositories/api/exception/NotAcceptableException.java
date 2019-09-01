package com.recruit.githubrepositories.api.exception;

import com.recruit.githubrepositories.api.dto.GithubRepositoryDetails;

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * API exception, thrown when given data is not acceptable.
 *
 */
public class NotAcceptableException extends RuntimeException {

    private static final long serialVersionUID = 5991191955244366152L;

    /**
     * Constructor accepting an error message.
     *
     * @param message
     *            message
     * @param violations
     */
    public NotAcceptableException(String message, Set<ConstraintViolation<GithubRepositoryDetails>> violations) {
        super(message);
    }

}
