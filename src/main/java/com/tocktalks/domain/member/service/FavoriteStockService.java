package com.tocktalks.domain.member.service;

import com.tocktalks.domain.member.dto.response.FavoriteStockResponse;
import com.tocktalks.domain.member.entity.FavoriteStock;
import com.tocktalks.domain.member.repository.FavoriteStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteStockService {

    private final FavoriteStockRepository favoriteStockRepository;

    @Transactional
    public void addFavorite(Long memberId, String stockCode, String stockName) {
        if (favoriteStockRepository.existsByMemberIdAndStockCode(memberId, stockCode)) {
            throw new IllegalArgumentException("이미 등록된 관심종목입니다.");
        }
        favoriteStockRepository.save(FavoriteStock.of(memberId, stockCode, stockName));
    }

    @Transactional
    public void removeFavorite(Long memberId, String stockCode) {
        favoriteStockRepository.deleteByMemberIdAndStockCode(memberId, stockCode);
    }

    public List<FavoriteStockResponse> getMyFavorites(Long memberId) {
        return favoriteStockRepository.findByMemberIdOrderByCreatedAtAsc(memberId)
                .stream()
                .map(FavoriteStockResponse::from)
                .toList();
    }
}