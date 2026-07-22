package com.tocktalks.domain.member.repository;

import com.tocktalks.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByProviderAndProviderSub(String provider, String providerSub);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
