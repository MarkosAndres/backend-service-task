package com.marcos.incode_home_task.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcos.incode_home_task.dto.BackendResponse;
import com.marcos.incode_home_task.dto.VerificationResponse;
import com.marcos.incode_home_task.verification.VerificationEntity;
import com.marcos.incode_home_task.verification.VerificationRepository;
import com.marcos.incode_home_task.verification.VerificationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;
import java.util.Optional;
import java.util.List;

@Service
public class VerificationService
{
    private static final Logger log = LoggerFactory.getLogger(VerificationService.class);

    private final VerificationRepository verificationRepository;
    private final ObjectMapper objectMapper;
    public VerificationService(VerificationRepository verificationRepository, ObjectMapper objectMapper)
    {
        this.verificationRepository = verificationRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void store(BackendResponse result, VerificationSource source, Instant timestamp)
    {
        UUID verificationId = result.verificationId();
        VerificationEntity verification = new VerificationEntity(
                verificationId,
                result.query(),
                timestamp,
                serialize(result),
                source);

        verificationRepository.save(verification);
        log.info("Stored verification: source={}, resultStatus={}", source, result.result().status());
    }

    public Optional<VerificationResponse> findByVerificationId(UUID verificationId)
    {
        log.info("Retrieving verification");
        return verificationRepository.findById(verificationId)
                .map(verification -> new VerificationResponse(
                        verification.getVerificationId(),
                        verification.getQueryText(),
                        verification.getTimestamp(),
                        deserialize(verification.getResult()),
                        verification.getSource()));
    }

    public List<VerificationResponse> findAll()
    {
        List<VerificationResponse> verifications = verificationRepository.findAll().stream()
                .map(verification -> new VerificationResponse(
                        verification.getVerificationId(),
                        verification.getQueryText(),
                        verification.getTimestamp(),
                        deserialize(verification.getResult()),
                        verification.getSource()))
                .toList();
        log.info("Retrieved all verifications: resultCount={}", verifications.size());
        return verifications;
    }

    private String serialize(BackendResponse response)
    {
        try
        {
            return objectMapper.writeValueAsString(response);
        }
        catch (JsonProcessingException exception)
        {
            throw new IllegalStateException("Unable to serialize verification result.", exception);
        }
    }

    private BackendResponse deserialize(String result)
    {
        try
        {
            return objectMapper.readValue(result, BackendResponse.class);
        }
        catch (JsonProcessingException exception)
        {
            throw new IllegalStateException("Unable to deserialize stored verification result.", exception);
        }
    }
}
