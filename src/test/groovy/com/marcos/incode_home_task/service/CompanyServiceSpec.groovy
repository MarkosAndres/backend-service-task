package com.marcos.incode_home_task.service

import spock.lang.Specification

class CompanyServiceSpec extends Specification
{
    def service = new CompanyService()

    def 'finds catalog companies case-insensitively by CIN'()
    {
        when:
        def companies = service.find('free_service_companies-1.json', 'cjq')

        then:
        companies
        companies.every { it.cin().toLowerCase().contains('cjq') }
    }

    def 'returns no entries for a nonexistent catalog'()
    {
        expect:
        service.find('does-not-exist.json', 'anything').empty
    }
}
