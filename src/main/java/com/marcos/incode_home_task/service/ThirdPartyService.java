package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.verification.ThirdPartySearchResult;
import com.marcos.incode_home_task.verification.VerificationSource;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.SupplierUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class ThirdPartyService
{
    private static final Logger log = LoggerFactory.getLogger(ThirdPartyService.class);

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

    public ThirdPartySearchResult findCompanies(String query)
    {
        log.info("Searching through free third-party provider: query={}, circuitState={}",
                query, freeCircuitBreaker.getState());

        Supplier<ThirdPartySearchResult> freeSupplier = CircuitBreaker.decorateCheckedSupplier(
                freeCircuitBreaker,
                () -> new ThirdPartySearchResult(
                        freeThirdPartyClient.findResults(query),
                        VerificationSource.FREE))
        .unchecked();

        ThirdPartySearchResult searchResult = SupplierUtils.recover(
                        freeSupplier,
                        exception ->
                        {
                            log.warn("Free provider failed; falling back to premium provider: query={}", query, exception);
                            return this.findCompaniesPremiumService(query);
                        })
                .get();

        if(searchResult.companies().isEmpty())
        {
            searchResult = this.findCompaniesPremiumService(query);
        }

        log.info("Third-party search completed: query={}, source={}, activeResultCount={}",
                query, searchResult.source(), searchResult.companies().size());

        return searchResult;
    }

    private ThirdPartySearchResult findCompaniesPremiumService(String query)
    {
        log.info("Searching through premium third-party provider: query={}, circuitState={}",
                query, premiumCircuitBreaker.getState());
        Supplier<ThirdPartySearchResult> premiumSupplier = CircuitBreaker.decorateCheckedSupplier(
                premiumCircuitBreaker,
                () -> new ThirdPartySearchResult(
                        premiumThirdPartyClient.findResults(query),
                        VerificationSource.PREMIUM))
        .unchecked();

        return SupplierUtils.recover(premiumSupplier, exception ->
        {
            log.error("Premium provider failed; returning no results: query={}", query, exception);
            return new ThirdPartySearchResult(List.of(), VerificationSource.PREMIUM);
        }).get();
    }
}
