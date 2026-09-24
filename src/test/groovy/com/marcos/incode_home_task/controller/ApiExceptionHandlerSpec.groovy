package com.marcos.incode_home_task.controller

import com.marcos.incode_home_task.exception.FreeServiceUnavailableException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MissingServletRequestParameterException
import spock.lang.Specification

class ApiExceptionHandlerSpec extends Specification
{
    def handler = new ApiExceptionHandler()

    def 'maps provider unavailability to 503'()
    {
        when:
        def problem = handler.handleServiceUnavailable(new FreeServiceUnavailableException())

        then:
        problem.status == HttpStatus.SERVICE_UNAVAILABLE.value()
        problem.detail == 'Free third-party service'
    }

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
