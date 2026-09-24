package com.marcos.incode_home_task.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.marcos.incode_home_task.dto.*
import com.marcos.incode_home_task.entity.VerificationEntity
import com.marcos.incode_home_task.metrics.ApplicationMetrics
import com.marcos.incode_home_task.repository.VerificationRepository
import spock.lang.Specification

import java.time.Instant
import java.util.UUID

class VerificationServiceSpec extends Specification
{
    def repository = Mock(VerificationRepository)
    def metrics = Mock(ApplicationMetrics)
    def service = new VerificationService(repository, new ObjectMapper().findAndRegisterModules(), metrics)

    def 'serializes and persists completed verification'()
    {
        given:
        def id = UUID.randomUUID()
        def response = new BackendResponse(id, 'acme', SearchResult.noResults())

        when:
        service.store(response, VerificationSource.FREE, Instant.parse('2025-01-01T00:00:00Z'))

        then:
        1 * repository.save({ VerificationEntity entity ->
            entity.verificationId == id && entity.queryText == 'acme' && entity.source == VerificationSource.FREE && entity.result.contains('NO_RESULTS')
        })
        1 * metrics.verificationCompleted(VerificationSource.FREE, 'NO_RESULTS')
    }

    def 'maps a stored entity back to response'()
    {
        given:
        def id = UUID.randomUUID()
        def persisted = new VerificationEntity(id, 'acme', Instant.parse('2025-01-01T00:00:00Z'), new ObjectMapper().writeValueAsString(new BackendResponse(id, 'acme', SearchResult.noResults())), VerificationSource.PREMIUM)
        repository.findById(id) >> Optional.of(persisted)

        expect:
        service.findByVerificationId(id).get().result().result().status() == 'NO_RESULTS'
    }

    def 'maps all stored entities'()
    {
        given:
        repository.findAll() >> []

        expect:
        service.findAll() == []
    }
}
