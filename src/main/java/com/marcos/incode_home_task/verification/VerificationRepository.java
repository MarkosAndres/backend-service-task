package com.marcos.incode_home_task.verification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerificationRepository extends JpaRepository<VerificationEntity, UUID>
{
}
