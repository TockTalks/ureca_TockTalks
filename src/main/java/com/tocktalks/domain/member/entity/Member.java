package com.tocktalks.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "member", uniqueConstraints = {
        @UniqueConstraint(name = "uk_member_provider_sub", columnNames = {"provider", "provider_sub"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false, length = 20)
    private String provider; // local / kakao

    @Column(name = "provider_sub", length = 100)
    private String providerSub;

    @Column(nullable = false, length = 20)
    private String role; // user / admin

    @Column(nullable = false, length = 20)
    private String status; // active / blocked

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "reported_count", nullable = false)
    private int reportedCount;

    public static Member ofKakao(String email, String nickname, String providerSub) {
        Member member = new Member();
        member.email = email;
        member.nickname = nickname;
        member.provider = "kakao";
        member.providerSub = providerSub;
        member.role = "user";
        member.status = "active";
        member.createdAt = LocalDateTime.now();
        member.updatedAt = LocalDateTime.now();
        return member;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
        this.updatedAt = LocalDateTime.now();
    }

    public static Member ofLocal(String email, String encodedPassword, String nickname) {
        Member member = new Member();
        member.email = email;
        member.password = encodedPassword;
        member.nickname = nickname;
        member.provider = "local";
        member.role = "user";
        member.status = "active";
        member.createdAt = LocalDateTime.now();
        member.updatedAt = LocalDateTime.now();
        return member;
    }

    public void block() {
        if (isWithdrawn()) {
            throw new IllegalArgumentException("탈퇴한 회원은 차단할 수 없습니다.");
        }
        this.status = "blocked";
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isBlocked(){
        return "blocked".equals(this.status);
    }

    /**
     * 회원탈퇴 시 거래·방·게시글의 참조 무결성을 보존하면서 개인정보만 익명화한다.
     */
    public void withdraw() {
        String anonymousId = UUID.randomUUID().toString().replace("-", "");
        this.email = "withdrawn_" + anonymousId + "@withdrawn.local";
        this.password = null;
        this.nickname = "탈퇴한 회원";
        this.providerSub = null;
        this.status = "withdrawn";
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isWithdrawn() {
        return "withdrawn".equals(this.status);
    }

    public void increaseReportedCount() {
        this.reportedCount++;
        this.updatedAt = LocalDateTime.now();
    }

}
