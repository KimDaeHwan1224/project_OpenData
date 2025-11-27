package com.boot.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class FavoriteStationDTO {
    private Long favoriteId;
    private String userId;

//    private Long stationId;      // ✅ FK
    private String stationName;  // 화면 표시용

    private Integer pm10Value;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // 있으면 유지
}
