package com.marcos.incode_home_task.controller;

import com.marcos.incode_home_task.dto.BackendResponse;
import com.marcos.incode_home_task.service.BackendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class BackendServiceController
{
    private static final Logger log = LoggerFactory.getLogger(BackendServiceController.class);

    private final BackendService backendService;

    public BackendServiceController(BackendService backendService)
    {
        this.backendService = backendService;
    }

    @GetMapping("/backend-service")
    public BackendResponse search(
            @RequestParam UUID verificationId,
            @RequestParam String query)
    {
        log.info("Received backend search request: verificationId={}, query={}", verificationId, query);
        BackendResponse response = backendService.search(verificationId, query);
        log.info("Completed backend search request: verificationId={}", verificationId);
        return response;
    }
}
