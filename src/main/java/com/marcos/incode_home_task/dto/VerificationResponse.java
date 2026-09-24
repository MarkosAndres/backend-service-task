package com.marcos.incode_home_task.dto;

import java.time.Instant;
import java.util.UUID;

public record VerificationResponse(
        UUID verificationId,
        String queryText,
        Instant timestamp,
        BackendResponse result,
        VerificationSource source)
{
}
