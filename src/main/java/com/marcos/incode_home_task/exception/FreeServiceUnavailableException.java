package com.marcos.incode_home_task.exception;

import com.marcos.incode_home_task.company.ThirdPartyUnavailableException;

public class FreeServiceUnavailableException extends ThirdPartyUnavailableException
{
    public FreeServiceUnavailableException()
    {
        super("Free third-party service");
    }
}
