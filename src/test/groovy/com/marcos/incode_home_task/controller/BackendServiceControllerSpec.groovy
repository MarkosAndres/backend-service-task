package com.marcos.incode_home_task.controller

import com.marcos.incode_home_task.dto.*
import com.marcos.incode_home_task.service.BackendService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import java.util.UUID

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(controllers = BackendServiceController)
@AutoConfigureMockMvc(addFilters = false)
class BackendServiceControllerSpec extends Specification
{
    @Autowired MockMvc mvc
    @SpringBean BackendService backendService = Mock()

    def 'passes request parameters to backend service'()
    {
        given:
        def id = UUID.randomUUID()
        when:
        def result = mvc.perform(
                get('/backend-service')
                        .param('verificationId', id.toString())
                        .param('query', 'acme'))

        then:
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.result.status').value('NO_RESULTS'))
        1 * backendService.search(id, 'acme') >> new BackendResponse(id, 'acme', SearchResult.noResults())
    }

    def 'rejects an invalid verification id'()
    {
        expect:
        mvc.perform(
                get('/backend-service')
                        .param('verificationId', 'bad-id')
                        .param('query', 'acme'))
                .andExpect(status().isBadRequest())
    }
}
