# 보호소 탭 기획 (Shelter)

> **Route:** `shelter`  
> **관련 문서:** [PLANNING.md](./PLANNING.md) §3.1, §6.2 · [HOME.md](./HOME.md) · [DETAIL.md](./DETAIL.md) · [DESIGN_GUIDE.md](../DESIGN_GUIDE.md) · [PUBLIC_DATA_API.md](../PUBLIC_DATA_API.md)

---

## 1. 화면 개요

### 1.1 한 줄 정의
**시·도 / 시·군·구를 선택해 해당 지역 보호소 목록을 조회하고, 전화·주소로 입양·방문 문의에 바로 연결하는 탭.**

### 1.2 화면 목표

| 목표 | 설명 |
| :--- | :--- |
| **접근성** | 동물 공고와 무관하게, 지역 기준으로 보호소 연락처를 빠르게 찾게 한다 |
| **신뢰** | 공공데이터(`shelter_v2`) 기반 공식 보호소명·주소·전화를 그대로 노출한다 |
| **연결** | 보호소 → (Phase 2) 해당 보호소 유기동물 목록 → 동물 상세로 탐색 흐름을 만든다 |

### 1.3 사용자 시나리오

1. 하단 탭에서 **보호소** 선택
2. **시·도 → 시·군·구**를 고른다 (API 필수 조건)
3. 해당 지역 보호소 카드 목록 확인 (이름, 소속 기관, 주소, 전화)
4. **전화** 버튼 탭 → `ACTION_DIAL`로 입양·방문 문의
5. (Phase 2) 보호소 카드 탭 → **보호소 상세** → "이 보호소 유기동물 보기" → 홈 필터 연동
6. (Phase 2) 동물 상세의 보호소명 탭 → 보호소 탭으로 이동 (이름·전화 매칭)

### 1.4 탭 배치 의도

보호소 탭은 **2번 탭**에 둡니다 ([PLANNING.md](./PLANNING.md) §6.1).

- MVP에서 **마음에 두기** 탭이 비어 있어도, 보호소 탭은 지역 선택만으로 즉시 가치를 제공합니다.
- FindMe의 핵심 여정 **탐색(홈) → 연락(보호소) → 관심(마음에 두기) → 관리(마이)** 중 **연락** 단계를 담당합니다.

---

## 2. 화면 구조 (와이어프레임)

### 2.1 목록 화면 (MVP)

```
┌─────────────────────────────────────┐
│  보호소                      [지역 ≡]│  ← Top App Bar
├─────────────────────────────────────┤
│  서울특별시 · 강남구                  │  ← 선택 지역 요약 (탭 시 지역 시트)
├─────────────────────────────────────┤
│  보호소 12곳                         │  ← 결과 요약
├─────────────────────────────────────┤
│ ┌─────────────────────────────────┐ │
│ │ 🏠 ○○동물보호센터                │ │  ← ShelterCard
│ │    서울특별시 ○○구 ○○로 123      │ │
│ │    📞 02-000-0000    [ 전화 ]   │ │
│ └─────────────────────────────────┘ │
│ ┌─────────────────────────────────┐ │
│ │ 🏠 △△시립동물보호센터            │ │
│ │    서울특별시 ○○구 ...           │ │
│ │    📞 02-111-1111    [ 전화 ]   │ │
│ └─────────────────────────────────┘ │
│         ... (스크롤)                 │
├─────────────────────────────────────┤
│  홈  │ 보호소 │ 마음에 두기 │ 마이   │  ← Bottom Nav (유지)
└─────────────────────────────────────┘
```

### 2.2 지역 미선택 (Empty — 초기 진입)

```
┌─────────────────────────────────────┐
│  보호소                      [지역 ≡]│
├─────────────────────────────────────┤
│                                     │
│           🐾                        │
│     지역을 선택해 주세요              │
│  시·도와 시·군·구를 고르면           │
│  해당 지역 보호소를 찾을 수 있어요     │
│                                     │
│        [ 지역 선택하기 ]             │
│                                     │
└─────────────────────────────────────┘
```

### 2.3 지역 선택 Bottom Sheet

홈 탭의 지역 필터와 **동일한 UX**를 재사용합니다 (`HomeRegionFilterBottomSheet` 패턴).

```
┌─────────────────────────────────────┐
│  지역 선택                     [닫기]│
├─────────────────────────────────────┤
│  시·도    [ 서울특별시        ▼ ]   │
│  시·군·구  [ 강남구            ▼ ]   │  ← 시·도 선택 후 활성화
├─────────────────────────────────────┤
│  [ 초기화 ]              [ 적용 ]   │
└─────────────────────────────────────┘
```

> **API 제약:** `shelter_v2`는 `upr_cd`(시·도)와 `org_cd`(시·군·구) **모두 필수**입니다.  
> 시·도만 선택한 상태로는 목록 API를 호출할 수 없으므로, **시·군·구까지 선택해야** 목록을 불러옵니다.

**레이아웃 원칙** ([DESIGN_GUIDE.md](../DESIGN_GUIDE.md) 준수)

- 화면 마진: `16.dp`
- 카드 모서리: `16.dp`, elevation `2~4.dp`
- 카드 간격: `12.dp`
- Primary CTA(전화): `Corner Radius 12.dp`, Secondary(`#4DB6AC`) 또는 Outlined

---

## 3. UI 구성 요소

### 3.1 Top App Bar

| 요소 | MVP | Phase 2 |
| :--- | :---: | :---: |
| 타이틀 | `보호소` | 동일 |
| 지역 필터 아이콘 | ✅ Bottom Sheet 열기 | 동일 |
| 검색 | 🔲 | 보호소명·주소 클라이언트 필터 |

### 3.2 지역 선택 영역

| 항목 | 스펙 |
| :--- | :--- |
| 데이터 | `GetSidoListUseCase`, `GetSigunguListUseCase` (홈과 공유) |
| UI | Bottom Sheet + Dropdown 2단 (시·도 → 시·군·구) |
| 적용 | 시·군·구 선택 후 **적용** 시 `GetShelterListUseCase` 호출 |
| 상태 유지 | 탭 전환 시 `saveState` / `restoreState`로 선택 지역·스크롤 보존 |
| 홈과 공유 | 🔲 Phase 2 — `RegionSelectionRepository` 또는 DataStore로 마지막 지역 공유 |

### 3.3 결과 요약

| 상태 | 문구 예시 |
| :--- | :--- |
| 로딩 중 | (Progress bar 또는 스켈레톤) |
| 성공 | `보호소 N곳` |
| 빈 결과 | `선택한 지역에 등록된 보호소가 없습니다` |
| 에러 | `보호소 정보를 불러오지 못했습니다` + [다시 시도] |

### 3.4 ShelterCard (보호소 카드)

| UI 요소 | Domain / API | 비고 |
| :--- | :--- | :--- |
| 보호소명 | `Shelter.name` (`careNm`) | Head 2, 1~2줄 ellipsis |
| 소속 기관 | `Shelter.orgName` (`orgNm`) | Body 2, `#757575` |
| 주소 | `Shelter.address` (`careAddr`) | Body 2, 2줄 ellipsis |
| 전화번호 | `Shelter.tel` (`careTel`) | 없으면 "연락처 없음" + 전화 버튼 비활성 |
| 전화 버튼 | — | `Intent(ACTION_DIAL)` (상세 CTA와 동일) |
| 카드 탭 | — | MVP: 없음 / Phase 2: 보호소 상세 이동 |

**접근성:** `contentDescription = "{보호소명}, {주소}, 전화 {번호}"`

### 3.5 보호소 상세 (Phase 2)

Route: `shelter/{careRegNo}` — Bottom Bar **숨김**

```
┌─────────────────────────────────────┐
│  ←  ○○동물보호센터                   │
├─────────────────────────────────────┤
│  소속    서울특별시 ○○구             │
│  주소    서울특별시 ...              │  ← 탭 시 클립보드 복사
│  전화    02-000-0000                 │
├─────────────────────────────────────┤
│  [ 📞 보호소에 전화 ]                │  ← Sticky CTA
│  [ 🐾 이 보호소 유기동물 보기 ]      │  ← 홈 탭 + 보호소 필터 (Phase 2)
└─────────────────────────────────────┘
```

---

## 4. 상태 (UiState)

```kotlin
data class ShelterUiState(
    val selectedSido: Region? = null,
    val selectedSigungu: Region? = null,
    val draftSido: Region? = null,
    val draftSigungu: Region? = null,
    val sidoList: List<Region> = emptyList(),
    val sigunguList: List<Region> = emptyList(),
    val shelters: List<ShelterUiModel> = emptyList(),
    val isSidoListLoading: Boolean = false,
    val isSigunguListLoading: Boolean = false,
    val isShelterListLoading: Boolean = false,
    val isRegionFilterSheetVisible: Boolean = false,
    val errorMessage: String? = null,
    val hasAppliedRegion: Boolean = false, // 시·군·구 적용 완료 여부
)
```

### 4.1 상태 다이어그램

```
[초기] ── 지역 미선택 Empty
   │
   ├─ 지역 시트 → 시·군·구 적용
   │
   ▼
[Loading] ── API shelter_v2
   │
   ├─ 성공 & items > 0 → [Content] 목록
   ├─ 성공 & items = 0 → [Empty] 결과 없음
   └─ 실패 → [Error] + Retry
```

---

## 5. 데이터 · API

### 5.1 사용 API

| API | 용도 | 비고 |
| :--- | :--- | :--- |
| `sido_v2` | 시·도 목록 | 홈과 동일 |
| `sigungu_v2` | 시·군·구 목록 | `upr_cd` = 시·도 코드 |
| `shelter_v2` | 보호소 목록 | `upr_cd` + `org_cd` **필수** |

### 5.2 shelter_v2 요청 파라미터

| 파라미터 | Domain | API | 필수 |
| :--- | :--- | :--- | :---: |
| 시·도 | `selectedSido.code` | `upr_cd` | ✅ |
| 시·군·구 | `selectedSigungu.code` | `org_cd` | ✅ |
| 페이지 | `pageNo` | `pageNo` | |
| 페이지 크기 | `numOfRows` | `numOfRows` | 기본 `100` |

> 한 시·군·구 내 보호소 수는 보통 수십 곳 이하입니다. MVP는 **페이지 1회 조회(`numOfRows=100`)**로 충분합니다.  
> `totalCount` 초과 시 Phase 2에서 무한 스크롤 또는 페이지네이션을 추가합니다.

### 5.3 응답 → Domain 매핑

| API (`ShelterItemDto`) | Domain (`Shelter`) | UI |
| :--- | :--- | :--- |
| `careRegNo` | `careRegNo` | 상세 Route 키 (Phase 2) |
| `careNm` | `name` | 카드 제목 |
| `orgNm` | `orgName` | 소속 기관 |
| `careTel` | `tel` | 전화 |
| `careAddr` | `address` | 주소 |

매핑 구현: `PublicDataMapper.kt` — `ShelterItemDto.toDomain()` ✅

### 5.4 기존 코드 활용

| 레이어 | 상태 | 경로 |
| :--- | :--- | :--- |
| API | ✅ | `AbandonmentPublicApi.getShelterList` |
| Repository | ✅ | `ShelterRepository` / `ShelterRepositoryImpl` |
| Domain Model | ✅ | `Shelter.kt` |
| UseCase | 🔲 | `GetShelterListUseCase` (신규) |
| Region UseCase | ✅ | `GetSidoListUseCase`, `GetSigunguListUseCase` |
| Presentation | 🔲 | `ShelterScreen`, `ShelterViewModel` |

### 5.5 동물 공고 API와의 관계

유기동물 목록(`abandonmentPublic_v2`)에는 **보호소 등록번호(`careRegNo`)가 없고** `careNm`, `careTel`만 제공됩니다.

| 연동 | MVP | Phase 2 |
| :--- | :--- | :--- |
| 상세 → 보호소 탭 | 🔲 | 보호소명 + 전화번호로 목록 내 **퍼지 매칭** |
| 보호소 → 홈 필터 | 🔲 | `careNm` 또는 보호소명 기준 홈 필터 (API `care_reg_no` 파라미터 없음 — **클라이언트 필터** 또는 Phase 3 API 조사) |
| 보호소 상세 → 유기동물 | 🔲 | 홈 탭 이동 + 해당 보호소명 포함 공고만 표시 (클라이언트 필터) |

---

## 6. 내비게이션

| From | Action | To |
| :--- | :--- | :--- |
| Bottom Bar | 보호소 탭 선택 | `shelter` (top-level, Bottom Bar 유지) |
| ShelterCard 전화 | `ACTION_DIAL` | 외부 전화 앱 |
| (Phase 2) ShelterCard 탭 | `navigate(shelter/{careRegNo})` | 보호소 상세 |
| (Phase 2) 상세 > 보호소명 | 보호소 탭 + 매칭 | `shelter` |
| (Phase 2) 상세 > "유기동물 보기" | 홈 탭 + 필터 | `home` |

- 보호소 **상세**는 top-level 바깥 push → Bottom Bar 숨김 ([PLANNING.md](./PLANNING.md) §6.3)
- `FindMeRoute` 확장 예: `SHELTER_DETAIL = "shelter/{careRegNo}"`

---

## 7. MVP vs 로드맵

### MVP (1차 출시)

- [x] `ShelterScreen` + `ShelterViewModel` + `ShelterUiState`
- [x] `GetShelterListUseCase`
- [x] 지역 선택 Bottom Sheet (시·도 / 시·군·구)
- [x] 보호소 카드 리스트 (이름, 소속, 주소, 전화)
- [x] 전화 CTA (`ACTION_DIAL`, 번호 없으면 비활성)
- [x] Empty (지역 미선택 / 결과 없음) / Error / Retry UI
- [ ] 탭 상태 유지 (지역 선택, 스크롤) — NavHost `saveState` 기본 동작

### Phase 2

- [ ] 보호소 상세 화면 (`shelter/{careRegNo}`)
- [ ] 주소 클립보드 복사
- [ ] 보호소명 검색 (클라이언트 필터)
- [ ] "이 보호소 유기동물 보기" → 홈 연동
- [ ] 동물 상세 → 보호소 탭 딥링크 (이름·전화 매칭)
- [ ] 홈과 **마지막 선택 지역** 공유 (DataStore)
- [ ] Pull-to-refresh
- [ ] 스켈레톤 로딩

### Phase 3

- [ ] 지도 표시 (주소 → 좌표 Geocoding, Kakao/Naver Maps SDK)
- [ ] 길찾기 Intent
- [ ] 위치 기반 **가까운 보호소** (GPS + 거리순)
- [ ] 보호소 즐겨찾기 (Room)
- [ ] 공공 API 보호소 단건·등록번호 필터 지원 여부 재조사

---

## 8. 비기능 요구사항

| 항목 | 기준 |
| :--- | :--- |
| **성능** | 지역 적용 후 첫 목록 로딩 3초 이내 |
| **캐싱** | Phase 2 — 시·도/시·군·구 코드 Room 또는 메모리 캐시 (홈과 공유) |
| **API 부하** | 동일 지역 재조회 시 짧은 TTL 캐시 (예: 5분) |
| **접근성** | 카드·전화 버튼 `contentDescription` 제공 |
| **오프라인** | Phase 2 — 마지막 조회 목록 표시 + 오프라인 배너 |
| **개인정보** | 위치(GPS)는 Phase 3, 사용 전 권한·목적 고지 |

---

## 9. 구현 위치 (코드)

| 레이어 | 경로 (예정) |
| :--- | :--- |
| Screen | `presentation/shelter/ShelterScreen.kt` |
| ViewModel | `presentation/shelter/ShelterViewModel.kt` |
| UiState | `presentation/shelter/ShelterUiState.kt` |
| UiModel | `presentation/shelter/model/ShelterUiModel.kt` |
| Components | `presentation/shelter/component/ShelterCard.kt` |
| Region Sheet | `presentation/shelter/component/ShelterRegionFilterBottomSheet.kt` (홈 컴포넌트 추출·공유 검토) |
| UseCase | `domain/usecase/GetShelterListUseCase.kt` |
| Detail (P2) | `presentation/shelter/detail/ShelterDetailScreen.kt` |
| Nav | `FindMeNavHost.kt`, `FindMeRoute.kt` |
| Strings | `presentation/.../values/strings.xml` |

### 9.1 홈과의 컴포넌트 공유 제안

지역 Bottom Sheet 로직이 홈·보호소에서 **중복**됩니다.

| 방안 | 설명 |
| :--- | :--- |
| **A. 추출 (권장)** | `presentation/common/region/RegionFilterBottomSheet.kt` + 공통 ViewModel 헬퍼 |
| **B. 복제** | MVP 속도 우선, Phase 2에서 A로 리팩터 |

MVP는 **B(복제)** 로 빠르게 구현하고, 두 탭 안정화 후 **A**로 통합하는 것을 권장합니다.

---

## 10. 현재 구현 현황

| 항목 | 상태 |
| :--- | :--- |
| Route 등록 (`shelter`) | ✅ |
| `ShelterScreen` | ✅ |
| `ShelterRepository` / API | ✅ |
| `GetShelterListUseCase` | ✅ |
| Region API / UseCase | ✅ |
| 지역 선택 UI | ✅ |
| 보호소 카드 리스트 | ✅ |
| 전화 CTA | ✅ |
| 보호소 상세 | 🔲 |

---

## 11. 오픈 이슈 / 결정 필요

| # | 질문 | 제안 |
| :---: | :--- | :--- |
| 1 | 시·군·구 필수 — 초기 Empty UX | **지역 미선택 안내** + CTA로 시트 열기 (§2.2) |
| 2 | 목록 vs 1열 카드 | **1열 전폭 카드** — 텍스트(주소·기관)가 많아 2열 부적합 |
| 3 | 홈 지역 필터와 상태 공유 | MVP: **독립** / Phase 2: DataStore 공유 |
| 4 | 상세 → 보호소 탭 매칭 | `careRegNo` 없음 → **보호소명 + 전화** 일치 검색 (동명이 보호소 주의) |
| 5 | 보호소 → 홈 유기동물 필터 | API에 보호소 ID 필터 없음 → Phase 2 **클라이언트 필터** (`shelterName` 일치) |
| 6 | `orgNm` 표시 | 소속 지자체/기관명 — 카드에 **보조 텍스트**로 표시 |
| 7 | 전화 버튼 위치 | 카드 내 **Outlined 버튼** vs 아이콘 only — **Outlined + "전화"** (상세 CTA와 톤 통일) |
| 8 | 페이지네이션 | MVP **1페이지(100건)** / 초과 시 Phase 2 pagination |

---

*이 문서는 보호소 탭 구현 시 기준 문서입니다. 변경 시 [PLANNING.md](./PLANNING.md) §6.2와 동기화하세요.*
