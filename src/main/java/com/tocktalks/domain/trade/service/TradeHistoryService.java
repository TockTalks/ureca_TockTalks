package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.domain.trade.dto.response.TradeHistoryResponse;
import com.tocktalks.domain.trade.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradeHistoryService {

    private final RoomParticipantRepository
            roomParticipantRepository;

    private final TransactionRepository
            transactionRepository;

    public Page<TradeHistoryResponse> getTradeHistory(
            Long memberId,
            Long roomParticipantId,
            Pageable pageable
    ) {
        validateId(memberId, "회원 ID");
        validateId(roomParticipantId, "방 참가자 ID");

        RoomParticipant participant = roomParticipantRepository
                .findById(roomParticipantId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "거래 내역을 조회할 수 있는 참가자 정보를 찾을 수 없습니다."
                        )
                );

        if (!participant.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException(
                    "거래 내역을 조회할 수 있는 참가자 정보를 찾을 수 없습니다."
            );
        }

        return transactionRepository
                .findAllByRoomParticipantIdOrderByExecutedAtDesc(
                        roomParticipantId,
                        pageable
                )
                .map(TradeHistoryResponse::from);
    }

    private void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + "가 올바르지 않습니다."
            );
        }
    }
}