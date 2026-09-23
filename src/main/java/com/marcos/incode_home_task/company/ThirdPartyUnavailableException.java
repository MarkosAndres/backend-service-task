package com.marcos.incode_home_task.company;

public class ThirdPartyUnavailableException extends RuntimeException
{
    public ThirdPartyUnavailableException(String serviceName)
    {
        super(serviceName + " is unavailable");
    }
}
