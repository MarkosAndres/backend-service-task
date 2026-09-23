package com.marcos.incode_home_task.controller;

import com.marcos.incode_home_task.company.CompanyService;
import com.marcos.incode_home_task.dto.PremiumCompanyResponse;
import com.marcos.incode_home_task.exception.PremiumServiceUnavailableException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class PremiumThirdPartyController
{
    private final CompanyService companyService;

    public PremiumThirdPartyController(CompanyService catalog)
    {
        this.companyService = catalog;
    }

    @GetMapping("/premium-third-party")
    public List<PremiumCompanyResponse> search(@RequestParam String query)
    {
        if (ThreadLocalRandom.current().nextInt(10) == 0)
        {
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
