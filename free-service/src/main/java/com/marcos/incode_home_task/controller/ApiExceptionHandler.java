package com.marcos.incode_home_task.controller;

import com.marcos.incode_home_task.exception.FreeServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler
{
    @ExceptionHandler(FreeServiceUnavailableException.class)
    ProblemDetail handleServiceUnavailable(FreeServiceUnavailableException exception)
    {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }
}
