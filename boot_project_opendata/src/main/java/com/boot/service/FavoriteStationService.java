package com.boot.service;

import java.util.List;
import com.boot.dto.FavoriteStationDTO;

public interface FavoriteStationService {
    int add(FavoriteStationDTO dto);                 // insert
    int upsert(FavoriteStationDTO dto);              // merge

    // 파라미터 이름을 stationName 으로 통일
    int remove(String userId, String stationName);   // delete

    List<FavoriteStationDTO> list(String userId);

    FavoriteStationDTO get(String userId, String stationName);

    boolean toggle(FavoriteStationDTO dto);
}
