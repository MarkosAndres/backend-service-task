package com.marcos.incode_home_task.controller;

import com.marcos.incode_home_task.dto.FreeCompanyResponse;
import com.marcos.incode_home_task.service.FreeThirdPartyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FreeThirdPartyController
{
    private static final Logger log = LoggerFactory.getLogger(FreeThirdPartyController.class);

    private final FreeThirdPartyService freeThirdPartyService;

    public FreeThirdPartyController(FreeThirdPartyService freeThirdPartyService)
    {
        this.freeThirdPartyService = freeThirdPartyService;
    }

    @GetMapping("/free-third-party")
    public List<FreeCompanyResponse> search(@RequestParam String query)
    {
        log.info("Received free third-party search request");
        List<FreeCompanyResponse> response = freeThirdPartyService.search(query);
        log.info("Completed free third-party search request: resultCount={}", response.size());
        return response;
    }

}
