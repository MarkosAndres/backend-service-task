package com.marcos.incode_home_task.exception;

import com.marcos.incode_home_task.verification.VerificationSource;

public class ThirdPartyServiceException extends Exception
{
    private final VerificationSource source;

    public ThirdPartyServiceException(Throwable cause, VerificationSource source)
    {
        super(cause);
        this.source = source;
    }

    public VerificationSource source()
    {
        return source;
    }
}
