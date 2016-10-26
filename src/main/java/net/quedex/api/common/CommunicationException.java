package net.quedex.api.common;

import java.io.IOException;

public class CommunicationException extends IOException
{
    public CommunicationException()
    {
    }

    public CommunicationException(final String message)
    {
        super(message);
    }

    public CommunicationException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public CommunicationException(final Throwable cause)
    {
        super(cause);
    }
}
