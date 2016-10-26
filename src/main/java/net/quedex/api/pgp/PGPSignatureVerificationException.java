package net.quedex.api.pgp;

public class PGPSignatureVerificationException extends PGPExceptionBase
{
    public PGPSignatureVerificationException()
    {
    }

    public PGPSignatureVerificationException(final String message)
    {
        super(message);
    }

    public PGPSignatureVerificationException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
