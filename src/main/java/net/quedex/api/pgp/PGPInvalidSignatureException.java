package net.quedex.api.pgp;

public final class PGPInvalidSignatureException extends PGPExceptionBase {

    public PGPInvalidSignatureException() {
    }

    public PGPInvalidSignatureException(String message) {
        super(message);
    }

    public PGPInvalidSignatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public PGPInvalidSignatureException(Throwable cause) {
        super(cause);
    }
}
