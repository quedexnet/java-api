package net.quedex.api.pgp;

public class PGPEncryptionException extends PGPExceptionBase
{
    public PGPEncryptionException()
    {
    }

    public PGPEncryptionException(final String message)
    {
        super(message);
    }

    public PGPEncryptionException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public PGPEncryptionException(final Throwable cause)
    {
        super(cause);
    }
}
