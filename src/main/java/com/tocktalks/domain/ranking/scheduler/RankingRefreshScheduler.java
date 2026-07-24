package com.tocktalks.domain.ranking.scheduler;

import com.tocktalks.domain.price.service.KisPriceService;
import com.tocktalks.domain.ranking.service.RankingService;
import com.tocktalks.domain.room.entity.Room;
import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.domain.room.repository.RoomRepository;
import com.tocktalks.domain.trade.entity.Holding;
import com.tocktalks.domain.trade.repository.HoldingRepository;
import com.tocktalks.domain.trade.service.CurrentPriceProvider;
import com.tocktalks.domain.trade.service.TradeAssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingRefreshScheduler {

    private static final String STATUS_ONGOING = "ongoing";
    private static final String PARTICIPANT_ACTIVE = "ACTIVE";
    private final RoomRepository roomRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final CurrentPriceProvider currentPriceProvider;
    private final TradeAssetService tradeAssetService;
    private final RankingService rankingService;
    private final HoldingRepository holdingRepository;

    // price:quote:* TTL(30초)과 맞춰서, 이 스케줄러가 돌 때 캐시가 거의 항상 살아있게 한다.
    // TTL보다 짧은 주기(기존 20초)로 돌면 캐시가 매번 만료된 상태라 매번 KIS를 새로 호출하게 된다.
    @Scheduled(fixedDelay = 30_000)
    public void refresh(){
        for(Room room : roomRepository.findByStatus(STATUS_ONGOING)){
            try{
                refreshRoom(room.getId());
            }catch(Exception e){
                log.warn("랭킹 주기 갱신 실패 roomId{}", room.getId(), e);
            }
        }
    }

    @Transactional(readOnly = true)
    void refreshRoom(Long roomId){
        List<RoomParticipant> participants = roomParticipantRepository.findByRoomIdAndStatus(roomId, PARTICIPANT_ACTIVE);
        if(participants.isEmpty()) return;

        List<Long> participantIds = participants.stream().map(RoomParticipant::getId).toList();
        Map<Long, List<Holding>> holdingsByParticipant = holdingRepository.findAllByRoomParticipantIdIn(participantIds).stream()
                .collect(Collectors.groupingBy(Holding::getRoomParticipantId));

        List<String> stockCodes = holdingsByParticipant.values().stream()
                .flatMap(List::stream)
                .map(Holding::getStockCode)
                .distinct().toList();

        Map<String, BigDecimal> priceByCode = currentPriceProvider.getCurrentPrices(stockCodes);

        if(!STATUS_ONGOING.equals(roomRepository.findById(roomId).map(Room::getStatus).orElse(null))) return;

        for(RoomParticipant participant : participants){
            List<Holding> holdings = holdingsByParticipant.getOrDefault(participant.getId(), List.of());
            long totalAsset = tradeAssetService.calculateTotalAsset(participant, holdings, priceByCode);
            rankingService.updateRanking(roomId, participant.getMemberId(), totalAsset, participant.getInitialSeedMoney());
        }

        rankingService.broadcastRanking(roomId);
    }
}
