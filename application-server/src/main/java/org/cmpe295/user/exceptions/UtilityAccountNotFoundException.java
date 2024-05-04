package org.cmpe295.user.exceptions;

public class UtilityAccountNotFoundException extends RuntimeException {

    public UtilityAccountNotFoundException(String message) {
        super(message);
    }

    public UtilityAccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
