package com.marcos.incode_home_task.controller;

import com.marcos.incode_home_task.exception.PremiumServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler
{
    @ExceptionHandler(PremiumServiceUnavailableException.class)
    ProblemDetail handleServiceUnavailable(PremiumServiceUnavailableException exception)
    {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }
}
