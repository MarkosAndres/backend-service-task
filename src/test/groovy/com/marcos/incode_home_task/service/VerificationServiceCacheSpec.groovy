package com.marcos.incode_home_task.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.benmanes.caffeine.cache.Caffeine
import com.marcos.incode_home_task.config.CacheConfiguration
import com.marcos.incode_home_task.dto.BackendResponse
import com.marcos.incode_home_task.dto.SearchResult
import com.marcos.incode_home_task.dto.VerificationSource
import com.marcos.incode_home_task.entity.VerificationEntity
import com.marcos.incode_home_task.metrics.ApplicationMetrics
import com.marcos.incode_home_task.repository.VerificationRepository
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import spock.lang.Specification

import java.time.Instant
import java.time.Duration

@SpringJUnitConfig(classes = [CacheConfiguration, VerificationServiceCacheTestConfiguration])
class VerificationServiceCacheSpec extends Specification
{
    @Autowired
    VerificationService service
    @Autowired
    ObjectMapper objectMapper
    @SpringBean
    VerificationRepository repository = Mock()
    @SpringBean
    ApplicationMetrics metrics = Mock()

    def 'serializes and persists completed verification'()
    {
        given:
        def verificationId = UUID.randomUUID()
        def response = new BackendResponse(verificationId, 'acme', SearchResult.noResults())

        when:
        service.store(response, VerificationSource.FREE, Instant.parse('2025-01-01T00:00:00Z'))

        then:
        1 * repository.save({ VerificationEntity entity ->
            entity.verificationId == verificationId &&
                    entity.queryText == 'acme' &&
                    entity.source == VerificationSource.FREE &&
                    entity.result.contains('NO_RESULTS')
        })
        1 * metrics.verificationCompleted(VerificationSource.FREE, 'NO_RESULTS')
    }

    def 'returns a cached verification on repeated lookup by id'()
    {
        given:
        def verificationId = UUID.randomUUID()
        def verification = verificationEntity(verificationId, 'first result')

        when:
        def firstLookup = service.findByVerificationId(verificationId)
        def secondLookup = service.findByVerificationId(verificationId)

        then:
        firstLookup.get().queryText() == 'first result'
        secondLookup.get().queryText() == 'first result'
        1 * repository.findById(verificationId) >> Optional.of(verification)
    }

    def 'maps a stored entity back to response'()
    {
        given:
        def verificationId = UUID.randomUUID()
        repository.findById(verificationId) >> Optional.of(verificationEntity(verificationId, 'acme'))

        expect:
        service.findByVerificationId(verificationId).get().result().result().status() == 'NO_RESULTS'
    }

    def 'maps all stored entities'()
    {
        given:
        repository.findAll() >> []

        expect:
        service.findAll() == []
    }

    def 'evicts a cached verification when that id is stored'()
    {
        given:
        def verificationId = UUID.randomUUID()
        def initialVerification = verificationEntity(verificationId, 'old result')
        def updatedVerification = verificationEntity(verificationId, 'new result')
        def response = new BackendResponse(verificationId, 'new result', SearchResult.noResults())

        when:
        service.findByVerificationId(verificationId)
        service.store(response, VerificationSource.PREMIUM, Instant.parse('2025-01-01T00:00:00Z'))
        def lookupAfterStore = service.findByVerificationId(verificationId)

        then:
        lookupAfterStore.get().queryText() == 'new result'
        2 * repository.findById(verificationId) >>> [Optional.of(initialVerification), Optional.of(updatedVerification)]
        1 * repository.save(_ as VerificationEntity)
        1 * metrics.verificationCompleted(VerificationSource.PREMIUM, 'NO_RESULTS')
    }

    private VerificationEntity verificationEntity(UUID verificationId, String query)
    {
        def response = new BackendResponse(verificationId, query, SearchResult.noResults())
        return new VerificationEntity(
                verificationId,
                query,
                Instant.parse('2025-01-01T00:00:00Z'),
                objectMapper.writeValueAsString(response),
                VerificationSource.FREE)
    }
}

@TestConfiguration
class VerificationServiceCacheTestConfiguration
{
    @Bean
    CacheManager cacheManager()
    {
        def cacheManager = new CaffeineCacheManager('verificationById')
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(Duration.ofMinutes(30)))
        return cacheManager
    }

    @Bean
    ObjectMapper objectMapper()
    {
        return new ObjectMapper().findAndRegisterModules()
    }

    @Bean
    VerificationService verificationService(
            VerificationRepository repository,
            ObjectMapper objectMapper,
            ApplicationMetrics metrics)
    {
        return new VerificationService(repository, objectMapper, metrics)
    }
}
