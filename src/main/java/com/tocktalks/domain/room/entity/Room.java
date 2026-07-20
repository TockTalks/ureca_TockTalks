package com.tocktalks.domain.room.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id")
    private Long ownerId; // 기본방은 NULL

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @Column(name = "seed_money", nullable = false)
    private Long seedMoney;

    @Column(name = "invite_code", length = 50)
    private String inviteCode;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(nullable = false, length = 20)
    private String status; // recruiting / ongoing / closed

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static Room createDefault(Long seedMoney) {
        Room room = new Room();
        room.name = "기본방";
        room.isDefault = true;
        room.isPublic = true;
        room.seedMoney = seedMoney;
        room.status = "ongoing";
        room.createdAt = LocalDateTime.now();
        return room;
    }

    public static Room createPrivate(Long ownerId, String name, boolean isPublic, Long seedMoney,
                                      LocalDateTime startAt, LocalDateTime endAt, Integer maxParticipants) {
        Room room = new Room();
        room.ownerId = ownerId;
        room.name = name;
        room.isDefault = false;
        room.isPublic = isPublic;
        room.seedMoney = seedMoney;
        room.inviteCode = isPublic ? null : generateInviteCode();
        room.maxParticipants = maxParticipants;
        room.startAt = startAt;
        room.endAt = endAt;
        room.status = "ongoing";
        room.createdAt = LocalDateTime.now();
        return room;
    }

    public void close() {
        this.status = "closed";
    }

    private static String generateInviteCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
