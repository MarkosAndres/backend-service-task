package com.marcos.incode_home_task.service

import com.marcos.incode_home_task.dto.Company
import com.marcos.incode_home_task.exception.PremiumServiceUnavailableException
import spock.lang.Specification

import java.time.LocalDate

class PremiumThirdPartyServiceSpec extends Specification
{
    def companyService = Mock(CompanyService)
    def failureSimulator = Mock(ProviderFailureSimulator)
    def service = new PremiumThirdPartyService(companyService, failureSimulator)

    def 'maps catalog company to premium response when provider is available'()
    {
        given:
        companyService.find('premium_service_companies-1.json', 'acme') >>
                [new Company('1', 'Acme', LocalDate.of(2020, 1, 1), 'A', true)]

        when:
        def response = service.search('acme')

        then:
        response*.companyIdentificationNumber() == ['1']
        response*.isActive() == [true]
        1 * failureSimulator.isUnavailable(10) >> false
    }

    def 'returns unavailable when the premium provider failure is simulated'()
    {
        given:
        failureSimulator.isUnavailable(10) >> true

        when:
        service.search('acme')

        then:
        thrown(PremiumServiceUnavailableException)
        0 * companyService._
    }
}
