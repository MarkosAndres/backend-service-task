package com.marcos.incode_home_task.controller;

import com.marcos.incode_home_task.company.Company;
import com.marcos.incode_home_task.dto.BackendResponse;
import com.marcos.incode_home_task.dto.CompanyResponse;
import com.marcos.incode_home_task.dto.FreeCompanyResponse;
import com.marcos.incode_home_task.dto.PremiumCompanyResponse;
import com.marcos.incode_home_task.dto.SearchResult;
import com.marcos.incode_home_task.exception.BackendServiceUnavailableException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class BackendServiceController
{
    private final FreeThirdPartyController freeThirdParty;
    private final PremiumThirdPartyController premiumThirdParty;

    public BackendServiceController(FreeThirdPartyController freeThirdParty,
                                    PremiumThirdPartyController premiumThirdParty)
    {
        this.freeThirdParty = freeThirdParty;
        this.premiumThirdParty = premiumThirdParty;
    }

    @GetMapping("/backend-service")
    public BackendResponse search(@RequestParam UUID verificationId, @RequestParam String query)
    {
        List<Company> matches;
        try
        {
            matches = fromFree(freeThirdParty.search(query));
        }
        catch (RuntimeException exception)
        {
            matches = findPremiumResults(query);
        }

        if (matches.isEmpty())
        {
            matches = findPremiumResults(query);
        }

        List<Company> activeMatches = matches.stream().filter(Company::active).toList();
        if (activeMatches.isEmpty())
        {
            return new BackendResponse(verificationId, query, SearchResult.noResults());
        }

        Company first = activeMatches.getFirst();
        List<CompanyResponse> otherResults = activeMatches.subList(1, activeMatches.size()).stream()
                .map(CompanyResponse::from).toList();
        return new BackendResponse(verificationId, query, SearchResult.found(CompanyResponse.from(first), otherResults));
    }

    private List<Company> fromFree(List<FreeCompanyResponse> responses)
    {
        return responses
                .stream()
                .map(response ->
                        new Company(response.cin(),
                                response.name(),
                                response.registration_date(),
                                response.address(),
                                response.is_active()))
                .toList();
    }

    private List<Company> fromPremium(List<PremiumCompanyResponse> responses)
    {
        return responses
                .stream()
                .map(response ->
                        new Company(response.companyIdentificationNumber(),
                                response.companyName(),
                                response.registrationDate(),
                                response.companyFullAddress(),
                                response.isActive()))
                .toList();
    }

    private List<Company> findPremiumResults(String query)
    {
        try
        {
            return fromPremium(premiumThirdParty.search(query));
        }
        catch (RuntimeException exception)
        {
            throw new BackendServiceUnavailableException();
        }
    }
}
