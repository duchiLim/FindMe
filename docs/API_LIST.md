# 국가동물보호정보시스템 API 상세 목록

구조동물(유기동물) 조회를 위해 제공되는 Open API 엔드포인트와 각 기능별 상세 명세입니다.

## 1. 기본 정보
- **Base URL**: `http://apis.data.go.kr/1543061/abandonmentPublicService_v2/`
- **데이터 형식**: XML (기본) / JSON (요청 파라미터 `_type=json` 추가 시)
- **참고**: 구버전 `abandonmentPublicSrvc`는 더 이상 사용하지 않습니다. 서비스 URL과 상세 기능 코드 모두 `_v2`를 사용합니다.

## 2. API 엔드포인트 목록

| 기능 | 엔드포인트 | 설명 | 주요 파라미터 |
| :--- | :--- | :--- | :--- |
| **유기동물 조회** | `abandonmentPublic_v2` | 구조된 유기동물 목록 및 상세 정보 | `bgnde`, `endde`, `upkind`, `state` |
| **시도 조회** | `sido_v2` | 전국의 행정구역(시도) 코드 및 이름 | - |
| **시군구 조회** | `sigungu_v2` | 특정 시도에 속한 시군구 코드 및 이름 | `upr_cd` (시도코드) |
| **보호소 조회** | `shelter_v2` | 특정 시군구에 위치한 동물보호소 목록 | `upr_cd`, `org_cd` (시군구코드) |
| **품종 조회** | `kind_v2` | 축종별 세부 품종 코드 및 이름 | `up_kind_cd` (축종코드) |

## 3. 관련 연관 API
유기동물 구조 정보 외에 추가적으로 활용 가능한 서비스입니다.

### 동물보호센터 정보 조회
- **Base URL**: `http://apis.data.go.kr/1543061/animalShelterSrvc_v2/`
- **엔드포인트**: `shelterInfo_v2`
- **설명**: 전국의 동물보호센터 주소, 연락처, 운영시간 등 상세 정보

### 동물등록 정보 조회
- **Base URL**: `http://apis.data.go.kr/1543061/animalInfoSrvc_v3/`
- **엔드포인트**: `animalInfo_v3`
- **설명**: 등록된 반려동물의 정보 확인 (동물등록번호 필요)

## 4. 축종 코드 (upkind)
API 호출 시 `upkind` 파라미터에 사용되는 코드값입니다.

- **개**: `417000`
- **고양이**: `422400`
- **기타**: `429900`

---
*모든 API 호출 시 `serviceKey`와 `numOfRows`, `pageNo`는 공통적으로 포함되어야 합니다.*
