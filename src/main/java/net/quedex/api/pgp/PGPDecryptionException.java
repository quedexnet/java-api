package net.quedex.api.pgp;

public class PGPDecryptionException extends PGPExceptionBase
{
    public PGPDecryptionException()
    {
    }

    public PGPDecryptionException(final String message)
    {
        super(message);
    }

    public PGPDecryptionException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public PGPDecryptionException(final Throwable cause)
    {
        super(cause);
    }
}
