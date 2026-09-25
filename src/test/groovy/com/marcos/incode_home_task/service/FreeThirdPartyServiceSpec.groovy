package com.marcos.incode_home_task.service

import com.marcos.incode_home_task.dto.Company
import com.marcos.incode_home_task.exception.FreeServiceUnavailableException
import spock.lang.Specification

import java.time.LocalDate

class FreeThirdPartyServiceSpec extends Specification
{
    def companyService = Mock(CompanyService)
    def failureSimulator = Mock(ProviderFailureSimulator)
    def freeThirdPartyService = new FreeThirdPartyService(companyService, failureSimulator)

    def 'maps catalog company to free response when provider is available'()
    {
        given:
        companyService.find('free_service_companies-1.json', 'acme') >>
                [new Company('1', 'Acme', LocalDate.of(2020, 1, 1), 'A', true)]

        when:
        def response = freeThirdPartyService.search('acme')

        then:
        response*.cin() == ['1']
        response*.is_active() == [true]
        1 * failureSimulator.isUnavailable(40) >> false
    }

    def 'returns unavailable when the free provider failure is simulated'()
    {
        given:
        failureSimulator.isUnavailable(40) >> true

        when:
        freeThirdPartyService.search('acme')

        then:
        thrown(FreeServiceUnavailableException)
        0 * companyService._
    }
}
