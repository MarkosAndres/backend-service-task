package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.company.Company;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThirdPartyService
{
    private final FreeThirdPartyClient freeThirdPartyClient;
    private final PremiumThirdPartyClient premiumThirdPartyClient;

    public ThirdPartyService(
            FreeThirdPartyClient freeThirdPartyClient,
            PremiumThirdPartyClient premiumThirdPartyClient)
    {
        this.freeThirdPartyClient = freeThirdPartyClient;
        this.premiumThirdPartyClient = premiumThirdPartyClient;
    }

    public List<Company> findCompanies(String query)
    {
        List<Company> freeResults = freeThirdPartyClient.findResults(query);

        List<Company> companiesFound = freeResults.isEmpty()
                ? premiumThirdPartyClient.findResults(query)
                : freeResults;

        return companiesFound.stream()
                .filter(Company::active)
                .toList();
    }
}
