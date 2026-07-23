package com.tocktalks.domain.member.repository;

import com.tocktalks.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByProviderAndProviderSub(String provider, String providerSub);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    //관리자 회원 검색 - 닉네임 또는 이메일로 검색
    Page<Member> findByNicknameContainingOrEmailContaining(String nickname, String email, Pageable pageable);

    Page<Member> findByReportedCountGreaterThanOrderByReportedCountDesc(int reportedCount, Pageable pageable);

}
