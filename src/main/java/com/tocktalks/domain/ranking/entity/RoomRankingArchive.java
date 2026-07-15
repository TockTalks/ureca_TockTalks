package com.tocktalks.domain.ranking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_ranking_archive")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomRankingArchive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "final_asset", nullable = false)
    private Long finalAsset;

    @Column(name = "final_return_rate", nullable = false, precision = 9, scale = 4)
    private BigDecimal finalReturnRate;

    @Column(name = "final_rank", nullable = false)
    private Integer finalRank;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static RoomRankingArchive of(Long roomId, Long memberId, Long finalAsset,
                                         BigDecimal finalReturnRate, Integer finalRank) {
        RoomRankingArchive archive = new RoomRankingArchive();
        archive.roomId = roomId;
        archive.memberId = memberId;
        archive.finalAsset = finalAsset;
        archive.finalReturnRate = finalReturnRate;
        archive.finalRank = finalRank;
        archive.createdAt = LocalDateTime.now();
        return archive;
    }
}
