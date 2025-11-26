package com.boot.controller;

import com.boot.dto.AirQualityDTO;
import com.boot.service.AirQualityService;
import com.boot.service.RedisCacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/air")
@RequiredArgsConstructor
public class AirApiController {

	private final RedisCacheService redisCacheService;
    private final AirQualityService airQualityService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String REDIS_KEY = "AIR:ALL_DATA";

    /**
     * ============================================
     * 1) 전국 측정소 전체 조회 (17개 시도 API 호출)
     * ============================================
     */
    @GetMapping("/stations")
    public ResponseEntity<?> getAllStations() {

        try {
            String json = redisCacheService.get(REDIS_KEY);

            // ① 캐싱된 데이터 존재 → 즉시 반환
            if (json != null) {
                List<AirQualityDTO> list =
                        objectMapper.readValue(json, new TypeReference<List<AirQualityDTO>>() {});
                return ResponseEntity.ok(list);
            }

            // ② 캐싱 없음 → 공공데이터 즉시 호출 후 Redis 저장
            List<AirQualityDTO> list = airQualityService.getAllAirQuality();

            String newJson = objectMapper.writeValueAsString(list);
            redisCacheService.set(REDIS_KEY, newJson, 3600);

            return ResponseEntity.ok(list);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("ERROR: " + e.getMessage());
        }
    }

    /**
     * ============================================
     * 2) 특정 측정소 상세정보 조회
     *    (공공데이터 API에서 해당 시도를 다시 조회)
     * ============================================
     */
    @GetMapping("/station/{stationName}")
    public ResponseEntity<?> getStationDetail(@PathVariable String stationName) {

        try {
            // 1) Redis에서 전체 데이터 조회
            String json = redisCacheService.get(REDIS_KEY);

            // ❗ Redis가 null이면 → API 절대 호출 금지
            if (json == null) {
                return ResponseEntity.status(503).body(Map.of(
                        "success", false,
                        "message", "데이터가 아직 준비되지 않았습니다. 잠시 후 다시 시도해주세요."
                ));
            }

            // 2) JSON → List 변환
            List<AirQualityDTO> all = objectMapper.readValue(
                    json,
                    new TypeReference<List<AirQualityDTO>>() {}
            );

            // 3) 해당 측정소 찾기
            Optional<AirQualityDTO> match = all.stream()
                    .filter(s -> s.getStationName().equals(stationName))
                    .findFirst();

            if (match.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of(
                        "success", false,
                        "message", stationName + " 측정소 데이터 없음"
                ));
            }

            AirQualityDTO dto = match.get();

            // 4) 기존 JS 구조 유지
            Map<String, Object> item = new HashMap<>();
            item.put("stationName", dto.getStationName());
            item.put("sidoName", dto.getSidoName());
            item.put("dataTime", dto.getDataTime());

            item.put("pm10Value", dto.getPm10Value());
            item.put("pm25Value", dto.getPm25Value());
            item.put("o3Value", dto.getO3Value());
            item.put("no2Value", dto.getNo2Value());
            item.put("coValue", dto.getCoValue());
            item.put("so2Value", dto.getSo2Value());

            item.put("pm10Grade", dto.getPm10Grade());
            item.put("pm25Grade", dto.getPm25Grade());
            item.put("o3Grade", dto.getO3Grade());
            item.put("no2Grade", dto.getNo2Grade());
            item.put("khaiValue", dto.getKhaiValue());
            item.put("khaiGrade", dto.getKhaiGrade());

            item.put("calcO3", dto.getO3Grade());    
            item.put("calcNO2", dto.getNo2Grade());  
            Map<String, Object> body = Map.of("items", List.of(item));
            Map<String, Object> wrapper = Map.of("body", body);
            Map<String, Object> response = Map.of("response", wrapper);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("ERROR: " + e.getMessage());
        }
    }


    /**
     * ============================================
     * 3) 건강체크 (테스트용)
     * ============================================
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "mode", "PUBLIC_DATA",
                "message", "공공데이터 API 사용중"
        ));
    }


    /**
     * ============================================
     * 4) CSV 다운로드 (공공데이터 기반)
     * ============================================
     */
    @GetMapping("/download/csv")
    public void downloadCsv(javax.servlet.http.HttpServletResponse response) throws Exception {

        List<AirQualityDTO> list = airQualityService.getAllAirQuality();

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=" +
                        URLEncoder.encode("air_quality.csv", StandardCharsets.UTF_8));

        // BOM 추가
        response.getOutputStream().write(new byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF});

        var writer = new java.io.PrintWriter(response.getOutputStream(), true, StandardCharsets.UTF_8);

        writer.println("측정소,시도,위도,경도,PM10,PM2.5,O3,NO2,CO,SO2");

        for (AirQualityDTO s : list) {
            writer.printf("%s,%s,%f,%f,%d,%d,%f,%f,%f,%f\n",
                    s.getStationName(), s.getSidoName(),
                    s.getDmY(), s.getDmX(),
                    s.getPm10Value(), s.getPm25Value(),
                    s.getO3Value(), s.getNo2Value(),
                    s.getCoValue(), s.getSo2Value()
            );
        }
        writer.flush();
    }
}
