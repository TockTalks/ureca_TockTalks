package com.tocktalks.domain.portfolio.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssetHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_participant_id", nullable = false)
    private Long roomParticipantId;

    @Column(name = "total_asset", nullable = false)
    private Long totalAsset;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;
    
    private AssetHistory(Long roomParticipantId, Long totalAsset, LocalDateTime recordedAt) {
        this.roomParticipantId = roomParticipantId;
        this.totalAsset = totalAsset;
        this.recordedAt = recordedAt;
    }
    
    public static AssetHistory create(Long roomParticipantId, Long totalAsset) {
        return new AssetHistory(roomParticipantId, totalAsset, LocalDateTime.now());
    }
}
