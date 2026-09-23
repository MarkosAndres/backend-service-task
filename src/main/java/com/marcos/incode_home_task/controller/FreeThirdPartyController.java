package com.marcos.incode_home_task.controller;

import com.marcos.incode_home_task.company.CompanyCatalog;
import com.marcos.incode_home_task.company.ThirdPartyUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class FreeThirdPartyController
{
    private final CompanyCatalog catalog;

    public FreeThirdPartyController(CompanyCatalog catalog)
    {
        this.catalog = catalog;
    }

    @GetMapping("/free-third-party")
    public List<FreeCompanyResponse> search(@RequestParam String query)
    {
        if (ThreadLocalRandom.current().nextInt(10) < 4)
        {
            throw new FreeServiceUnavailableException();
        }
        return catalog.find("free_service_companies-1.json", query).stream()
                .map(company -> new FreeCompanyResponse(company.cin(), company.name(), company.registrationDate(), company.address(), company.active()))
                .toList();
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public static class FreeServiceUnavailableException extends ThirdPartyUnavailableException
    {
        public FreeServiceUnavailableException()
        {
            super("Free third-party service");
        }
    }

    public record FreeCompanyResponse(String cin, String name, java.time.LocalDate registration_date,
                                      String address, boolean is_active)
    {
    }
}
