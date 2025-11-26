package com.boot.util;

import com.boot.dto.AirQualityDTO;
import com.boot.service.AirQualityService;
import com.boot.service.RedisCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(name = "redis.enabled", havingValue = "true")
@RequiredArgsConstructor
public class AirInitializer implements CommandLineRunner {

    private final AirQualityService airQualityService;
    private final RedisCacheService redisCacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String REDIS_KEY = "AIR:ALL_DATA";

    @Override
    public void run(String... args) {

        log.info("🚀 서버 부팅: Redis 공기질 캐싱 상태 확인 시작");

        String cached = redisCacheService.get(REDIS_KEY);
        if (cached != null) {
            log.info("✅ Redis 이미 존재 → API 호출 생략");
            return; // 부팅 즉시 끝
        }

        // ❗ 부팅을 지연시키지 않기 위해 비동기로 공공데이터 호출
        new Thread(() -> {
            try {
                log.info("⚠ Redis 캐시 없음 → 백그라운드에서 최초 API 호출 시작");

                // 🚀 병렬 처리된 빠른 API 호출 (AirQualityService 안에서 parallelStream 적용)
                List<AirQualityDTO> fresh = airQualityService.getAllAirQuality();

                String json = objectMapper.writeValueAsString(fresh);
                redisCacheService.set(REDIS_KEY, json, 21600); // TTL 6시간으로 확장

                log.info("🎉 공공데이터 캐싱 완료: 총 {}개 항목 저장", fresh.size());

            } catch (Exception e) {
                log.error("❌ 백그라운드 캐싱 실패", e);
            }
        }).start(); // 이게 핵심: 비동기 실행

        log.info("⏩ 서버 부팅 완료 (캐싱은 백그라운드에서 진행 중)");
    }
}
