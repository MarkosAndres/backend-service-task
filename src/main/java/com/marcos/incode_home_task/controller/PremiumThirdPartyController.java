package com.marcos.incode_home_task.controller;

import com.marcos.incode_home_task.dto.PremiumCompanyResponse;
import com.marcos.incode_home_task.service.PremiumThirdPartyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PremiumThirdPartyController
{
    private final PremiumThirdPartyService premiumThirdPartyService;

    public PremiumThirdPartyController(PremiumThirdPartyService premiumThirdPartyService)
    {
        this.premiumThirdPartyService = premiumThirdPartyService;
    }

    @GetMapping("/premium-third-party")
    public List<PremiumCompanyResponse> search(@RequestParam String query)
    {
        return premiumThirdPartyService.search(query);
    }
}
