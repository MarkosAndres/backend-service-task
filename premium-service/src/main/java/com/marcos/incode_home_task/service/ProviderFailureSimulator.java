package com.marcos.incode_home_task.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class ProviderFailureSimulator
{
    public boolean isUnavailable(int failurePercentage)
    {
        return ThreadLocalRandom.current().nextInt(100) < failurePercentage;
    }
}
