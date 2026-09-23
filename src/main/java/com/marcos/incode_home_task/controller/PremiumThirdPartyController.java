package com.marcos.incode_home_task.controller;

import com.marcos.incode_home_task.company.CompanyService;
import com.marcos.incode_home_task.company.ThirdPartyUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class PremiumThirdPartyController
{
    private final CompanyService catalog;

    public PremiumThirdPartyController(CompanyService catalog)
    {
        this.catalog = catalog;
    }

    @GetMapping("/premium-third-party")
    public List<PremiumCompanyResponse> search(@RequestParam String query)
    {
        if (ThreadLocalRandom.current().nextInt(10) == 0)
        {
            throw new PremiumServiceUnavailableException();
        }
        return catalog.find("premium_service_companies-1.json", query).stream()
                .map(company -> new PremiumCompanyResponse(company.cin(), company.name(), company.registrationDate(), company.address(), company.active()))
                .toList();
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public static class PremiumServiceUnavailableException extends ThirdPartyUnavailableException
    {
        public PremiumServiceUnavailableException()
        {
            super("Premium third-party service");
        }
    }

    public record PremiumCompanyResponse(String companyIdentificationNumber, String companyName,
                                         LocalDate registrationDate, String companyFullAddress, boolean isActive)
    {
    }
}
