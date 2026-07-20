package com.tocktalks.domain.portfolio.service;

import com.tocktalks.domain.portfolio.dto.AssetHistoryResponse;
import com.tocktalks.domain.portfolio.repository.AssetHistoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {
    
    private final AssetHistoryRepository assetHistoryRepository;
    
    public List<AssetHistoryResponse> getAssetHistory(Long roomParticipantId) {
        return assetHistoryRepository.findAllByRoomParticipantIdOrderBySnapshotDateAsc(roomParticipantId)
            .stream()
            .map(AssetHistoryResponse::from)
            .collect(Collectors.toList());
    }
}
