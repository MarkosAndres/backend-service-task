package com.marcos.incode_home_task.service

import com.marcos.incode_home_task.dto.Company
import spock.lang.Specification

import java.time.LocalDate

class PremiumThirdPartyServiceSpec extends Specification
{
    def companyService = Mock(CompanyService)
    def service = new PremiumThirdPartyService(companyService)

    def 'maps catalog company to premium response when provider is available'()
    {
        given:
        companyService.find('premium_service_companies-1.json', 'acme') >>
                [new Company('1', 'Acme', LocalDate.of(2020, 1, 1), 'A', true)]

        when:
        def response = eventually { service.search('acme') }

        then:
        response*.companyIdentificationNumber() == ['1']
        response*.isActive() == [true]
    }

    private static <T> T eventually(Closure<T> action)
    {
        for (int attempt = 0; attempt < 100; attempt++)
        {
            try
            {
                return action.call()
            }
            catch (RuntimeException ignored)
            {
            }
        }
        throw new AssertionError('Premium provider was unavailable in every attempt')
    }
}
