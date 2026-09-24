package com.marcos.incode_home_task.metrics;

import com.marcos.incode_home_task.dto.VerificationSource;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ApplicationMetrics
{
    private final MeterRegistry meterRegistry;

    public ApplicationMetrics(MeterRegistry meterRegistry)
    {
        this.meterRegistry = meterRegistry;
    }

    public void verificationCompleted(VerificationSource source, String outcome)
    {
        meterRegistry
                .counter(
                        "verifications.completed",
                        "source",
                        source.name(),
                        "outcome",
                        outcome)
                .increment();
    }

    public void thirdPartyRequestFailed(VerificationSource source)
    {
        meterRegistry.counter(
                        "third.party.request.failures",
                        "provider", source.name().toLowerCase(Locale.ROOT))
                .increment();
    }

    public void thirdPartyRequestCalled(VerificationSource source)
    {
        meterRegistry.counter(
                        "third.party.request.calls",
                        "provider", source.name().toLowerCase(Locale.ROOT))
                .increment();
    }

    public void thirdPartyFallbackCalled(VerificationSource source)
    {
        meterRegistry.counter(
                        "third.party.fallback.calls",
                        "provider", source.name().toLowerCase(Locale.ROOT))
                .increment();
    }

    public void backendSearchFailed()
    {
        meterRegistry.counter("backend.search.failures").increment();
    }
}
