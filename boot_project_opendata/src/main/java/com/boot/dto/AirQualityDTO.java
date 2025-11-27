package com.boot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AirQualityDTO {
    private String sidoName;
    private String stationName;
    private String dataTime;
    private int pm10Value;
    private int pm25Value;
    private Double o3Value;
    private Double no2Value;
    private Double so2Value;
    private Double coValue;
    private int pm10Grade;
    private int pm25Grade;
    private int o3Grade;
    private int no2Grade;
    private int khaiValue;
    private int khaiGrade;
    private double dmY;
    private double dmX;
}

