package com.boot.scheduler;

import com.boot.dto.AirQualityDTO;
import com.boot.service.AirQualityService;
import com.boot.service.RedisCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
/*
* 매시간 05분이 되면 AirQualityService로 API 호출
* 전국 대기질 데이터를 가져옴
* JSON으로 변환
* Redis "AIR:ALL_DATA" 키에 저장
* TTL(유효 시간)을 6시간으로 설정
*/
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "scheduler.enabled", havingValue = "true")
public class AirScheduler {

    private final AirQualityService airQualityService;
    private final RedisCacheService redisCacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String REDIS_KEY = "AIR:ALL_DATA";

//    /**
//     * cron = "0 5 * * * *""  → 정각 + 5분
    @Scheduled(cron = "0 5 * * * *")
    public void refreshAirData() {
        try {
            log.info("🔄 [스케줄러] 공공데이터 → Redis 갱신 시작");

            List<AirQualityDTO> list = airQualityService.getAllAirQuality();
            if (list == null || list.isEmpty()) {
                log.warn("⚠ 공공데이터 응답 없음. Redis 갱신 건너뜀");
                return;
            }

            String json = objectMapper.writeValueAsString(list);
            redisCacheService.set(REDIS_KEY, json, 21600); // TTL 6시간

            log.info("✅ [스케줄러] Redis 공기질 데이터 갱신 완료 ({}개)", list.size());

        } catch (Exception e) {
            log.error("❌ [스케줄러] 갱신 실패!", e);
        }
    }
}
