# 마이 탭 기획 (My)

> **Route:** `my`  
> **관련 문서:** [PLANNING.md](./PLANNING.md) §3.2, §3.3, §6.2 · [HOME.md](./HOME.md) · [DETAIL.md](./DETAIL.md) · [FAVORITE.md](./FAVORITE.md) · [DESIGN_GUIDE.md](../DESIGN_GUIDE.md)

---

## 1. 화면 개요

### 1.1 한 줄 정의
**최근 본 유기동물과 앱 설정·신청 내역을 한곳에 모아, 탐색 탭과 분리된 「내 활동 관리」 허브 탭.**

### 1.2 화면 목표

| 목표 | 설명 |
| :--- | :--- |
| **재탐색** | 우연히 본 공고를 다시 찾기 쉽게 **최근 본 동물** 기록을 제공한다 |
| **관리** | 입양·임보 신청·알림·앱 정보 등 **사용자 설정**을 탐색 UI와 분리한다 |
| **신뢰** | 공공데이터 출처·앱 버전을 투명하게 노출한다 |

### 1.3 마음에 두기 vs 마이

| 구분 | **마음에 두기** (`favorite`) | **마이** (`my`) |
| :--- | :--- | :--- |
| 의도 | 입양 **후보**로 적극 저장 | **열람 이력**·설정·신청 관리 |
| 저장 트리거 | 사용자 ♥ 탭 | 상세 화면 **진입 시 자동** |
| UI | 2열 그리드 전용 탭 | 섹션 + 메뉴 리스트 |
| 데이터 | `favorite_animals` | `recently_viewed_animals` (+ Phase 2 신청) |

두 기능 모두 Room에 저장하되, **역할과 UX를 명확히 분리**합니다.

### 1.4 사용자 시나리오

1. 홈·마음에 두기에서 카드 탭 → **동물 상세** 진입 → Room에 **최근 본** 자동 기록
2. 하단 탭 **마이** 선택 → **최근 본 동물** 가로 스크롤 확인
3. 최근 본 카드 탭 → 다시 **동물 상세**
4. (MVP) **앱 정보** 메뉴 → 버전·데이터 출처 확인
5. (Phase 2) **최근 본 전체 보기** · **기록 삭제**
6. (Phase 2) **입양·임보 신청 내역** (placeholder → 추후 폼 연동)
7. (Phase 3) 알림 설정 · 커뮤니티 · 계정

### 1.5 탭 배치 의도

마이 탭은 **4번(마지막) 탭**입니다 ([PLANNING.md](./PLANNING.md) §6.1).

- **홈 → 보호소 → 마음에 두기 → 마이** 여정의 **관리** 단계입니다.
- 탐색·연락·관심 저장과 달리 **설정·이력** 성격이라 하단 바 **맨 오른쪽**이 관례와 맞습니다.
- 로그인·신청·알림 등 **앱 운영 기능**을 이 탭에 모아 탐색 탭 복잡도를 낮춥니다.

---

## 2. 화면 구조 (와이어프레임)

### 2.1 메인 화면 (MVP)

```
┌─────────────────────────────────────┐
│  마이                                │  ← Top App Bar
├─────────────────────────────────────┤
│  ┌─ 최근 본 동물 ────────── [전체 ›] │  ← Section Header (Phase 2: 전체)
│  │ ┌────┐ ┌────┐ ┌────┐            │
│  │ │사진│ │사진│ │사진│  →         │  ← LazyRow (컴팩트 카드)
│  │ │품종│ │품종│ │품종│            │
│  │ └────┘ └────┘ └────┘            │
│  └──────────────────────────────────│
│                                     │
│  ┌─ 설정 ─────────────────────────┐ │
│  │  📋 앱 정보                  ›  │  ← ListItem
│  │  📡 데이터 출처               ›  │
│  └──────────────────────────────────│
│                                     │
│  FindMe v1.0.0                      │  ← Footer 캡션
├─────────────────────────────────────┤
│  홈  │ 보호소 │ 마음에 두기 │ 마이   │
└─────────────────────────────────────┘
```

### 2.2 최근 본 동물 없음 (Empty)

```
┌─────────────────────────────────────┐
│  마이                                │
├─────────────────────────────────────┤
│  ┌─ 최근 본 동물 ───────────────────┐ │
│  │  아직 본 동물이 없어요            │ │
│  │  상세 화면을 보면 여기에 표시돼요   │ │
│  └──────────────────────────────────│ │
│  ... 설정 메뉴 동일 ...               │
└─────────────────────────────────────┘
```

### 2.3 앱 정보 (Phase 2 — 하위 화면)

Route: `my/about` — Bottom Bar **숨김**

```
┌─────────────────────────────────────┐
│  ←  앱 정보                          │
├─────────────────────────────────────┤
│  FindMe                             │
│  버전 1.0.0 (1)                     │
│                                     │
│  유기동물 입양·임시보호를 돕는 앱      │
│                                     │
│  [ 오픈소스 라이선스 ]  (Phase 3)    │
└─────────────────────────────────────┘
```

### 2.4 최근 본 전체 (`my/recent`)

Route: `my/recent` — Bottom Bar **숨김**

```
┌─────────────────────────────────────┐
│  ←  최근 본 동물    [보기] [전체 삭제] │
├─────────────────────────────────────┤
│  (2열뷰 / 노멀뷰 / 카드뷰 — 홈과 동일) │
│  개별 항목 ✕ 로 삭제                  │
└─────────────────────────────────────┘
```

**목록 보기 형태 (Layout Mode)** — 홈과 동일한 3종, **DataStore 키는 별도** (`recent_layout_mode`)

| 이름 | 코드 | 컴포넌트 | 최근 본 전용 |
| :--- | :--- | :--- | :--- |
| **2열뷰** | `GRID` | `AnimalCard` | ✕ 삭제 버튼 |
| **노멀뷰** | `LIST` | `AnimalListCard` | ✕ 삭제 + **본 시각** (`viewedAtLabel`) |
| **카드뷰** | `COVER` | `AnimalCoverCard` | ✕ 삭제 버튼 |

Top App Bar **보기 전환** 버튼: **2열뷰 → 노멀뷰 → 카드뷰 → 2열뷰** (홈과 동일 순환, 설정값은 독립 저장)

**구현 위치**
| 항목 | 경로 |
| :--- | :--- |
| Enum | `domain/model/HomeLayoutMode.kt` (홈과 공유) |
| 저장 | `UserPreferencesDataStore` key: `recent_layout_mode` |
| UiState | `MyRecentUiState.layoutMode` |
| 전환 | `MyRecentViewModel.toggleLayoutMode()` → `SetRecentLayoutModeUseCase` |
| UI | `MyRecentScreen` · `MyRecentAnimalContent` |

**레이아웃 원칙** ([DESIGN_GUIDE.md](../DESIGN_GUIDE.md) 준수)

- 화면 마진: `16.dp`
- 섹션 간격: `24.dp`
- 최근 본 가로 카드: 너비 `120.dp`, 사진 1:1, 모서리 `12.dp`
- 설정 메뉴: Material 3 `ListItem`, 구분선 `HorizontalDivider`

---

## 3. UI 구성 요소

### 3.1 Top App Bar

| 요소 | MVP | Phase 2 |
| :--- | :---: | :---: |
| 타이틀 | `마이` (`nav_my`) | 동일 |
| 프로필/로그인 | 🔲 | Phase 3 계정 |

### 3.2 최근 본 동물 섹션

| 항목 | 스펙 |
| :--- | :--- |
| 데이터 | Room `recently_viewed_animals`, `viewedAt DESC` |
| MVP 표시 | 최대 **10건** 가로 `LazyRow` |
| 카드 | 사진 · 품종 · (선택) 상태 칩 |
| 탭 | `animal/{desertionNo}` |
| Empty | §2.2 안내 문구 (섹션 내부) |
| 전체 보기 | 🔲 Phase 2 → `my/recent` |
| 기록 삭제 | 🔲 Phase 2 (전체 / 개별) |

**자동 기록 규칙**

- **동물 상세** 화면 진입 시 1회 기록 (`AnimalDetailViewModel` 또는 UseCase)
- 동일 `desertionNo` 재방문 → `viewedAt`만 갱신 (맨 앞으로)
- 최대 **30건** 유지, 초과 시 **가장 오래된 항목** 삭제

### 3.3 설정 메뉴 (MVP)

| 메뉴 | MVP | Phase 2 | 설명 |
| :--- | :---: | :---: | :--- |
| **앱 정보** | ✅ | `my/about` 화면 | 버전명, 버전코드 |
| **데이터 출처** | ✅ | Bottom Sheet 또는 Web | 공공데이터포털 안내 |
| 입양·임보 신청 내역 | 🔲 | ✅ placeholder | §3.4 |
| 알림 설정 | 🔲 | ✅ | Phase 2 |
| 마음에 둔 동물 바로가기 | 🔲 | ✅ | `favorite` 탭 shortcut |
| 커뮤니티·실종신고 | 🔲 | 🔲 | Phase 3 · [PLANNING.md](./PLANNING.md) §3.3 |

### 3.4 입양·임보 신청 내역 (Phase 2 — placeholder)

MVP 서버·폼 없음 → **「준비 중」** Empty 화면만 제공.

| 상태 | UI |
| :--- | :--- |
| 신청 0건 | `아직 신청 내역이 없어요` + 홈/마음에 두기 CTA |
| (Phase 3) | 신청서 목록 · 상태(접수/검토/완료) |

Route: `my/applications` (Phase 2)

### 3.5 Footer

- 앱 하단: `FindMe v{versionName}` (BuildConfig)
- 보조 색 `#757575`, Body 2

---

## 4. 상태 (UiState)

```kotlin
data class MyUiState(
    val recentAnimals: List<RecentAnimalUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val appVersionName: String = "",
    val appVersionCode: Int = 0,
)

data class RecentAnimalUiModel(
    val desertionNo: String,
    val kindName: String,
    val processState: String,
    val shelterName: String,
    val imageUrls: List<String>,
    val statusType: ProcessStateType,
    val viewedAt: Long,
    val viewedAtLabel: String, // "2시간 전" — Presentation mapper
)

data class MyRecentUiState(
    val recentAnimals: List<RecentAnimalUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val showClearConfirmDialog: Boolean = false,
    val layoutMode: HomeLayoutMode = HomeLayoutMode.GRID, // DataStore recent_layout_mode
)
```

### 4.1 상태 다이어그램

```
[탭 진입]
   │
   ▼
[Loading] ── Room Flow (recently viewed)
   │
   ├─ items = 0 → [Content] Empty 섹션 + 설정 메뉴
   └─ items > 0 → [Content] LazyRow + 설정 메뉴
```

---

## 5. 데이터 · 저장소

### 5.1 Room Entity (예시)

```kotlin
@Entity(tableName = "recently_viewed_animals")
data class RecentlyViewedEntity(
    @PrimaryKey val desertionNo: String,
    val kindName: String,
    val processState: String,
    val shelterName: String,
    val imageUrl1: String?,
    val imageUrl2: String?,
    val viewedAt: Long,
)
```

> **마음에 두기**(`favorite_animals`)와 스냅샷 필드는 유사하지만 **`viewedAt`**·**자동 기록**·**최대 30건 FIFO** 정책이 다릅니다.  
> Entity 통합보다 **테이블 분리**를 권장합니다 (의미·쿼리·보존 정책 분리).

### 5.2 Domain · Repository · UseCase

| 컴포넌트 | 역할 |
| :--- | :--- |
| `RecentlyViewedRepository` | observe / insert-or-update / clear / trim |
| `ObserveRecentlyViewedUseCase` | 마이 탭 Flow |
| `RecordRecentlyViewedUseCase` | 상세 진입 시 호출 |
| `ClearRecentlyViewedUseCase` | (Phase 2) 전체 삭제 |
| `RefreshRecentSnapshotsUseCase` | (선택) API 상태 갱신 — [FAVORITE.md](./FAVORITE.md)와 동일 패턴 |

**기록 시점 (권장)**

```kotlin
// AnimalDetailViewModel.init — animal 로드 성공 후
recordRecentlyViewedUseCase(desertionNo)
```

캐시/API에서 `AbandonedAnimal` 스냅샷을 가져와 저장 ([FAVORITE.md](./FAVORITE.md) `AddFavoriteUseCase` 패턴 재사용).

### 5.3 카드 표시 필드 매핑

| UI | Room / Domain |
| :--- | :--- |
| 사진 | `imageUrl1`, `imageUrl2` |
| 품종 | `kindName` |
| 상태 | `processState` |
| 보호소 | `shelterName` (전체 목록) |
| 시간 | `viewedAt` → 상대 시간 문자열 |
| 상세 키 | `desertionNo` |

### 5.4 기존 코드 활용

| 레이어 | 상태 | 경로 |
| :--- | :--- | :--- |
| Route | ✅ | `FindMeRoute.MY` |
| NavHost | 🔲 Placeholder | `FindMeNavHost.kt` |
| Room (recent) | 🔲 | `data/local/recent/` |
| `FindMeDatabase` | ✅ | v2 migration 필요 |
| `AnimalCard` / compact variant | 🔲 | `RecentAnimalCard` 신규 |
| 상세 기록 연동 | 🔲 | `AnimalDetailViewModel` |
| `FavoriteRepository` 패턴 | ✅ | Room·Mapper 참고 |

---

## 6. 내비게이션

| From | Action | To |
| :--- | :--- | :--- |
| Bottom Bar | 마이 탭 | `my` (top-level) |
| 최근 본 카드 | `animalDetail(desertionNo)` | 동물 상세 |
| 앱 정보 | `navigate(my/about)` | 앱 정보 (Phase 2) |
| 데이터 출처 | Bottom Sheet / 브라우저 | 외부 링크 |
| (Phase 2) 전체 보기 | `my/recent` | 최근 본 전체 |
| (Phase 2) 신청 내역 | `my/applications` | placeholder |
| (Phase 2) 마음에 두기 shortcut | `favorite` 탭 | 탭 전환 |

**상세 진입 시** Room 기록 → 마이 탭 최근 본 갱신 (Flow).

`FindMeRoute` 확장 예:

```kotlin
const val MY = "my"
const val MY_ABOUT = "my/about"
const val MY_RECENT = "my/recent"
const val MY_APPLICATIONS = "my/applications"
```

---

## 7. MVP vs 로드맵

### MVP (1차 출시)

**저장소 · 도메인**
- [x] `RecentlyViewedEntity` + DAO + DB migration (v1→v2)
- [x] `RecentlyViewedRepository` + UseCase (observe, record)
- [x] `AnimalDetailViewModel` 진입 시 자동 기록

**마이 탭**
- [x] `MyScreen` + `MyViewModel` + `MyUiState`
- [x] 최근 본 동물 `LazyRow` (최대 10건 미리보기)
- [x] Empty 섹션 UI
- [x] 설정: **앱 정보** (Dialog), **데이터 출처** (브라우저)
- [x] Footer 버전 표시

### Phase 2

- [x] `my/recent` 하위 화면 (전체 목록)
- [x] 최근 본 **전체 삭제** / 개별 삭제
- [x] 최근 본 **2열뷰·노멀뷰·카드뷰** (DataStore `recent_layout_mode`, 홈과 독립)
- [ ] `my/about` 하위 화면
- [ ] 입양·임보 신청 내역 placeholder
- [ ] 알림 설정 (토글 UI, 실제 푸시는 Phase 3)
- [ ] 마음에 두기 탭 shortcut
- [ ] API 스냅샷 refresh (종료 공고 표시)

### Phase 3

- [ ] 입양·임보 **신청서 작성·제출** (마이 연동)
- [ ] 푸시 알림 (마음에 둔 동물 상태 변경)
- [ ] 계정·로그인
- [ ] 커뮤니티·실종신고 메뉴
- [ ] 오픈소스 라이선스 화면

---

## 8. 비기능 요구사항

| 항목 | 기준 |
| :--- | :--- |
| **성능** | Room Flow → UI 100ms 이내 |
| **용량** | 최대 30건, URL만 저장 (이미지 바이너리 X) |
| **개인정보** | 최근 본 기록 **기기 로컬만**, MVP 클라우드 동기화 없음 |
| **자동 기록** | 상세 진입 시 **사용자 추가 액션 없음** — 설정에서 끄기 (Phase 2) |
| **접근성** | 최근 본 카드 CD: `{품종}, {상태}, {n}시간 전에 본 동물` |

---

## 9. 구현 위치 (코드)

| 레이어 | 경로 (예정) |
| :--- | :--- |
| Entity / DAO | `data/local/recent/RecentlyViewedEntity.kt`, `RecentlyViewedDao.kt` |
| Database | `FindMeDatabase` v2 + `Migration` |
| Repository | `data/repository/RecentlyViewedRepositoryImpl.kt` |
| Domain | `domain/repository/RecentlyViewedRepository.kt` |
| UseCase | `domain/usecase/RecordRecentlyViewedUseCase.kt` 등 |
| Screen | `presentation/my/MyScreen.kt` |
| ViewModel | `presentation/my/MyViewModel.kt` |
| Component | `presentation/my/component/RecentAnimalRow.kt`, `MyMenuSection.kt` |
| Detail 연동 | `AnimalDetailViewModel.kt` |
| Nav | `FindMeNavHost.kt`, `FindMeRoute.kt` |
| Strings | `presentation/.../values/strings.xml` |

### 9.1 DB Migration

`favorite_animals` 추가로 DB v1 존재 시:

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // CREATE recently_viewed_animals ...
    }
}
```

신규 설치: `@Database(version = 2, entities = [Favorite..., RecentlyViewed...])`

---

## 10. 현재 구현 현황

| 항목 | 상태 |
| :--- | :--- |
| Route 등록 (`my`) | ✅ |
| `MyScreen` | ✅ |
| Room (recently viewed) | ✅ |
| 상세 자동 기록 | ✅ |
| 최근 본 UI | ✅ |
| 설정 메뉴 | ✅ |
| 신청 내역 | 🔲 |
| 문자열 | ✅ |

---

## 11. 오픈 이슈 / 결정 필요

| # | 질문 | 제안 |
| :---: | :--- | :--- |
| 1 | 최 recent UI | MVP: **가로 LazyRow** (10건) · Phase 2: 세로 전체 목록 |
| 2 | 기록 시점 | **상세 ViewModel init** (로드 성공 후) — 스플래시·홈만 본 경우 제외 |
| 3 | 최대 보관 건수 | **30건** FIFO |
| 4 | 마음에 두기와 중복 표시 | **허용** — favorite=의도, recent=이력 |
| 5 | 앱 정보 MVP | **인라인 Footer + 메뉴 1탭**으로 버전·출처 표시 (별도 화면 Phase 2) |
| 6 | 데이터 출처 링크 | `https://www.data.go.kr/` 공공데이터포털 + 서비스명 캡션 |
| 7 | 상대 시간 표시 | `viewedAt` → `2시간 전` / `어제` (Phase 2 전체 목록) |
| 8 | recent 스냅샷 refresh | MVP **스냅샷만** · Phase 2 API refresh (favorite와 동일) |
| 9 | 최근 본 끄기 | Phase 2 설정 토글 + 기존 데이터 삭제 확인 |
| 10 | 신청 내역 MVP | **메뉴 숨김** vs **「준비 중」placeholder** — **Phase 2 placeholder** 권장 |

---

## 12. 구현 순서 (권장)

1. **Data:** `RecentlyViewedEntity` + DAO + DB v2 migration
2. **Domain:** Repository + `RecordRecentlyViewed` / `ObserveRecentlyViewed` UseCase
3. **Detail:** `AnimalDetailViewModel`에 기록 호출
4. **Presentation:** `MyScreen` — LazyRow + 설정 메뉴 + Footer
5. **Nav:** Placeholder → `MyScreen`
6. **Phase 2:** `my/recent`, `my/about`, 삭제·신청 placeholder

---

*이 문서는 마이 탭 구현 시 기준 문서입니다. 변경 시 [PLANNING.md](./PLANNING.md) §6.2, [DETAIL.md](./DETAIL.md) §6과 동기화하세요.*
