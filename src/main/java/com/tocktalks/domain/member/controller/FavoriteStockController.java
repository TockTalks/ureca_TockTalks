package com.tocktalks.domain.member.controller;

import com.tocktalks.domain.member.dto.request.FavoriteStockRequest;
import com.tocktalks.domain.member.dto.response.FavoriteStockResponse;
import com.tocktalks.domain.member.service.FavoriteStockService;
import com.tocktalks.global.security.LoginMemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member/favorite-stocks")
@RequiredArgsConstructor
public class FavoriteStockController {

    private final FavoriteStockService favoriteStockService;

    @PostMapping
    public ResponseEntity<Void> addFavorite(
            @LoginMemberId Long memberId,
            @RequestBody FavoriteStockRequest request
    ) {
        favoriteStockService.addFavorite(memberId, request.stockCode());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{stockCode}")
    public ResponseEntity<Void> removeFavorite(
            @LoginMemberId Long memberId,
            @PathVariable String stockCode
    ) {
        favoriteStockService.removeFavorite(memberId, stockCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<FavoriteStockResponse>> getMyFavorites(@LoginMemberId Long memberId) {
        return ResponseEntity.ok(favoriteStockService.getMyFavorites(memberId));
    }
}