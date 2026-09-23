package com.marcos.incode_home_task.exception;

public class NoRecordsFoundException extends Exception
{
    public NoRecordsFoundException()
    {
        super("The third-party service returned no records");
    }
}
