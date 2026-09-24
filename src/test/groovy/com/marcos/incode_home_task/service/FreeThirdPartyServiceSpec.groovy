package com.marcos.incode_home_task.service

import com.marcos.incode_home_task.dto.Company
import spock.lang.Specification

import java.time.LocalDate

class FreeThirdPartyServiceSpec extends Specification
{
    def companyService = Mock(CompanyService)
    def freeThirdPartyService = new FreeThirdPartyService(companyService)

    def 'maps catalog company to free response when provider is available'()
    {
        given:
        companyService.find('free_service_companies-1.json', 'acme') >>
                [new Company('1', 'Acme', LocalDate.of(2020, 1, 1), 'A', true)]

        when:
        def response = eventually { freeThirdPartyService.search('acme') }

        then:
        response*.cin() == ['1']
        response*.is_active() == [true]
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
        throw new AssertionError('Free provider was unavailable in every attempt')
    }
}
