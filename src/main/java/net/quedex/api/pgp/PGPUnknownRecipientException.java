package net.quedex.api.pgp;

public final class PGPUnknownRecipientException extends PGPExceptionBase {

    public PGPUnknownRecipientException() {
    }

    public PGPUnknownRecipientException(String message) {
        super(message);
    }

    public PGPUnknownRecipientException(String message, Throwable cause) {
        super(message, cause);
    }

    public PGPUnknownRecipientException(Throwable cause) {
        super(cause);
    }
}
