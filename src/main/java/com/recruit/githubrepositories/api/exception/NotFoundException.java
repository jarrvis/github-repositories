package com.recruit.githubrepositories.api.exception;

/**
 * API exception, thrown when an entity is not found where it is expected.
 * 
 */
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 6297471955828454690L;

    /**
     * Constructor accepting an error message.
     * 
     * @param message
     *            message
     */
    public NotFoundException(String message) {
        super(message);
    }

}
