package com.marcos.incode_home_task.exception;

public class ThirdPartyServiceUnavailableException extends RuntimeException
{
    public ThirdPartyServiceUnavailableException(Throwable cause)
    {
        super("A third-party service is unavailable", cause);
    }
}
