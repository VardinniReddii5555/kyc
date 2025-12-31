package com.kyc.kycapp.repository;

import com.kyc.kycapp.entity.KycDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KycRepository extends JpaRepository<KycDetails, Long> {

    boolean existsByUsername(String username);

    boolean existsByPanNumber(String panNumber);

    KycDetails findByUsername(String username);
}
