package com.marcos.incode_home_task.metrics;

import com.marcos.incode_home_task.dto.VerificationSource;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.concurrent.Callable;

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
        meterRegistry.counter(
                        "verifications.completed",
                        "source", source.name(),
                        "outcome", outcome)
                .increment();
    }

    public <T> T timeThirdPartyRequest(VerificationSource source, Callable<T> operation) throws Exception
    {
        return Timer.builder("third.party.request.duration")
                .tag("provider", source.name().toLowerCase(Locale.ROOT))
                .register(meterRegistry)
                .recordCallable(operation);
    }
}
