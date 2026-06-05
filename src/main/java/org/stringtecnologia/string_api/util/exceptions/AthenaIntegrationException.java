package org.stringtecnologia.string_api.util.exceptions;

public class AthenaIntegrationException extends RuntimeException {

    public AthenaIntegrationException(String message) {
        super(message);
    }

    public AthenaIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
