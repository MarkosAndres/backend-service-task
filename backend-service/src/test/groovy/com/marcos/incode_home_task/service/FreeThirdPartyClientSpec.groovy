package com.marcos.incode_home_task.service

import com.marcos.incode_home_task.dto.*
import com.marcos.incode_home_task.metrics.ApplicationMetrics
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestClient
import spock.lang.Specification

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

class FreeThirdPartyClientSpec extends Specification
{
    def metrics = Mock(ApplicationMetrics)
    def premiumClient = Mock(PremiumThirdPartyClient)
    def builder = RestClient.builder()
    def server = MockRestServiceServer.bindTo(builder).build()
    def client = new FreeThirdPartyClient(builder, 'http://provider.test', premiumClient, metrics)

    def 'maps active free-provider response and filters inactive companies'()
    {
        given:
        server.expect(requestTo('http://provider.test/free-third-party?query=acme'))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        '[{"cin":"1","name":"Active","registration_date":"2020-01-01","address":"A","is_active":true},{"cin":"2","name":"Inactive","registration_date":"2020-01-01","address":"B","is_active":false}]',
                        MediaType.APPLICATION_JSON))

        when:
        def result = client.findResults('acme')

        then:
        result.source() == VerificationSource.FREE
        result.companies()*.cin() == ['1']
        1 * metrics.thirdPartyRequestCalled(VerificationSource.FREE)
        0 * metrics.thirdPartyRequestFailed(_)
        server.verify()
    }

    def 'delegates circuit-breaker fallback to premium client'()
    {
        given:
        def premiumResult = new ThirdPartySearchResult([], VerificationSource.PREMIUM)
        premiumClient.findResults('acme') >> premiumResult

        when:
        def result = client.findResultsFromPremium('acme', new RuntimeException())

        then:
        result == premiumResult
        1 * metrics.thirdPartyFallbackCalled(VerificationSource.FREE)
    }
}
