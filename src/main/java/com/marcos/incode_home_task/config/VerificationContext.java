package com.marcos.incode_home_task.config;

public final class VerificationContext
{
    public static final String MDC_KEY = "verificationId";
    public static final String HEADER_NAME = "X-Verification-Id";
    public static final String QUERY_MDC_KEY = "query";
    public static final String QUERY_HEADER_NAME = "X-Query";

    private VerificationContext()
    {
    }
}
