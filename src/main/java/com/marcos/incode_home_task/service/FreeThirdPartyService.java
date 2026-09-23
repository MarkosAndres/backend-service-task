package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.company.CompanyService;
import com.marcos.incode_home_task.dto.FreeCompanyResponse;
import com.marcos.incode_home_task.exception.FreeServiceUnavailableException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FreeThirdPartyService
{
    private final CompanyService companyService;

    public FreeThirdPartyService(CompanyService companyService)
    {
        this.companyService = companyService;
    }

    public List<FreeCompanyResponse> search(String query)
    {
        if (ThreadLocalRandom.current().nextInt(10) < 4)
        {
            throw new FreeServiceUnavailableException();
        }

        return companyService
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
    }
}
