package net.quedex.api.pgp;

public class PGPDecryptionException extends PGPExceptionBase {

    public PGPDecryptionException() {
    }

    public PGPDecryptionException(String message) {
        super(message);
    }

    public PGPDecryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PGPDecryptionException(Throwable cause) {
        super(cause);
    }
}
