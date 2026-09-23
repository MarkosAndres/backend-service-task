package com.marcos.incode_home_task.exception;

public class FreeServiceUnavailableException extends RuntimeException
{
    public FreeServiceUnavailableException()
    {
        super("Free third-party service");
    }
}
