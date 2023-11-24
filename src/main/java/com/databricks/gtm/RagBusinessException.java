package com.databricks.gtm;

public class RagBusinessException extends Exception {

    public RagBusinessException() {
        super();
    }

    public RagBusinessException(String message) {
        super(message);
    }

    public RagBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public RagBusinessException(Throwable cause) {
        super(cause);
    }

    protected RagBusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
