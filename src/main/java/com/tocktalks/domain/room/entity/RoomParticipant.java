package com.tocktalks.domain.room.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_participant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long balance;

    @Column(name = "initial_seed_money", nullable = false)
    private Long initialSeedMoney;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
}
