# 홈 화면 기획 (Home)

> **Route:** `home`  
> **관련 문서:** [PLANNING.md](./PLANNING.md) §3.1, §6.2 · [DESIGN_GUIDE.md](../DESIGN_GUIDE.md) · [PUBLIC_DATA_API.md](../PUBLIC_DATA_API.md)

---

## 1. 화면 개요

### 1.1 한 줄 정의
**전국 유기동물 공고를 사진 카드로 탐색하고, 필터로 원하는 조건의 동물을 찾아 상세 화면으로 진입하는 메인 탭.**

### 1.2 화면 목표
| 목표 | 설명 |
| :--- | :--- |
| **탐색** | 입양 희망자가 한눈에 여러 공고를 비교할 수 있게 한다 |
| **필터링** | 지역·축종·상태 등 조건으로 후보를 좁힌다 |
| **연결** | 관심 카드 → 상세 → (추후) 마음에 두기·보호소 문의로 이어진다 |

### 1.3 사용자 시나리오
1. 앱 실행 → 스플래시 → **홈 탭** 진입
2. 최근 30일 유기동물 목록이 카드로 표시된다
3. (선택) 필터 칩/바텀시트로 **개/고양이**, **공고중**, **내 지역** 등 적용
4. 카드 탭 → **동물 상세** (`animal/{desertionNo}`)
5. (추후) 카드의 **마음에 두기** 버튼 → 마음에 둔 목록 탭에 저장

---

## 2. 화면 구조 (와이어프레임)

```
┌─────────────────────────────────────┐
│  FindMe                    [필터 ≡] │  ← Top App Bar (선택)
├─────────────────────────────────────┤
│  [전체] [개] [고양이] [공고중] ...   │  ← 필터 칩 (가로 스크롤)
├─────────────────────────────────────┤
│  유기동물 1,234마리 · 최근 30일       │  ← 결과 요약
├─────────────────────────────────────┤
│ ┌──────────┐  ┌──────────┐         │
│ │  [사진]  │  │  [사진]  │         │  ← 2열 그리드 카드
│ │ 품종     │  │ 품종     │         │
│ │ 🟢 공고중│  │ 🔵 보호중│         │
│ │ 보호소명 │  │ 보호소명 │         │
│ └──────────┘  └──────────┘         │
│         ... (무한 스크롤)            │
├─────────────────────────────────────┤
│  홈  │ 보호소 │ 마음에 두기 │ 마이            │  ← Bottom Nav (유지)
└─────────────────────────────────────┘
```

**레이아웃 원칙** ([DESIGN_GUIDE.md](../DESIGN_GUIDE.md) 준수)
- 화면 마진: `16.dp`
- 카드 모서리: `16.dp`, elevation `2~4.dp`
- 카드 간격: `12.dp` (그리드 gutter)
- 사진 비율: 보기 형태별 상이 — **2열뷰 1:1**, **카드뷰 16:9**, **노멀뷰 썸네일 정사각** (§3.6)

---

## 3. UI 구성 요소

### 3.1 Top App Bar (Phase 2)
| 요소 | 설명 |
| :--- | :--- |
| 타이틀 | `FindMe` 또는 `유기동물` |
| 필터 아이콘 | 필터 바텀시트 열기 (지역·기간·상태 상세) |

> MVP에서는 Top Bar 없이 **필터 칩 + 리스트**만으로 시작해도 됩니다.

### 3.2 필터 영역

#### 빠른 필터 (Filter Chips) — MVP 권장
| 칩 | API 매핑 | 기본값 |
| :--- | :--- | :--- |
| 전체 | 필터 없음 | ✅ 선택 |
| 개 | `upkind=417000` | |
| 고양이 | `upkind=422400` | |
| 공고중 | `state=notice` | |
| 보호중 | `state=protect` | |

- 칩은 **단일 선택**(축종) + **상태는 토글 가능** 등 규칙을 Phase 2에서 정교화
- 칩 변경 시 **pageNo=1**으로 초기화 후 목록 재조회

#### 상세 필터 (Bottom Sheet) — Phase 2
| 항목 | UI | API 파라미터 |
| :--- | :--- | :--- |
| 시도 | Dropdown | `upr_cd` (연동: 시도 API) |
| 시군구 | Dropdown | `org_cd` (연동: 시군구 API) |
| 유기 기간 | Date range | `bgnde`, `endde` (YYYYMMDD) |
| 축종 | Radio | `upkind` |
| 상태 | Radio | `state` |
| 초기화 / 적용 | 버튼 | — |

**기본 기간:** 미지정 시 **최근 30일** (Repository 기본값과 동일)

### 3.3 결과 요약 바
| 표시 | 예시 |
| :--- | :--- |
| 총 건수 | `유기동물 1,234마리` |
| 적용 필터 요약 | `· 개 · 공고중 · 최근 30일` |
| (선택) 정렬 | `최신순` (API 기본: 최근 유기일 우선) |

### 3.4 동물 카드 (Animal Card)

카드 1장에 노출할 정보 (우선순위 순):

| 우선순위 | 필드 | Domain / API | UI |
| :---: | :--- | :--- | :--- |
| 1 | 사진 | `imageUrls` (`popfile1`, `popfile2`) | Coil, placeholder: 발바닥 아이콘 |
| 2 | 품종 | `kindName` (`kindNm`) | Body 1, 1줄 ellipsis |
| 3 | 상태 | `processState` | 상태 칩 (색상: DESIGN_GUIDE §2.3) |
| 4 | 보호소 | `shelterName` (`careNm`) | Body 2, 1줄 |
| 5 | (선택) 성별·중성 | `sex`, `neuterYn` | 아이콘 또는 짧은 텍스트 |

**카드 액션**
| 액션 | MVP | Phase 2 |
| :--- | :---: | :---: |
| 카드 전체 탭 → 상세 | ✅ | ✅ |
| 마음에 두기 버튼 | 🔲 | ✅ |
| 보호소 전화 | 🔲 | ✅ (카드 롱프레스 또는 상세에서) |

**상태 칩 색상 매핑** (문자열 contains 기준, 추후 Domain enum 정리)

| processState 키워드 | 색상 |
| :--- | :--- |
| 입양, 공고 | Green `#4CAF50` |
| 보호 | Blue `#2196F3` |
| 종료, 반환, 자연사 | Grey `#9E9E9E` |
| (추후) 안락사 임박 | Red `#F44336` |

### 3.5 리스트 레이아웃
| 항목 | 권장 |
| :--- | :--- |
| 형태 | **LazyVerticalGrid**, `GridCells.Fixed(2)` (기본 **2열뷰**) |
| 페이지 크기 | `numOfRows = 20` |
| 더 불러오기 | 스크롤 하단 도달 시 `pageNo++` (무한 스크롤) |
| Pull-to-refresh | Phase 2 (`SwipeRefresh`) |

### 3.6 목록 보기 형태 (Layout Mode)

Top App Bar의 **보기 전환** 버튼으로 아래 3가지를 순환합니다.  
**2열뷰 → 노멀뷰 → 카드뷰 → 2열뷰**

| 이름 | 코드 (`HomeLayoutMode`) | 레이아웃 | 컴포넌트 | 사진·텍스트 |
| :--- | :--- | :--- | :--- | :--- |
| **2열뷰** | `GRID` | 2열 그리드 (`LazyVerticalGrid`) | `AnimalCard` | **1:1** 정사각형 crop, 텍스트는 카드 하단 |
| **노멀뷰** | `LIST` | 1열 리스트 (`LazyColumn`) | `AnimalListCard` | 좌측 썸네일 **120dp** + 품종·상태·보호소 **각 1행** |
| **카드뷰** | `COVER` | 1열 리스트 (`LazyColumn`) | `AnimalCoverCard` | **16:9** 전폭 crop, 텍스트는 하단 그라데이션 위 |

**공통**
- 화면 마진 `16.dp`, 카드 간격 `12.dp`, 모서리 `16.dp`, elevation `2.dp`
- 큐레이션(오늘 등록·입양 임박), 무한 스크롤, 마음에 두기, 검색·필터 **스크롤 접힘** 동작은 세 보기 모두 동일
- Coil `ContentScale.Crop`으로 비율 영역을 채움

**와이어프레임 (요약)**

```
[2열뷰]                    [노멀뷰]                 [카드뷰]
┌────┐ ┌────┐            ┌─────────────────┐    ┌─────────────────┐
│ 1:1│ │ 1:1│            │[썸] 품종  [상태]│    │   16:9 사진     │
│텍스트│ │텍스트│            │     보호소      ♥│    │  (전체 crop)    ♥│
└────┘ └────┘            └─────────────────┘    │ 품종·보호소(오버)│
                                                └─────────────────┘
```

**구현 위치**
| 항목 | 경로 |
| :--- | :--- |
| Enum | `domain/model/HomeLayoutMode.kt` |
| 저장 | `data/settings/UserPreferencesDataStore.kt` (DataStore Preferences, key: `home_layout_mode`) |
| UiState | `HomeUiState.layoutMode` (기본값 `GRID` / 2열뷰, 앱 시작 시 DataStore에서 복원) |
| 전환 | `HomeViewModel.toggleLayoutMode()` → `SetHomeLayoutModeUseCase` |
| UI | `HomeScreen` — `HomeAnimalGrid` / `HomeAnimalList` / `HomeAnimalCoverList` |

---

## 4. 화면 상태 (UiState)

### 4.1 상태 다이어그램

```
        ┌──────────┐
        │ Loading  │  최초 진입 / 필터 변경 / refresh
        └────┬─────┘
             │
     ┌───────┼───────┐
     ▼       ▼       ▼
 Success   Empty   Error
     │
     └──► LoadingMore (페이지 추가 로딩)
```

### 4.2 상태별 UI

| 상태 | 조건 | UI |
| :--- | :--- | :--- |
| **Loading** | 첫 로딩, 필터 변경 | 전체 화면 스켈레톤 또는 중앙 Progress |
| **Success** | `items.isNotEmpty()` | 그리드 + 결과 요약 |
| **Empty** | `totalCount == 0` | 일러스트 + "조건에 맞는 유기동물이 없습니다" + 필터 초기화 |
| **Error** | API/네트워크 실패 | 메시지 + **다시 시도** 버튼 |
| **LoadingMore** | 다음 페이지 요청 중 | 리스트 하단 Progress |

### 4.3 권장 UiState 모델 (구현 참고)

```kotlin
data class HomeUiState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val animals: List<AbandonedAnimalUiModel> = emptyList(),
    val totalCount: Int = 0,
    val filter: HomeFilterUiState = HomeFilterUiState(),
    val filterSummary: String = "",
    val canLoadMore: Boolean = false,
    val errorMessage: String? = null,
    val layoutMode: HomeLayoutMode = HomeLayoutMode.GRID, // 2열뷰
)
```

> 현재 구현(`totalCount` + `previewNames`)은 API 연동 검증용 임시 UI. MVP 본격 구현 시 위 구조로 확장.

---

## 5. 데이터 · API

### 5.1 UseCase
| UseCase | 용도 |
| :--- | :--- |
| `GetAbandonedAnimalsUseCase` | 목록 조회 (페이지·필터) |
| `GetSidoListUseCase` | (Phase 2) 시도 필터 |
| (추후) 시군구·품종 | 상세 필터 연동 |

### 5.2 API
- **Endpoint:** `abandonmentPublic_v2`
- **Base URL:** `abandonmentPublicService_v2` ([API_LIST.md](../API_LIST.md) 참고)

### 5.3 필터 → Domain → API 매핑

| UI 필터 | Domain (`AbandonedAnimalFilter`) | API Query |
| :--- | :--- | :--- |
| 페이지 | `pageNo`, `numOfRows` | `pageNo`, `numOfRows` |
| 기간 | `beginDate`, `endDate` | `bgnde`, `endde` |
| 축종 칩 | `upKind: UpKind` | `upkind` |
| 상태 칩 | `state: AbandonmentState` | `state` |
| 시도/시군구 | (Phase 2 추가) | `upr_cd`, `org_cd` |

### 5.4 카드 표시 필드 매핑

| UI | Domain (`AbandonedAnimal`) |
| :--- | :--- |
| 사진 | `imageUrls` (`popfile1`, `popfile2`) |
| 품종 | `kindName` (`kindNm`) |
| 상태 | `processState` |
| 보호소 | `shelterName` |
| 상세 이동 키 | `desertionNo` |

---

## 6. 내비게이션

| From | Action | To |
| :--- | :--- | :--- |
| 홈 카드 탭 | `navController.navigate(FindMeRoute.animalDetail(desertionNo))` | 동물 상세 |
| (Phase 2) 마음에 두기 버튼 | Room 저장 + 스낵바 | 마음에 두기 탭 · [FAVORITE.md](./FAVORITE.md) |
| (Phase 2) 보호소명 탭 | 보호소 탭 + (선택) 지역·매칭 | 보호소 · [SHELTER.md](./SHELTER.md) |

- 홈은 **top-level route** → Bottom Bar 유지
- 상세 진입 시 Bottom Bar 숨김 ([PLANNING.md](./PLANNING.md) §6.3)

---

## 7. MVP vs 로드맵

### MVP (1차 출시)
- [ ] 2열 그리드 카드 리스트
- [ ] Coil 이미지 로딩 + placeholder
- [ ] 결과 요약 (총 N마리)
- [ ] 빠른 필터 칩: 축종(개/고양이/전체), 상태(공고중/보호중)
- [ ] 무한 스크롤 (pageNo 증가)
- [ ] Empty / Error / Retry UI
- [ ] 카드 탭 → 동물 상세 이동

### Phase 2
- [ ] Top App Bar + 필터 바텀시트 (시도/시군구/기간)
- [ ] Pull-to-refresh
- [ ] 카드 내 마음에 두기 버튼 · [FAVORITE.md](./FAVORITE.md)
- [ ] 스켈레톤 로딩
- [ ] 최근 본 동물 (마이 연동) · [MY.md](./MY.md)

### Phase 3
- [x] 검색 (품종명·특징 키워드 — API 제약 시 클라이언트 필터)
- [x] 정렬 옵션
- [x] "오늘 등록" / "입양 임박" 큐레이션 섹션

---

## 8. 비기능 요구사항

| 항목 | 기준 |
| :--- | :--- |
| **성능** | 첫 페이지 로딩 목표 3초 이내 (네트워크 환경 가정) |
| **이미지** | Coil disk cache, 썸네일 우선 로딩 |
| **접근성** | 카드 `contentDescription`: "{품종}, {상태}, {보호소}" |
| **오프라인** | Phase 2 Room 캐시 후 마지막 목록 표시 + 오프라인 배너 |
| **API 부하** | `numOfRows` 20 이하, 불필요한 중복 요청 방지 (필터 debounce 300ms) |

---

## 9. 구현 위치 (코드)

| 레이어 | 경로 (예정) |
| :--- | :--- |
| Screen | `presentation/home/HomeScreen.kt` |
| ViewModel | `presentation/home/HomeViewModel.kt` |
| UiState | `presentation/home/HomeUiState.kt` |
| UiModel | `presentation/home/model/AbandonedAnimalUiModel.kt` (신규) |
| Components | `presentation/home/component/AnimalCard.kt` (2열뷰) |
| | `presentation/home/component/AnimalListCard.kt` (노멀뷰) |
| | `presentation/home/component/AnimalCoverCard.kt` (카드뷰) |
| Layout | `domain/model/HomeLayoutMode.kt` · `data/settings/UserPreferencesDataStore.kt` |
| UseCase | `domain/usecase/GetAbandonedAnimalsUseCase.kt` |
| Filter | `domain/model/AbandonedAnimalFilter.kt` |

---

## 10. 현재 구현 현황

| 항목 | 상태 |
| :--- | :--- |
| API 연동 (`GetAbandonedAnimalsUseCase`) | ✅ |
| `HomeViewModel` / `HomeUiState` | ✅ |
| **2열뷰** (`AnimalCard`, 2열 그리드) | ✅ |
| **노멀뷰** (`AnimalListCard`, 1열) | ✅ |
| **카드뷰** (`AnimalCoverCard`, 1열 16:9) | ✅ |
| 보기 형태 전환 (Top App Bar) | ✅ |
| 검색·필터 스크롤 접힘 (`TopAppBarScrollBehavior`) | ✅ |
| 필터 칩·바텀시트 | ✅ |
| 무한 스크롤 | ✅ |
| Pull-to-refresh | ✅ |
| 동물 상세 화면 | ✅ |
| Coil | ✅ |

---

## 11. 오픈 이슈 / 결정 필요

| # | 질문 | 제안 |
| :---: | :--- | :--- |
| 1 | 목록 보기 형태 | ✅ **2열뷰 / 노멀뷰 / 카드뷰** 3종 전환 (§3.6) |
| 2 | 필터 칩 다중 선택 허용? | MVP: 축종 1개 + 상태 0~1개. Phase 2에서 조합 규칙 문서화 |
| 3 | `processState` 문자열 파싱 | Domain에 `AbandonmentStatus` sealed class 도입 검토 |
| 4 | 이미지 URL http | cleartext 허용됨. Phase 2에서 https 리다이렉트 확인 |
| 5 | 종료(입양/반환) 공고 표시 | 기본 **숨김** vs **표시+회색 칩** — MVP는 API 기본 응답 그대로 |

---

*이 문서는 홈 화면 구현 시 기준 문서입니다. 변경 시 PLANNING.md §6.2와 동기화하세요.*
