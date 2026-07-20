package com.tocktalks.domain.portfolio.dto;

import com.tocktalks.domain.portfolio.entity.AssetHistory;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AssetHistoryResponse {
    private LocalDate date;
    private Long totalAsset;
    
    // Entity를 DTO로 변환하는 정적 팩토리 메서드
    public static AssetHistoryResponse from(AssetHistory history) {
        return new AssetHistoryResponse(
            history.getSnapshotDate(),
            history.getTotalAsset()
        );
    }
}
