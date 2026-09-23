package com.marcos.incode_home_task.controller;

import com.marcos.incode_home_task.dto.FreeCompanyResponse;
import com.marcos.incode_home_task.service.FreeThirdPartyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FreeThirdPartyController
{
    private final FreeThirdPartyService freeThirdPartyService;

    public FreeThirdPartyController(FreeThirdPartyService freeThirdPartyService)
    {
        this.freeThirdPartyService = freeThirdPartyService;
    }

    @GetMapping("/free-third-party")
    public List<FreeCompanyResponse> search(@RequestParam String query)
    {
        return freeThirdPartyService.search(query);
    }

}
