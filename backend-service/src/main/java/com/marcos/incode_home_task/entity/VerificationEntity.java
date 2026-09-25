package com.marcos.incode_home_task.entity;

import com.marcos.incode_home_task.dto.VerificationSource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "verifications")
public class VerificationEntity
{
    @Id
    @Column(name = "verification_id", nullable = false, updatable = false)
    private UUID verificationId;

    @Column(name = "query_text", nullable = false)
    private String queryText;

    @Column(name = "requested_at", nullable = false, updatable = false)
    private Instant timestamp;

    @Column(name = "result", nullable = false, columnDefinition = "TEXT")
    private String result;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private VerificationSource source;

    protected VerificationEntity()
    {
    }

    public VerificationEntity(UUID verificationId, String queryText, Instant timestamp, String result,
            VerificationSource source)
    {
        this.verificationId = verificationId;
        this.queryText = queryText;
        this.timestamp = timestamp;
        this.result = result;
        this.source = source;
    }

    public UUID getVerificationId()
    {
        return verificationId;
    }

    public String getQueryText()
    {
        return queryText;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public String getResult()
    {
        return result;
    }

    public VerificationSource getSource()
    {
        return source;
    }
}
