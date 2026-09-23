package com.marcos.incode_home_task.controller;

import com.marcos.incode_home_task.dto.VerificationResponse;
import com.marcos.incode_home_task.service.VerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.UUID;
import java.util.List;

@RestController
public class VerificationController
{
    private static final Logger log = LoggerFactory.getLogger(VerificationController.class);

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService)
    {
        this.verificationService = verificationService;
    }

    @GetMapping("/verifications/{verificationId}")
    public ResponseEntity<VerificationResponse> findByVerificationId(@PathVariable UUID verificationId)
    {
        log.info("Received verification retrieval request: verificationId={}", verificationId);
        return verificationService.findByVerificationId(verificationId)
                .map(response -> {
                    log.info("Completed verification retrieval request: verificationId={}", verificationId);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    log.info("Verification was not found: verificationId={}", verificationId);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/verifications")
    public List<VerificationResponse> findAll()
    {
        log.info("Received request to retrieve all verifications");
        return verificationService.findAll();
    }
}
