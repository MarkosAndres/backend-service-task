package com.marcos.incode_home_task.service

import com.marcos.incode_home_task.dto.VerificationSource
import com.marcos.incode_home_task.metrics.ApplicationMetrics
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestClient
import spock.lang.Specification

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

class PremiumThirdPartyClientSpec extends Specification
{
    def metrics = Mock(ApplicationMetrics)
    def builder = RestClient.builder()
    def server = MockRestServiceServer.bindTo(builder).build()
    def client = new PremiumThirdPartyClient(builder, 'http://provider.test', metrics)

    def 'maps active premium-provider response and filters inactive companies'()
    {
        given:
        server.expect(requestTo('http://provider.test/premium-third-party?query=acme'))
                .andRespond(withSuccess('[{"companyIdentificationNumber":"1","companyName":"Active","registrationDate":"2020-01-01","companyFullAddress":"A","isActive":true},{"companyIdentificationNumber":"2","companyName":"Inactive","registrationDate":"2020-01-01","companyFullAddress":"B","isActive":false}]',
                        MediaType.APPLICATION_JSON))

        when:
        def result = client.findResults('acme')

        then:
        result.source() == VerificationSource.PREMIUM
        result.companies()*.cin() == ['1']
        1 * metrics.thirdPartyRequestCalled(VerificationSource.PREMIUM)
        server.verify()
    }
}
