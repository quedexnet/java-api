package net.quedex.api.pgp;

public final class PGPInvalidSignatureException extends PGPExceptionBase
{
    public PGPInvalidSignatureException()
    {
    }

    public PGPInvalidSignatureException(final String message)
    {
        super(message);
    }

    public PGPInvalidSignatureException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public PGPInvalidSignatureException(final Throwable cause)
    {
        super(cause);
    }
}
