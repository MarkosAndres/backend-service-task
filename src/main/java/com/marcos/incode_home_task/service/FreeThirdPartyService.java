package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.dto.FreeCompanyResponse;
import com.marcos.incode_home_task.exception.FreeServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FreeThirdPartyService
{
    private static final Logger log = LoggerFactory.getLogger(FreeThirdPartyService.class);

    private final CompanyService companyService;
    private final ProviderFailureSimulator failureSimulator;

    public FreeThirdPartyService(CompanyService companyService, ProviderFailureSimulator failureSimulator)
    {
        this.companyService = companyService;
        this.failureSimulator = failureSimulator;
    }

    public List<FreeCompanyResponse> search(String query)
    {
        if (failureSimulator.isUnavailable(40))
        {
            log.warn("Free provider is unavailable");
            throw new FreeServiceUnavailableException();
        }

        List<FreeCompanyResponse> results = companyService
                .find("free_service_companies-1.json", query)
                .stream()
                .map(company ->
                        new FreeCompanyResponse(
                                company.cin(),
                                company.name(),
                                company.registrationDate(),
                                company.address(),
                                company.active()))
                .toList();
        log.info("Free provider search completed: resultCount={}", results.size());
        return results;
    }
}
