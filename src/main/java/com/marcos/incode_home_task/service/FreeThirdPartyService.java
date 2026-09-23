package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.company.CompanyService;
import com.marcos.incode_home_task.dto.FreeCompanyResponse;
import com.marcos.incode_home_task.exception.FreeServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FreeThirdPartyService
{
    private static final Logger log = LoggerFactory.getLogger(FreeThirdPartyService.class);

    private final CompanyService companyService;

    public FreeThirdPartyService(CompanyService companyService)
    {
        this.companyService = companyService;
    }

    public List<FreeCompanyResponse> search(String query)
    {
//        log.info("Searching free provider data: query={}", query);
        if (ThreadLocalRandom.current().nextInt(10) < 4)
        {
            log.warn("Free provider is unavailable: query={}", query);
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
        log.info("Free provider search completed: query={}, resultCount={}", query, results.size());
        return results;
    }
}
