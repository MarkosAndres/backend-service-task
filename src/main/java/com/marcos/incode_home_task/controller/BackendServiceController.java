package com.marcos.incode_home_task.controller;

import com.marcos.incode_home_task.dto.BackendResponse;
import com.marcos.incode_home_task.service.BackendService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class BackendServiceController
{
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
        return backendService.search(verificationId, query);
    }
}
