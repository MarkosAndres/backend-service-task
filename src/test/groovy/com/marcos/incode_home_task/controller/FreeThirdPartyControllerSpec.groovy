package com.marcos.incode_home_task.controller

import com.marcos.incode_home_task.dto.FreeCompanyResponse
import com.marcos.incode_home_task.exception.FreeServiceUnavailableException
import com.marcos.incode_home_task.service.FreeThirdPartyService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(controllers = FreeThirdPartyController)
@AutoConfigureMockMvc(addFilters = false)
class FreeThirdPartyControllerSpec extends Specification
{
    @Autowired MockMvc mvc
    @SpringBean FreeThirdPartyService freeThirdPartyService = Mock()

    def 'returns the free provider results'()
    {
        given:
        freeThirdPartyService.search('acme') >>
                [new FreeCompanyResponse('CIN-1', 'Acme', LocalDate.of(2020, 1, 1), 'Main St', true)]

        expect:
        mvc.perform(get('/free-third-party').param('query', 'acme'))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$[0].cin').value('CIN-1'))
                .andExpect(jsonPath('$[0].name').value('Acme'))
    }

    def 'rejects a missing query'()
    {
        expect:
        mvc.perform(get('/free-third-party')).andExpect(status().isBadRequest())
    }

    def 'returns 503 when the free provider is unavailable'()
    {
        given:
        freeThirdPartyService.search('acme') >>
                { throw new FreeServiceUnavailableException() }

        expect:
        mvc.perform(
                get('/free-third-party')
                        .param('query', 'acme'))
                .andExpect(status().isServiceUnavailable())
    }
}
