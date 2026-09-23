package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.company.Company;
import com.marcos.incode_home_task.dto.BackendResponse;
import com.marcos.incode_home_task.dto.CompanyResponse;
import com.marcos.incode_home_task.dto.FreeCompanyResponse;
import com.marcos.incode_home_task.dto.PremiumCompanyResponse;
import com.marcos.incode_home_task.dto.SearchResult;
import com.marcos.incode_home_task.exception.BackendServiceUnavailableException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BackendService
{
    private final FreeThirdPartyService freeThirdPartyService;
    private final PremiumThirdPartyService premiumThirdPartyService;

    public BackendService(FreeThirdPartyService freeThirdPartyService,
                          PremiumThirdPartyService premiumThirdPartyService)
    {
        this.freeThirdPartyService = freeThirdPartyService;
        this.premiumThirdPartyService = premiumThirdPartyService;
    }

    public BackendResponse search(UUID verificationId, String query)
    {
        List<Company> matches;
        try
        {
            matches = fromFree(freeThirdPartyService.search(query));
        }
        catch (RuntimeException exception)
        {
            matches = findPremiumResults(query);
        }

        if (matches.isEmpty())
        {
            matches = findPremiumResults(query);
        }

        List<Company> activeCompaniesFound = matches
                .stream()
                .filter(Company::active)
                .toList();

        if (activeCompaniesFound.isEmpty())
        {
            return new BackendResponse(
                    verificationId,
                    query,
                    SearchResult.noResults());
        }

        Company firstCompanyResult = activeCompaniesFound.getFirst();
        List<CompanyResponse> otherResults = activeCompaniesFound
                .subList(1, activeCompaniesFound.size())
                .stream()
                .map(CompanyResponse::from)
                .toList();

        return new BackendResponse(
                verificationId,
                query,
                SearchResult.found(CompanyResponse.from(firstCompanyResult), otherResults));
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
            return fromPremium(premiumThirdPartyService.search(query));
        }
        catch (RuntimeException exception)
        {
            throw new BackendServiceUnavailableException();
        }
    }
}
