package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.verification.ThirdPartySearchResult;
import com.marcos.incode_home_task.exception.ThirdPartyServiceException;
import com.marcos.incode_home_task.verification.VerificationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ThirdPartyService
{
    private static final Logger log = LoggerFactory.getLogger(ThirdPartyService.class);

    private final FreeThirdPartyClient freeThirdPartyClient;
    private final PremiumThirdPartyClient premiumThirdPartyClient;

    public ThirdPartyService(
            FreeThirdPartyClient freeThirdPartyClient,
            PremiumThirdPartyClient premiumThirdPartyClient)
    {
        this.freeThirdPartyClient = freeThirdPartyClient;
        this.premiumThirdPartyClient = premiumThirdPartyClient;
    }

    public ThirdPartySearchResult findCompanies(String query)
            throws ThirdPartyServiceException
    {
        ThirdPartySearchResult searchResult = freeThirdPartyClient.findResults(query);

        if(VerificationSource.FREE == searchResult.source()
                && searchResult.companies().isEmpty())
        {
            log.info("Free provider returned no results; searching premium provider");
            searchResult = premiumThirdPartyClient.findResults(query);
        }

        log.info("Third-party search completed: source={}, activeResultCount={}",
                searchResult.source(), searchResult.companies().size());

        return searchResult;
    }
}
