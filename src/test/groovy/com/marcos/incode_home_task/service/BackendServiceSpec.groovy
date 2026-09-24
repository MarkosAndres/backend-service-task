package com.marcos.incode_home_task.service

import com.marcos.incode_home_task.dto.*
import com.marcos.incode_home_task.exception.ThirdPartyServiceException
import com.marcos.incode_home_task.metrics.ApplicationMetrics
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDate

class BackendServiceSpec extends Specification
{
    def thirdPartyService = Mock(ThirdPartyService)
    def verificationService = Mock(VerificationService)
    def metrics = Mock(ApplicationMetrics)
    def backendService = new BackendService(thirdPartyService, verificationService, metrics)
    def id = UUID.randomUUID()

    def 'returns the first company separately and stores the verification'()
    {
        given:
        def companyA = new Company('1', 'First', LocalDate.of(2020, 1, 1), 'A', true)
        def companyB = new Company('2', 'Second', LocalDate.of(2021, 1, 1), 'B', true)
        thirdPartyService.findCompanies('acme') >>
                new ThirdPartySearchResult([companyA, companyB], VerificationSource.FREE)

        when:
        def backendResponse = backendService.search(id, 'acme')

        then:
        backendResponse.result().status() == 'FOUND'
        backendResponse.result().company().cin() == '1'
        backendResponse.result().otherResults()*.cin() == ['2']
        1 * verificationService.store(_, VerificationSource.FREE, _ as Instant)
        0 * metrics._
    }

    def 'stores no-results response with source'()
    {
        given:
        thirdPartyService.findCompanies('none')
                >> new ThirdPartySearchResult([], VerificationSource.PREMIUM)

        when:
        def backendResponse = backendService.search(id, 'none')

        then:
        backendResponse.result().status() == 'NO_RESULTS'
        1 * verificationService.store(_, VerificationSource.PREMIUM, _ as Instant)
    }

    def 'returns unavailable response and records failure when both providers fail'()
    {
        given:
        thirdPartyService.findCompanies('acme') >>
                { throw new ThirdPartyServiceException(new RuntimeException(), VerificationSource.PREMIUM) }

        when:
        def backendResponse = backendService.search(id, 'acme')

        then:
        backendResponse.result().status() == 'THIRD_PARTIES_UNAVAILABLE'
        1 * metrics.backendSearchFailed()
        1 * verificationService.store(_, VerificationSource.PREMIUM, _ as Instant)
    }
}
