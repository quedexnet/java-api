package net.quedex.api.pgp;

public final class PGPKeyNotFoundException extends PGPExceptionBase {

    public PGPKeyNotFoundException() {
    }

    public PGPKeyNotFoundException(String message) {
        super(message);
    }

    public PGPKeyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PGPKeyNotFoundException(Throwable cause) {
        super(cause);
    }
}
