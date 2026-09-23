package com.marcos.incode_home_task.exception;

public class BackendServiceUnavailableException extends RuntimeException
{
    public BackendServiceUnavailableException()
    {
        super("Both third-party services are unavailable");
    }
}
