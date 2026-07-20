package com.tocktalks.domain.ranking.service;

import com.tocktalks.domain.ranking.dto.response.RankingArchiveResponse;
import com.tocktalks.domain.ranking.dto.response.RankingListResponse;
import com.tocktalks.domain.ranking.type.RankingType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RankingServiceTest {

    @Autowired
    private RankingService rankingService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final Long TEST_ROOM_ID = 9999L;

    @AfterEach
    void tearDown() {
        // 테스트 후 Redis 데이터 정리 (다음 테스트에 영향 안 주도록)
        redisTemplate.delete("ranking:return:" + TEST_ROOM_ID);
        redisTemplate.delete("ranking:asset:" + TEST_ROOM_ID);
    }

    @Test
    void 랭킹_업데이트_후_수익률_순위가_정상적으로_계산된다() {
        // given: memberId=100은 +20% 수익, memberId=101은 -10% 손실
        rankingService.updateRanking(TEST_ROOM_ID, 100L, 1_200_000L, 1_000_000L);
        rankingService.updateRanking(TEST_ROOM_ID, 101L, 900_000L, 1_000_000L);

        // when
        RankingListResponse response =
                rankingService.getRanking(TEST_ROOM_ID, 100L, RankingType.RETURN_RATE, 10);

        // then
        assertThat(response.topN()).hasSize(2);
        assertThat(response.topN().get(0).memberId()).isEqualTo(100L); // 수익률 높은 사람이 1등
        assertThat(response.topN().get(1).memberId()).isEqualTo(101L);
        assertThat(response.myRank().rank()).isEqualTo(1); // 100번은 1등이어야 함

        System.out.println("Top1: " + response.topN().get(0));
        System.out.println("MyRank: " + response.myRank());
    }

    @Test
    void 방_종료시_최종랭킹이_DB에_저장되고_Redis는_비워진다() {
        // given
        rankingService.updateRanking(TEST_ROOM_ID, 200L, 1_500_000L, 1_000_000L);
        rankingService.updateRanking(TEST_ROOM_ID, 201L, 800_000L, 1_000_000L);

        // when
        rankingService.finalizeRanking(TEST_ROOM_ID);

        // then: DB에 저장된 최종 랭킹 확인
        List<RankingArchiveResponse> finalRanking =
                rankingService.getFinalRanking(TEST_ROOM_ID, RankingType.RETURN_RATE);

        assertThat(finalRanking).hasSize(2);
        assertThat(finalRanking.get(0).memberId()).isEqualTo(200L);
        assertThat(finalRanking.get(0).finalRank()).isEqualTo(1);

        // then: Redis는 비워졌는지 확인
        Long remaining = redisTemplate.opsForZSet()
                .zCard("ranking:return:" + TEST_ROOM_ID);
        assertThat(remaining == null || remaining == 0).isTrue();

        System.out.println("최종 랭킹: " + finalRanking);
    }
}