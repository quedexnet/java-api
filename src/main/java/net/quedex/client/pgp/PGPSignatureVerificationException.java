package net.quedex.client.pgp;

public class PGPSignatureVerificationException extends PGPExceptionBase {

    public PGPSignatureVerificationException() {
    }

    public PGPSignatureVerificationException(String message) {
        super(message);
    }

    public PGPSignatureVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
