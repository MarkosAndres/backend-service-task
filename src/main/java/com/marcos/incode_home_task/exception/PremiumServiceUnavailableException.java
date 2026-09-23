package com.marcos.incode_home_task.exception;

public class PremiumServiceUnavailableException extends RuntimeException
{
    public PremiumServiceUnavailableException()
    {
        super("Premium third-party service");
    }
}
