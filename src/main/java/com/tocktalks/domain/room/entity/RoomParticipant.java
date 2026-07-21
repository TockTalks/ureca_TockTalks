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

    @Column(nullable = false, length = 20)
    private String status; // ACTIVE / ENDED

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    public static RoomParticipant join(Long roomId, Long memberId, Long seedMoney) {
        RoomParticipant participant = new RoomParticipant();
        participant.roomId = roomId;
        participant.memberId = memberId;
        participant.balance = seedMoney;
        participant.initialSeedMoney = seedMoney;
        participant.status = "ACTIVE";
        participant.joinedAt = LocalDateTime.now();
        return participant;
    }

    public void end() {
        this.status = "ENDED";
        this.endedAt = LocalDateTime.now();
    }

    public void withdraw(long amount) {
        validateAmount(amount);

        if (this.balance < amount) {
            throw new IllegalArgumentException(
                    "거래 잔액이 부족합니다."
            );
        }

        this.balance -= amount;
    }

    public void deposit(long amount) {
        validateAmount(amount);

        try {
            this.balance = Math.addExact(
                    this.balance,
                    amount
            );
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException(
                    "거래 잔액이 허용 범위를 초과합니다.",
                    exception
            );
        }
    }

    private static void validateAmount(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "거래 금액은 1원 이상이어야 합니다."
            );
        }
    }
}
