package com.marcos.incode_home_task.controller;

import com.marcos.incode_home_task.company.ThirdPartyUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler
{

    @ExceptionHandler(ThirdPartyUnavailableException.class)
    ProblemDetail handleThirdPartyUnavailable(ThirdPartyUnavailableException exception)
    {
        return problem(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    ProblemDetail handleInvalidRequest(Exception exception)
    {
        return problem(HttpStatus.BAD_REQUEST, "A required request parameter is missing or invalid.");
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpectedException(Exception exception)
    {
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }

    private ProblemDetail problem(HttpStatus status, String detail)
    {
        return ProblemDetail.forStatusAndDetail(status, detail);
    }
}
