# 마음에 두기 탭 기획 (Favorite)

> **Route:** `favorite`  
> **관련 문서:** [PLANNING.md](./PLANNING.md) §3.2, §6.2 · [HOME.md](./HOME.md) · [DETAIL.md](./DETAIL.md) · [DESIGN_GUIDE.md](../DESIGN_GUIDE.md) · [PUBLIC_DATA_API.md](../PUBLIC_DATA_API.md)

---

## 1. 화면 개요

### 1.1 한 줄 정의
**입양 후보로 마음에 둔 유기동물을 로컬에 저장·모아 보고, 최신 공고 상태를 확인하며 상세·보호소 문의로 이어지는 관심 목록 탭.**

### 1.2 화면 목표

| 목표 | 설명 |
| :--- | :--- |
| **저장** | 홈·상세에서 고른 동물을 기기에 안전하게 보관해 재방문 탐색을 돕는다 |
| **비교** | 여러 후보를 한 화면에서 나란히 비교·우선순위를 정한다 |
| **연결** | 관심 카드 → 동물 상세 → 보호소 전화로 입양 문의 흐름을 유지한다 |

### 1.3 사용자 시나리오

1. 홈 또는 동물 상세에서 **마음에 두기** 탭 → Room에 `desertionNo` 저장
2. 하단 탭 **마음에 두기** 선택 → 저장한 동물 **2열 카드 그리드** 확인
3. (백그라운드) 각 공고의 **최신 상태** API 갱신 (공고중 → 종료 등)
4. 카드 탭 → **동물 상세** (`animal/{desertionNo}`)
5. 카드의 ♥ 아이콘 또는 상세에서 **마음에서 빼기** → 목록에서 제거
6. (Phase 2) 공고 종료 동물에 **상태 변경 배지** · 정리 안내
7. (Phase 3) 상태 변경 **푸시 알림**

### 1.4 탭 배치 의도

마음에 두기 탭은 **3번 탭**에 둡니다 ([PLANNING.md](./PLANNING.md) §6.1).

- **홈(탐색) → 보호소(연락) → 마음에 두기(관심) → 마이(관리)** 여정의 **관심 저장** 단계입니다.
- 보호소 탭이 MVP 가치를 먼저 제공하므로, 마음에 두기는 **Room + 추가/제거 UX**가 갖춰진 뒤 본격 활용됩니다.
- UI 용어는 **「찜」 대신 「마음에 두기 / 마음에서 빼기 / 마음에 둔 동물」**을 사용합니다. (내부 Route 키 `favorite`는 유지)

---

## 2. 화면 구조 (와이어프레임)

### 2.1 목록 화면 (MVP)

```
┌─────────────────────────────────────┐
│  마음에 둔 동물                       │  ← Top App Bar
├─────────────────────────────────────┤
│  마음에 둔 동물 3마리 · 최근 추가순    │  ← 결과 요약
├─────────────────────────────────────┤
│ ┌──────────┐  ┌──────────┐         │
│ │ ♥ [사진] │  │ ♥ [사진] │         │  ← 홈과 동일 2열 AnimalCard
│ │ 품종 🟢  │  │ 품종 🔵  │         │     + 마음에서 빼기(♥ filled)
│ │ 보호소명 │  │ 보호소명 │         │
│ └──────────┘  └──────────┘         │
│         ... (스크롤)                 │
├─────────────────────────────────────┤
│  홈  │ 보호소 │ 마음에 두기 │ 마이   │  ← Bottom Nav (유지)
└─────────────────────────────────────┘
```

### 2.2 빈 목록 (Empty)

```
┌─────────────────────────────────────┐
│  마음에 둔 동물                       │
├─────────────────────────────────────┤
│                                     │
│           ♡                         │
│   아직 마음에 둔 동물이 없어요         │
│   홈에서 마음에 드는 아이를            │
│   저장해 보세요                      │
│                                     │
│        [ 유기동물 보러 가기 ]         │  ← 홈 탭 전환
│                                     │
└─────────────────────────────────────┘
```

### 2.3 마음에 두기 추가 (홈·상세 — MVP 필수 연동)

**동물 상세 Top Bar (권장 1차 진입점)**

```
┌─────────────────────────────────────┐
│  ←  [개] 믹스견              ♥       │  ← filled = 저장됨
```

**홈 카드 (Phase 2)**

```
┌──────────┐
│ ♡ [사진] │  ← 카드 우상단 아웃라인 ♥
│ 품종     │
```

**추가 시 피드백**

- Snackbar: `마음에 둔 목록에 추가했습니다` (기존 `favorite_added_message`)
- (Phase 2) Snackbar 액션: `목록 보기` → `favorite` 탭 전환

**레이아웃 원칙** ([DESIGN_GUIDE.md](../DESIGN_GUIDE.md) 준수)

- 홈 탭과 **동일한 2열 그리드·카드 스펙** (`AnimalCard` 재사용)
- ♥ 아이콘: Primary `#FFAB91` / filled 시 `#FF8A65`
- 화면 마진 `16.dp`, 카드 간격 `12.dp`, 모서리 `16.dp`

---

## 3. UI 구성 요소

### 3.1 Top App Bar

| 요소 | MVP | Phase 2 |
| :--- | :---: | :---: |
| 타이틀 | `마음에 둔 동물` (`favorite_screen_title`) | 동일 |
| 정렬 | 🔲 (고정: 최근 추가순) | 메뉴: 최근 추가순 / 공고 상태순 |
| 편집 | 🔲 | 다중 선택 삭제 |

### 3.2 결과 요약

| 상태 | 문구 예시 |
| :--- | :--- |
| 1마리 이상 | `마음에 둔 동물 N마리 · 최근 추가순` |
| 갱신 중 | 상단 `LinearProgressIndicator` (선택) |
| 종료 공고 포함 | (Phase 2) `종료된 공고 M마리` 보조 문구 |

### 3.3 FavoriteAnimalCard

홈 `AnimalCard`를 **확장**하거나 동일 Composable에 옵션 파라미터를 추가합니다.

| UI 요소 | 데이터 | 비고 |
| :--- | :--- | :--- |
| 사진 | `imageUrls.first()` | Coil, placeholder `ic_paw` |
| 품종 + 상태 칩 | `kindName`, `processState` | `AnimalKindTitleRow` 재사용 |
| 보호소명 | `shelterName` | Body Small |
| ♥ 버튼 | `isFavorite = true` | 탭 → **마음에서 빼기** (확인 없이 즉시, Phase 2에서 Undo) |
| 카드 본문 탭 | `desertionNo` | 동물 상세 이동 |

**접근성:** `contentDescription = "{품종}, {상태}, {보호소}, 마음에 둔 동물"`

### 3.4 Empty / Error

| 상태 | UI |
| :--- | :--- |
| 저장 0건 | §2.2 + CTA **유기동물 보러 가기** → `home` 탭 |
| Room 로드 실패 | 에러 문구 + **다시 시도** (드물게) |
| API 갱신 실패 | Room 스냅샷 그대로 표시 + (Phase 2) 오프라인 배너 |

---

## 4. 상태 (UiState)

```kotlin
data class FavoriteUiState(
    val favorites: List<FavoriteAnimalUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val sortOrder: FavoriteSortOrder = FavoriteSortOrder.SAVED_AT_DESC,
    val errorMessage: String? = null,
    val endedCount: Int = 0,
)

enum class FavoriteSortOrder {
    SAVED_AT_DESC,   // MVP 기본
    STATUS_NOTICE_FIRST, // Phase 2
}

data class FavoriteAnimalUiModel(
    val desertionNo: String,
    val kindName: String,
    val processState: String,
    val shelterName: String,
    val imageUrls: List<String>,
    val statusType: ProcessStateType,
    val savedAt: Long,
    val isRefreshing: Boolean = false,
)
```

### 4.1 상태 다이어그램

```
[탭 진입]
   │
   ▼
[Loading] ── Room Flow observe
   │
   ├─ items = 0 → [Empty]
   ├─ items > 0 → [Content] 그리드
   │       │
   │       └─ (백그라운드) desertionNo별 API refresh
   │              ├─ 성공 → Room 스냅샷·상태 갱신
   │              └─ 실패 → 기존 스냅샷 유지
   └─ Room 오류 → [Error]
```

---

## 5. 데이터 · 저장소

### 5.1 저장 전략 (Room)

마음에 두기는 **공공 API에 사용자별 저장 API가 없으므로** 기기 로컬 Room이 Source of Truth입니다.

| 항목 | 결정 |
| :--- | :--- |
| Primary Key | `desertionNo` (유기번호) |
| 정렬 | `savedAt` (Epoch ms, 추가 시각) |
| 스냅샷 | 카드 표시용 필드 Room에 **비정규화 저장** (오프라인·빠른 목록) |
| 갱신 | 탭 진입·Pull-to-refresh 시 API로 `processState` 등 최신화 |

### 5.2 Room Entity (예시)

```kotlin
@Entity(tableName = "favorite_animals")
data class FavoriteAnimalEntity(
    @PrimaryKey val desertionNo: String,
    val kindName: String,
    val processState: String,
    val shelterName: String,
    val shelterTel: String,
    val imageUrl1: String?,
    val imageUrl2: String?,
    val savedAt: Long,
)
```

### 5.3 Domain Model

```kotlin
data class FavoriteAnimal(
    val desertionNo: String,
    val savedAt: Long,
    val animal: AbandonedAnimal, // 스냅샷
)
```

### 5.4 Repository · UseCase

| 컴포넌트 | 역할 |
| :--- | :--- |
| `FavoriteRepository` | insert / delete / observe Flow |
| `ObserveFavoritesUseCase` | 목록 Flow (마음에 두기 탭) |
| `AddFavoriteUseCase` | `AbandonedAnimal` 저장 |
| `RemoveFavoriteUseCase` | `desertionNo` 삭제 |
| `IsFavoriteUseCase` | 상세 TopBar ♥ 상태 |
| `ToggleFavoriteUseCase` | (선택) 추가/제거 토글 |
| `RefreshFavoriteAnimalsUseCase` | (Phase 2) 저장 목록 API 일괄 갱신 |

**API 연동 (상태 갱신)**

- 단건: `GetAbandonedAnimalDetailUseCase(desertionNo)` — `desertion_no` 쿼리 재조회 ✅ 기존 구현
- Not Found → (Phase 2) 카드에 **「공고 종료·삭제됨」** 표시, 자동 삭제는 하지 않음 (사용자 선택)

### 5.5 카드 표시 필드 매핑

| UI | Room / Domain |
| :--- | :--- |
| 사진 | `imageUrl1`, `imageUrl2` → `imageUrls` |
| 품종 | `kindName` |
| 상태 | `processState` |
| 보호소 | `shelterName` |
| 상세 키 | `desertionNo` |
| 정렬 | `savedAt` |

### 5.6 기존 코드 활용

| 레이어 | 상태 | 경로 |
| :--- | :--- | :--- |
| Route | ✅ | `FindMeRoute.FAVORITE` |
| NavHost | 🔲 Placeholder | `FindMeNavHost.kt` |
| Room | 🔲 | `data/local/` 신규 |
| `AnimalCard` / `AnimalKindTitleRow` | ✅ | `presentation/home/component/` |
| `GetAbandonedAnimalDetailUseCase` | ✅ | 상태 갱신 |
| `AbandonedAnimalMemoryCache` | ✅ | 상세·목록 캐시 보조 |
| 문자열 | ✅ (일부) | `favorite_*`, `nav_favorite` |

---

## 6. 내비게이션

| From | Action | To |
| :--- | :--- | :--- |
| Bottom Bar | 마음에 두기 탭 | `favorite` (top-level) |
| Empty CTA | 유기동물 보러 가기 | `home` 탭 |
| 마음에 둔 카드 | `FindMeRoute.animalDetail(desertionNo)` | 동물 상세 |
| 상세 ♥ | `AddFavoriteUseCase` / `RemoveFavoriteUseCase` | 현재 화면 + Snackbar |
| (Phase 2) 홈 카드 ♥ | 동일 | Snackbar + (선택) `favorite` 탭 |
| (Phase 2) Snackbar 「목록 보기」 | 탭 전환 | `favorite` |

- 마음에 두기 탭은 **top-level route** → Bottom Bar 유지
- 상세 진입 시 Bottom Bar 숨김 ([PLANNING.md](./PLANNING.md) §6.3)

---

## 7. MVP vs 로드맵

### MVP (1차 출시)

**저장소 · 도메인**
- [x] Room `FavoriteAnimalEntity` + DAO + Database
- [x] `FavoriteRepository` / `FavoriteRepositoryImpl`
- [x] `ObserveFavoritesUseCase`, `AddFavoriteUseCase`, `RemoveFavoriteUseCase`, `ToggleFavoriteUseCase`

**마음에 두기 탭**
- [x] `FavoriteScreen` + `FavoriteViewModel` + `FavoriteUiState`
- [x] 2열 그리드 (`AnimalCard` + ♥ 제거 버튼)
- [x] Empty UI + 홈 탭 CTA
- [x] 카드 탭 → 동물 상세
- [x] ♥ 탭 → 마음에서 빼기

**추가 진입점 (탭만으로는 저장 불가 → 최소 1곳 필수)**
- [x] **동물 상세 Top Bar ♥** 토글 + Snackbar

**갱신**
- [x] 탭 진입 시 Room Flow 즉시 표시
- [x] 탭 진입 시 `desertionNo` 순차 API refresh

### Phase 2

- [ ] 홈 `AnimalCard` ♡ 추가 버튼
- [ ] Pull-to-refresh (전체 상태 갱신)
- [ ] Snackbar Undo (마음에서 빼기 취소)
- [ ] 정렬: 공고 상태순
- [ ] 종료 공고 시각적 구분 (회색 칩·딤 처리)
- [ ] 「종료된 공고 정리하기」 일괄 제거
- [ ] `ToggleFavoriteUseCase` 공통화

### Phase 3

- [ ] 공고 상태 변경 푸시 알림 (WorkManager)
- [ ] 입양·임시보호 신청 바로가기 (마이 연동)
- [ ] 마음에 둔 동물 **메모** (입양 상담 메모)
- [ ] 데이터 내보내기 / 계정 연동 (서버 백업 — 장기)

---

## 8. 비기능 요구사항

| 항목 | 기준 |
| :--- | :--- |
| **성능** | Room Flow → UI 100ms 이내 (로컬 우선) |
| **용량** | 스냅샷 필드만 저장, 이미지는 URL만 (바이너리 미저장) |
| **오프라인** | 네트워크 없어도 **마지막 스냅샷** 목록 표시 |
| **동기화** | API refresh 실패 시 기존 데이터 유지, 사용자에게 강제 삭제 없음 |
| **접근성** | ♥ 버튼 CD: `마음에서 빼기` / `마음에 두기` |
| **개인정보** | Room 데이터는 **기기 내부만**, MVP에 클라우드 동기화 없음 |

---

## 9. 구현 위치 (코드)

| 레이어 | 경로 (예정) |
| :--- | :--- |
| Entity / DAO | `data/local/favorite/FavoriteAnimalEntity.kt`, `FavoriteAnimalDao.kt` |
| Database | `data/local/FindMeDatabase.kt` |
| Repository | `data/repository/FavoriteRepositoryImpl.kt` |
| Domain Repo | `domain/repository/FavoriteRepository.kt` |
| UseCase | `domain/usecase/ObserveFavoritesUseCase.kt` 등 |
| Screen | `presentation/favorite/FavoriteScreen.kt` |
| ViewModel | `presentation/favorite/FavoriteViewModel.kt` |
| UiState | `presentation/favorite/FavoriteUiState.kt` |
| UiModel | `presentation/favorite/model/FavoriteAnimalUiModel.kt` |
| Component | `presentation/favorite/component/FavoriteAnimalCard.kt` (또는 `AnimalCard` 확장) |
| Detail 연동 | `AnimalDetailScreen.kt`, `AnimalDetailViewModel.kt` |
| Nav | `FindMeNavHost.kt` |
| Strings | `presentation/.../values/strings.xml` |
| Gradle | `data/build.gradle.kts` — Room + KSP |

### 9.1 `AnimalCard` 공유 제안

| 방안 | 설명 |
| :--- | :--- |
| **A. 파라미터 확장 (권장)** | `AnimalCard`에 `showFavoriteAction`, `isFavorite`, `onFavoriteClick` optional 추가 |
| **B. 래퍼** | `FavoriteAnimalCard`가 `AnimalCard` + ♥ 오버레이 |

MVP는 **A**로 홈·마음에 두기 카드 UI를 통일합니다.

---

## 10. 현재 구현 현황

| 항목 | 상태 |
| :--- | :--- |
| Route 등록 (`favorite`) | ✅ |
| `FavoriteScreen` | ✅ |
| Room / Repository | ✅ |
| UseCase | ✅ |
| 마음에 두기 탭 UI | ✅ |
| 상세 ♥ 토글 | ✅ |
| 홈 카드 ♥ | 🔲 |
| 문자열 (기본) | ✅ |

---

## 11. 오픈 이슈 / 결정 필요

| # | 질문 | 제안 |
| :---: | :--- | :--- |
| 1 | 추가 진입점 우선순위 | MVP: **상세 TopBar ♥만** → Phase 2: 홈 카드 |
| 2 | 제거 확인 다이얼로그 | **없음** (즉시 제거) · Phase 2 Snackbar Undo |
| 3 | 공고 종료 시 자동 삭제 | **자동 삭제 안 함** · Phase 2 「정리하기」 제공 |
| 4 | API Not Found 처리 | 스냅샷 유지 + 「공고를 찾을 수 없음」 배지 |
| 5 | 그리드 2열 vs 1열 | **2열** — 홈과 동일, 비교 탐색에 유리 |
| 6 | ♥ 위치 | 카드 **우상단** (사진 위) · 상세 **TopBar trailing** |
| 7 | Room 스냅샷 범위 | MVP: 카드+상세 필수 필드만 · JSON 전체 저장은 Phase 3 |
| 8 | refresh 타이밍 | 탭 진입 1회 + Pull-to-refresh (Phase 2) · 스크롤마다 X |
| 9 | 최대 저장 개수 | MVP **제한 없음** · Phase 3에서 100건 soft limit 검토 |
| 10 | Empty CTA | **홈 탭 전환** (보호소 X — 동물 탐색이 목적) |

---

## 12. 구현 순서 (권장)

1. **Data:** Room Entity/DAO/Database + Hilt Module
2. **Domain:** `FavoriteRepository` + Add/Remove/Observe UseCase
3. **Presentation Core:** `FavoriteViewModel`, UiState, mapper
4. **Favorite Tab UI:** Screen + Empty + 그리드
5. **Detail 연동:** TopBar ♥ + `IsFavorite` / Toggle
6. **Refresh:** 탭 진입 API 갱신 (선택)
7. **Phase 2:** 홈 카드 ♥, Undo, Pull-to-refresh

---

*이 문서는 마음에 두기 탭·기능 구현 시 기준 문서입니다. 변경 시 [PLANNING.md](./PLANNING.md) §6.2, [DETAIL.md](./DETAIL.md) §7과 동기화하세요.*
