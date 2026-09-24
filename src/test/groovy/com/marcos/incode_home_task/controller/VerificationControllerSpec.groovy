package com.marcos.incode_home_task.controller

import com.marcos.incode_home_task.dto.*
import com.marcos.incode_home_task.service.VerificationService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import java.time.Instant
import java.util.UUID

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(controllers = VerificationController)
@AutoConfigureMockMvc(addFilters = false)
class VerificationControllerSpec extends Specification
{
    @Autowired MockMvc mvc
    @SpringBean VerificationService verificationService = Mock()

    def 'returns a verification when it exists'()
    {
        given:
        def id = UUID.randomUUID()
        verificationService.findByVerificationId(id) >>
                Optional.of(new VerificationResponse(id, 'acme', Instant.parse('2025-01-01T00:00:00Z'), new BackendResponse(id, 'acme', SearchResult.noResults()), VerificationSource.FREE))

        expect:
        mvc.perform(get("/verifications/$id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.verificationId').value(id.toString()))
    }

    def 'returns 404 for an absent verification'()
    {
        given:
        def id = UUID.randomUUID()
        verificationService.findByVerificationId(id) >> Optional.empty()

        expect:
        mvc.perform(get("/verifications/$id")).andExpect(status().isNotFound())
    }

    def 'returns all verifications'()
    {
        given:
        verificationService.findAll() >> []

        expect:
        mvc.perform(get('/verifications')).andExpect(status().isOk()).andExpect(content().json('[]'))
    }
}
