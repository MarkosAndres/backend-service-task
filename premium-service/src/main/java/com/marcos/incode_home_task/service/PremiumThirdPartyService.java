package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.dto.PremiumCompanyResponse;
import com.marcos.incode_home_task.exception.PremiumServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PremiumThirdPartyService
{
    private static final Logger log = LoggerFactory.getLogger(PremiumThirdPartyService.class);

    private final CompanyService companyService;
    private final ProviderFailureSimulator failureSimulator;

    public PremiumThirdPartyService(CompanyService companyService, ProviderFailureSimulator failureSimulator)
    {
        this.companyService = companyService;
        this.failureSimulator = failureSimulator;
    }

    public List<PremiumCompanyResponse> search(String query)
    {
        if (failureSimulator.isUnavailable(10))
        {
            log.warn("Premium provider is unavailable");
            throw new PremiumServiceUnavailableException();
        }

        return companyService
                .find("premium_service_companies-1.json", query)
                .stream()
                .map(company ->
                        new PremiumCompanyResponse(
                                company.cin(),
                                company.name(),
                                company.registrationDate(),
                                company.address(),
                                company.active()))
                .toList();
    }
}
