package net.quedex.api.pgp;

public final class PGPKeyInitialisationException extends PGPExceptionBase {

    public PGPKeyInitialisationException() {
    }

    public PGPKeyInitialisationException(String message) {
        super(message);
    }

    public PGPKeyInitialisationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PGPKeyInitialisationException(Throwable cause) {
        super(cause);
    }
}
