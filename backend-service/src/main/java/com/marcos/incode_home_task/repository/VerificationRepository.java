package com.marcos.incode_home_task.repository;

import com.marcos.incode_home_task.entity.VerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerificationRepository extends JpaRepository<VerificationEntity, UUID>
{
}
