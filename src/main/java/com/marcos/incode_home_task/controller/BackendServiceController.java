package com.marcos.incode_home_task.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.marcos.incode_home_task.company.Company;
import com.marcos.incode_home_task.dto.FreeCompanyResponse;
import com.marcos.incode_home_task.exception.FreeServiceUnavailableException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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
            if (matches.isEmpty())
            {
                matches = fromPremium(premiumThirdParty.search(query));
            }
        }
        catch (FreeServiceUnavailableException exception)
        {
            try
            {
                matches = fromPremium(premiumThirdParty.search(query));
            }
            catch (PremiumThirdPartyController.PremiumServiceUnavailableException premiumException)
            {
                return new BackendResponse(verificationId, query, SearchResult.thirdPartiesDown());
            }
        }
        catch (PremiumThirdPartyController.PremiumServiceUnavailableException exception)
        {
            return new BackendResponse(verificationId, query, SearchResult.thirdPartiesDown());
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

    private List<Company> fromPremium(List<PremiumThirdPartyController.PremiumCompanyResponse> responses)
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

    public record BackendResponse(UUID verificationId, String query, SearchResult result)
    {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SearchResult(String status, CompanyResponse company, List<CompanyResponse> otherResults)
    {
        static SearchResult found(CompanyResponse company, List<CompanyResponse> otherResults)
        {
            return new SearchResult(
                    "FOUND",
                    company,
                    otherResults.isEmpty()
                            ? null
                            : otherResults);
        }

        static SearchResult noResults()
        {
            return new SearchResult("NO_RESULTS", null, null);
        }

        static SearchResult thirdPartiesDown()
        {
            return new SearchResult("THIRD_PARTIES_DOWN", null, null);
        }
    }

    public record CompanyResponse(String cin, String name, LocalDate registrationDate, String address, boolean isActive)
    {
        static CompanyResponse from(Company company)
        {
            return new CompanyResponse(
                    company.cin(),
                    company.name(),
                    company.registrationDate(),
                    company.address(),
                    company.active());
        }
    }
}
