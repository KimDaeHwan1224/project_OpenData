<div align="center">

# 🌫️ 전국 미세먼지 실시간 지도 서비스  
### 공공데이터 API + Kakao Map + Spring Boot + Oracle 통합 프로젝트

<br>

<img src="https://img.shields.io/badge/Java-17-007396?logo=java">
<img src="https://img.shields.io/badge/SpringBoot-2.7-6DB33F?logo=springboot">
<img src="https://img.shields.io/badge/MyBatis-000000">
<img src="https://img.shields.io/badge/Oracle-F80000?logo=oracle">
<img src="https://img.shields.io/badge/KakaoMapAPI-FFCD00">
<img src="https://img.shields.io/badge/PublicData-0052CC">
<img src="https://img.shields.io/badge/AWS-232F3E?logo=amazonaws">

<br><br>
</div>

---

## 📖 프로젝트 개요

공공데이터 포털의 전국 미세먼지 측정소 데이터를 수집하고  
카카오 지도 API와 좌표 변환을 활용하여  
**“전국 대기질 정보의 실시간 시각화”**를 구현한 프로젝트입니다.

- 전국 500여개 측정소 데이터 실시간 조회  
- 주소 → 좌표 변환 후 지도 마커 표시  
- 지역(시·도)별 미세먼지 평균 산출  
- 사용자 지도 인터랙션 기반 정보창 표시  
- Spring Boot + Oracle + Kakao API 완전 통합

---

## 🛠 기술 스택

| 분야 | 기술 |
|------|-------|
| **Backend** | Spring Boot, Java 17, MyBatis |
| **Frontend** | Kakao Map API, JavaScript, AJAX, JSP |
| **Database** | Oracle 11g / 19c |
| **API** | 공공데이터 대기질 API, Kakao Geocoding API |
| **DevOps** | AWS EC2(Ubuntu), Nginx/Tomcat |
| **Tools** | SQL Developer, Postman, ERDCloud |

---

## ✨ 주요 기능

### 📍 1. 공공데이터 실시간 수집
- 공공데이터 API(대기질) 호출  
- JSON → DTO 매핑 → Oracle DB 저장  
- 예외 처리 / API 장애 대비

---

### 🗺️ 2. Kakao Geocode API로 주소 → 좌표 변환
- 측정소 주소를 Kakao 주소검색 API로 변환  
- 위도·경도 좌표를 Oracle DB에 저장  
- 좌표 변환 실패 시 재시도 처리

---

### 📊 3. 시·도별 미세먼지 평균값 계산
- Oracle SQL로 지역별 집계  
- Spring 내부에서 PM10/PM2.5 평균 계산  
- 지도 상단에 평균 정보 시각화

---

### 🧭 4. 카카오 지도 기반 시각화
- 전국 측정소 마커 표시  
- 미세먼지 등급(좋음/보통/나쁨/매우나쁨)에 따라 색상 변경  
- 마커 클릭 시 상세 정보창 표시  
- 지도 클릭 시 오버레이 자동 닫힘

---

### 🔍 5. 검색 및 지역 이동 기능 (선택)
- 지역명 검색 시 해당 좌표로 지도 이동  
- 특정 측정소 하이라이트

---

## 🗂 ERD 및 테이블 명세서 (PDF)

📄 ERD  
➡ `/docs/erd.pdf`

📄 테이블 명세서  
➡ `/docs/table-definition.pdf`

---

## 🔍 핵심 구현 내용 (내가 담당한 기능)

### ✔ 1. 공공데이터 API 연동 + JSON 파싱
```java
String url = api + serviceKey + "&returnType=json";
ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
