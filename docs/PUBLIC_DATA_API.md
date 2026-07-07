# 공공데이터 API 연동 가이드

FindMe 서비스에서 사용하는 유기동물 관련 공공데이터 API 명세 및 활용 방법입니다.

## 1. 개요
- **서비스명**: 농림축산식품부 농림축산검역본부 - 국가동물보호정보시스템 구조동물 조회 서비스
- **제공기관**: 농림축산식품부 농림축산검역본부
- **데이터 형식**: REST API (XML/JSON 응답)
- **활용 목적**: 실시간 유기동물 공고 정보 확인, 지역별 보호소 조회, 품종별 필터링 기능 구현

## 2. API 인증 정보
- **플랫폼**: [공공데이터포털(data.go.kr)](https://www.data.go.kr/)
- **인증키 관리**: `local.properties` 또는 `secrets.properties`에 저장하여 관리 (보안 주의)
  ```properties
  # local.properties 예시
  PUBLIC_DATA_SERVICE_KEY=여러분의_인증키_입력
  ```

## 3. 주요 API 엔드포인트
- **Base URL**: `http://apis.data.go.kr/1543061/abandonmentPublicService_v2/`
- **구조동물(유기동물) 목록 조회**: `abandonmentPublic_v2`
- **시도/시군구 코드 조회**: `sido_v2`, `sigungu_v2`
- **보호소 정보 조회**: `shelter_v2`
- **품종 조회**: `kind_v2`

## 4. 핵심 요청 파라미터 (Request Parameters)
| 파라미터 | 설명 | 예시 값 |
| :--- | :--- | :--- |
| `serviceKey` | 공공데이터포털에서 발급받은 인증키 | (Encoding Key 사용 권장) |
| `bgnde` | 유기 날짜 시작일 (YYYYMMDD) | `20240101` |
| `endde` | 유기 날짜 종료일 (YYYYMMDD) | `20241231` |
| `upkind` | 축종 코드 | 개: `417000`, 고양이: `422400` |
| `state` | 상태 | 공고중: `notice`, 보호중: `protect` |
| `pageNo` | 페이지 번호 | `1` |
| `numOfRows` | 한 페이지 결과 수 | `10` |

## 5. 주요 응답 필드 (Response Fields)
| 필드명 | 설명 | 비고 |
| :--- | :--- | :--- |
| `happenPlace` | 발견 장소 | |
| `kindCd` | 품종 | `[개] 믹스견` 등 |
| `colorCd` | 색상 | |
| `age` | 나이 | `2024(년생)` |
| `weight` | 체중 | `5(Kg)` |
| `popfile` | 동물 사진 URL | 이미지 로딩 라이브러리(Coil 등) 사용 |
| `processState` | 상태 | `보호중`, `입양가능` 등 |
| `careNm` | 보호소 이름 | |
| `careTel` | 보호소 전화번호 | |
| `specialMark` | 특징 | 건강상태, 성격 등 상세 설명 |

## 6. 구현 참고사항
- **인증키 인코딩**: API 호출 시 인증키가 이미 인코딩되어 있다면 중복 인코딩되지 않도록 주의해야 합니다.
- **캐싱 정책**: 공공데이터 서버 부하를 줄이고 사용자 경험을 개선하기 위해 적절한 캐싱(예: Room DB) 활용을 권장합니다.
- **이미지 최적화**: `popfile`로 제공되는 이미지는 크기가 클 수 있으므로 효율적인 로딩 처리가 필요합니다.

---
*최신 API 명세는 공공데이터포털의 공식 문서를 참조하세요.*
