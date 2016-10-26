package net.quedex.api.pgp;

public final class PGPUnknownRecipientException extends PGPExceptionBase
{
    public PGPUnknownRecipientException()
    {
    }

    public PGPUnknownRecipientException(final String message)
    {
        super(message);
    }

    public PGPUnknownRecipientException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public PGPUnknownRecipientException(final Throwable cause)
    {
        super(cause);
    }
}
