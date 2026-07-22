package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.domain.trade.dto.response.HoldingResponse;
import com.tocktalks.domain.trade.entity.Holding;
import com.tocktalks.domain.trade.repository.HoldingRepository;
import com.tocktalks.domain.trade.dto.response.HoldingSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientException;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HoldingQueryService {

    private final RoomParticipantRepository
            roomParticipantRepository;

    private final HoldingRepository holdingRepository;

    private final CurrentPriceProvider currentPriceProvider;

    public List<HoldingResponse> getHoldings(
            Long memberId,
            Long roomParticipantId
    ) {
        validateId(memberId, "회원 ID");
        validateId(roomParticipantId, "방 참가자 ID");

        RoomParticipant participant = roomParticipantRepository
                .findById(roomParticipantId)
                .orElseThrow(this::participantNotFound);

        if (!participant.getMemberId().equals(memberId)) {
            throw participantNotFound();
        }

        return holdingRepository
                .findAllByRoomParticipantId(roomParticipantId)
                .stream()
                .sorted(
                        Comparator.comparing(
                                Holding::getStockCode
                        )
                )
                .map(holding ->
                        HoldingResponse.from(
                                holding,
                                getValuationPrice(holding)
                        )
                )
                .toList();
    }

    public HoldingSummaryResponse getHoldingSummary(
            Long memberId,
            Long roomParticipantId
    ) {
        List<HoldingResponse> holdings =
                getHoldings(
                        memberId,
                        roomParticipantId
                );

        return HoldingSummaryResponse.from(holdings);
    }

    private BigDecimal getValuationPrice(
            Holding holding
    ) {
        try {
            return currentPriceProvider.getCurrentPrice(
                    holding.getStockCode()
            );
        } catch (WebClientException exception) {
            log.warn(
                    "KIS 현재가 조회 실패로 평균 매입가를 임시 사용합니다. "
                            + "stockCode={}, roomParticipantId={}, message={}",
                    holding.getStockCode(),
                    holding.getRoomParticipantId(),
                    exception.getMessage()
            );

            return holding.getAvgPrice();
        }
    }

    private IllegalArgumentException participantNotFound() {
        return new IllegalArgumentException(
                "보유 종목을 조회할 수 있는 참가자 정보를 찾을 수 없습니다."
        );
    }

    private void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + "가 올바르지 않습니다."
            );
        }
    }
}