package com.marcos.incode_home_task.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.MissingServletRequestParameterException
import spock.lang.Specification

class ApiExceptionHandlerSpec extends Specification
{
    def handler = new ApiExceptionHandler()

    def 'maps malformed requests to 400'()
    {
        expect:
        handler.handleInvalidRequest(new MissingServletRequestParameterException('query', 'String')).status == HttpStatus.BAD_REQUEST.value()
    }

    def 'does not expose unexpected exception details'()
    {
        expect:
        handler.handleUnexpectedException(new IllegalStateException('secret')).detail == 'An unexpected error occurred.'
    }
}
