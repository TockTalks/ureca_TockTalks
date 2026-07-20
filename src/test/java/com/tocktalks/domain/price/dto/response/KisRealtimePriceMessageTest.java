
// KisRealtimePriceMessage.from() 함수가 잘 동작하는지 확인하는 테스트
// 삼성전자(예시) 종목 코드 사용

package com.tocktalks.domain.price.dto.response;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class KisRealtimePriceMessageTest {

    @Test
    void parsesRealtimeData() {
        String rawData = "005930^151952^71000^5^-500^-0.70^70900^71200^71500^70800^71000^70900^15^14670447";

        KisRealtimePriceMessage result = KisRealtimePriceMessage.from(rawData);

        assertThat(result.stockCode()).isEqualTo("005930");
        assertThat(result.currentPrice()).isEqualTo("71000");
        assertThat(result.changeSign()).isEqualTo("5");
        assertThat(result.priceChange()).isEqualTo("-500");
        assertThat(result.changeRate()).isEqualTo("-0.70");
        assertThat(result.accumulatedVolume()).isEqualTo("14670447");
    }
}