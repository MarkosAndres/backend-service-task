package com.marcos.incode_home_task.controller;

import com.marcos.incode_home_task.dto.PremiumCompanyResponse;
import com.marcos.incode_home_task.service.PremiumThirdPartyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PremiumThirdPartyController
{
    private static final Logger log = LoggerFactory.getLogger(PremiumThirdPartyController.class);

    private final PremiumThirdPartyService premiumThirdPartyService;

    public PremiumThirdPartyController(PremiumThirdPartyService premiumThirdPartyService)
    {
        this.premiumThirdPartyService = premiumThirdPartyService;
    }

    @GetMapping("/premium-third-party")
    public List<PremiumCompanyResponse> search(@RequestParam String query)
    {
        log.info("Received premium third-party search request: query={}", query);
        List<PremiumCompanyResponse> response = premiumThirdPartyService.search(query);
        log.info("Completed premium third-party search request: query={}, resultCount={}", query, response.size());
        return response;
    }
}
