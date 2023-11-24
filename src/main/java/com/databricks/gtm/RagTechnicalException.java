package com.databricks.gtm;

public class RagTechnicalException extends Throwable {

    public RagTechnicalException() {
        super();
    }

    public RagTechnicalException(String message) {
        super(message);
    }

    public RagTechnicalException(String message, Throwable cause) {
        super(message, cause);
    }

    public RagTechnicalException(Throwable cause) {
        super(cause);
    }

    protected RagTechnicalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
