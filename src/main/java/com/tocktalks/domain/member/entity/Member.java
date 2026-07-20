package com.tocktalks.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "member")
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
}
