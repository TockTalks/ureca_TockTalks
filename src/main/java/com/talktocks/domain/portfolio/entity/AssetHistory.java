package com.talktocks.domain.portfolio.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

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

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;
}
