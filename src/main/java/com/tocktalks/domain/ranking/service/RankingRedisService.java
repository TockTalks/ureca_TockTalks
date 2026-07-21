package com.tocktalks.domain.ranking.service;

import com.tocktalks.domain.ranking.dto.response.RankingDto;
import com.tocktalks.domain.ranking.type.RankingType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RankingRedisService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String RETURN_KEY_PREFIX = "ranking:return:";
    private static final String ASSET_KEY_PREFIX = "ranking:asset:";

    private String keyOf(Long roomId, RankingType type){
        return(type == RankingType.RETURN_RATE ? RETURN_KEY_PREFIX : ASSET_KEY_PREFIX) + roomId;
    }

    public void updateRanking(Long roomId, Long memberId, BigDecimal returnRate, Long finalAsset){
        redisTemplate.opsForZSet().add(keyOf(roomId, RankingType.RETURN_RATE),
                String.valueOf(memberId), returnRate.doubleValue());
        redisTemplate.opsForZSet().add(keyOf(roomId, RankingType.TOTAL_ASSET),
                String.valueOf(memberId), finalAsset.doubleValue());
    }

    public List<RankingDto> getTopN(Long roomId, RankingType type, int n){
        String key = keyOf(roomId, type);
        Set<ZSetOperations.TypedTuple<String>> tuples =
                redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, n - 1);

        if(tuples == null || tuples.isEmpty()){
            return List.of();
        }

        List<ZSetOperations.TypedTuple<String>> list = List.copyOf(tuples);
        return IntStream.range(0, list.size())
                .mapToObj(i -> RankingDto.of(list.get(i), i + 1))
                .toList();
    }

    public List<RankingDto> getAll(Long roomId, RankingType type){
        String key = keyOf(roomId, type);
        Set<ZSetOperations.TypedTuple<String>> tuples =
                redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
        if(tuples == null || tuples.isEmpty()) {
            return List.of();
        }

        List<ZSetOperations.TypedTuple<String>> list = List.copyOf(tuples);
        return IntStream.range(0, list.size())
                .mapToObj(i -> RankingDto.of(list.get(i), i + 1))
                .toList();
    }

    // 아직 랭킹 데이터가 없는 회원(비로그인, 미참여 등)이면 null을 반환한다.
    public RankingDto getMyRank(Long roomId, Long memberId, RankingType type){
        if(memberId == null){
            return null;
        }

        String key = keyOf(roomId, type);
        Long rank = redisTemplate.opsForZSet().reverseRank(key, String.valueOf(memberId));
        Double score = redisTemplate.opsForZSet().score(key, String.valueOf(memberId));

        if(rank == null || score == null){
            return null;
        }
        return new RankingDto(memberId, score, rank.intValue() + 1);
    }

    public Integer getRank(Long roomId, Long memberId, RankingType type){
        Long rank = redisTemplate.opsForZSet()
                .reverseRank(keyOf(roomId, type), String.valueOf(memberId));
        return rank == null ? null : rank.intValue() + 1;
    }

    public Double getScore(Long roomId, Long memberId, RankingType type){
        return redisTemplate.opsForZSet().score(keyOf(roomId, type), String.valueOf(memberId));
    }

    public void clear(Long roomId){
        redisTemplate.delete(keyOf(roomId, RankingType.RETURN_RATE));
        redisTemplate.delete(keyOf(roomId, RankingType.TOTAL_ASSET));
    }
}
