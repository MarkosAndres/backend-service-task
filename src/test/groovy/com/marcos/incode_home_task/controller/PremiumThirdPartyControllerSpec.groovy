package com.marcos.incode_home_task.controller

import com.marcos.incode_home_task.dto.PremiumCompanyResponse
import com.marcos.incode_home_task.exception.PremiumServiceUnavailableException
import com.marcos.incode_home_task.service.PremiumThirdPartyService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(controllers = PremiumThirdPartyController)
@AutoConfigureMockMvc(addFilters = false)
class PremiumThirdPartyControllerSpec extends Specification
{
    @Autowired MockMvc mvc
    @SpringBean PremiumThirdPartyService premiumThirdPartyService = Mock()

    def 'returns premium provider results'()
    {
        given:
        premiumThirdPartyService.search('acme') >>
                [new PremiumCompanyResponse('CIN-1', 'Acme', LocalDate.of(2020, 1, 1), 'Main St', true)]

        expect:
        mvc.perform(get('/premium-third-party').param('query', 'acme'))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$[0].companyName').value('Acme'))
    }

    def 'returns 503 when the premium provider is unavailable'()
    {
        given:
        premiumThirdPartyService.search('acme') >>
                { throw new PremiumServiceUnavailableException() }

        expect:
        mvc.perform(
                get('/premium-third-party')
                        .param('query', 'acme'))
                .andExpect(status().isServiceUnavailable())
    }
}
