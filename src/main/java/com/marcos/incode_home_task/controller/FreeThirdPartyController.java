package com.marcos.incode_home_task.controller;

import com.marcos.incode_home_task.company.CompanyService;
import com.marcos.incode_home_task.dto.FreeCompanyResponse;
import com.marcos.incode_home_task.exception.FreeServiceUnavailableException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class FreeThirdPartyController
{
    private final CompanyService companyService;

    public FreeThirdPartyController(CompanyService companyService)
    {
        this.companyService = companyService;
    }

    @GetMapping("/free-third-party")
    public List<FreeCompanyResponse> search(@RequestParam String query)
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
