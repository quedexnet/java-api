package net.quedex.api.pgp;

public final class PGPKeyInitialisationException extends PGPExceptionBase
{
    public PGPKeyInitialisationException()
    {
    }

    public PGPKeyInitialisationException(final String message)
    {
        super(message);
    }

    public PGPKeyInitialisationException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public PGPKeyInitialisationException(final Throwable cause)
    {
        super(cause);
    }
}
