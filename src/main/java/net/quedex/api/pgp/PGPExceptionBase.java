package net.quedex.api.pgp;

public class PGPExceptionBase extends Exception
{
    PGPExceptionBase()
    {
    }

    PGPExceptionBase(final String message)
    {
        super(message);
    }

    PGPExceptionBase(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    PGPExceptionBase(final Throwable cause)
    {
        super(cause);
    }
}
