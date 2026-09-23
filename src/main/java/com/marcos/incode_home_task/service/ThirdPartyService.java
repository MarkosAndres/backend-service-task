package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.company.Company;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.SupplierUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class ThirdPartyService
{
    private final FreeThirdPartyClient freeThirdPartyClient;
    private final PremiumThirdPartyClient premiumThirdPartyClient;
    private final CircuitBreaker freeCircuitBreaker;
    private final CircuitBreaker premiumCircuitBreaker;

    public ThirdPartyService(
            FreeThirdPartyClient freeThirdPartyClient,
            PremiumThirdPartyClient premiumThirdPartyClient,
            CircuitBreakerRegistry circuitBreakerRegistry)
    {
        this.freeThirdPartyClient = freeThirdPartyClient;
        this.premiumThirdPartyClient = premiumThirdPartyClient;
        freeCircuitBreaker = circuitBreakerRegistry.circuitBreaker("freeThirdParty");
        premiumCircuitBreaker = circuitBreakerRegistry.circuitBreaker("premiumThirdParty");
    }

    public List<Company> findCompanies(String query)
    {
        Supplier<List<Company>> freeSupplier = CircuitBreaker.decorateCheckedSupplier(
                freeCircuitBreaker,
                () -> freeThirdPartyClient.findResults(query))
        .unchecked();

        List<Company> freeResults = SupplierUtils.recover(
                        freeSupplier,
                        exception -> this.findCompaniesPremiumService(query))
                .get();

        return freeResults.stream()
                .filter(Company::active)
                .toList();
    }

    private List<Company> findCompaniesPremiumService(String query)
    {
        Supplier<List<Company>> premiumSupplier = CircuitBreaker.decorateCheckedSupplier(
                premiumCircuitBreaker,
                () -> premiumThirdPartyClient.findResults(query))
        .unchecked();

        return SupplierUtils.recover(premiumSupplier, ex -> List.of()).get();
    }
}
