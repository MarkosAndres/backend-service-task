package com.marcos.incode_home_task.service

import com.marcos.incode_home_task.dto.*
import spock.lang.Specification

import java.time.LocalDate

class ThirdPartyServiceSpec extends Specification
{
    def freeClient = Mock(FreeThirdPartyClient)
    def premiumClient = Mock(PremiumThirdPartyClient)
    def service = new ThirdPartyService(freeClient, premiumClient)

    def 'uses premium when free returns no results'()
    {
        given:
        def premiumResult = new ThirdPartySearchResult([new Company('2', 'Acme', LocalDate.now(), 'A', true)], VerificationSource.PREMIUM)
        freeClient.findResults('acme') >> new ThirdPartySearchResult([], VerificationSource.FREE)
        premiumClient.findResults('acme') >> premiumResult

        when:
        def result = service.findCompanies('acme')

        then:
        result == premiumResult
    }

    def 'keeps nonempty free result without a premium call'()
    {
        given:
        def freeResult = new ThirdPartySearchResult([new Company('1', 'Acme', LocalDate.now(), 'A', true)], VerificationSource.FREE)
        freeClient.findResults('acme') >> freeResult

        when:
        def result = service.findCompanies('acme')

        then:
        result == freeResult
    }
}
