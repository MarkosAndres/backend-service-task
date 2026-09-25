package com.marcos.incode_home_task.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntSupplier;

@Component
public class ProviderFailureSimulator
{
    private final IntSupplier randomPercentage;

    public ProviderFailureSimulator()
    {
        this(() -> ThreadLocalRandom.current().nextInt(100));
    }

    ProviderFailureSimulator(IntSupplier randomPercentage)
    {
        this.randomPercentage = randomPercentage;
    }

    public boolean isUnavailable(int failurePercentage)
    {
        if (failurePercentage < 0 || failurePercentage > 100)
        {
            throw new IllegalArgumentException("Failure percentage must be between 0 and 100");
        }
        return randomPercentage.getAsInt() < failurePercentage;
    }
}
