package com.marcos.incode_home_task.service

import spock.lang.Specification

import java.util.function.IntSupplier

class ProviderFailureSimulatorSpec extends Specification
{
    def 'is unavailable when the generated percentage is below the configured threshold'()
    {
        expect:
        new ProviderFailureSimulator(({ generatedPercentage } as IntSupplier))
                .isUnavailable(failurePercentage) == unavailable

        where:
        generatedPercentage | failurePercentage || unavailable
        39                  | 40                || true
        40                  | 40                || false
        9                   | 10                || true
        10                  | 10                || false
    }
}
