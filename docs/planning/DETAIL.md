# 동물 상세 화면 기획 (Animal Detail)

> **Route:** `animal/{desertionNo}`  
> **관련 문서:** [PLANNING.md](./PLANNING.md) §6.3 · [HOME.md](./HOME.md) · [DESIGN_GUIDE.md](../DESIGN_GUIDE.md) · [PUBLIC_DATA_API.md](../PUBLIC_DATA_API.md)

---

## 1. 화면 개요

### 1.1 한 줄 정의
**홈·찜에서 선택한 유기동물 한 마리의 사진·프로필·특징·보호소 정보를 확인하고, 입양 문의(전화)로 이어지는 상세 화면.**

### 1.2 화면 목표

| 목표 | 설명 |
| :--- | :--- |
| **정보 전달** | 카드에서 보지 못한 세부 정보(성별, 중성화, 특징, 발견 장소 등)를 한곳에 모은다 |
| **신뢰 형성** | 공공데이터 기반 공고 상태·보호소 연락처를 명확히 보여준다 |
| **행동 유도** | 보호소 전화·찜하기 등 다음 행동으로 자연스럽게 연결한다 |

### 1.3 사용자 시나리오

1. 홈 그리드에서 카드 탭 → **동물 상세** 진입 (`desertionNo` 전달)
2. 사진을 넘기며 확인하고, 품종·나이·특징을 읽는다
3. (MVP) **보호소에 전화** 버튼으로 입양·방문 문의
4. (Phase 2) **찜** 버튼으로 관심 목록 저장
5. (Phase 2) 보호소명 탭 → 보호소 탭/지도로 이동
6. 뒤로가기 → 홈 스크롤·필터 상태 유지

### 1.4 네비게이션 규칙

| 항목 | 규칙 |
| :--- | :--- |
| Bottom Bar | **숨김** (탭 바깥 push 화면) |
| 진입 경로 | 홈 카드 탭 · (Phase 2) 찜 탭 · (Phase 2) 마이 > 최근 본 동물 |
| 식별 키 | `desertionNo` (유기번호, 공고 고유 ID) |
| 뒤로가기 | `navController.popBackStack()` |

---

## 2. 화면 구조 (와이어프레임)

```
┌─────────────────────────────────────┐
│  ←  [개] 믹스견              ♡  ⋮   │  ← Top App Bar
├─────────────────────────────────────┤
│                                     │
│         [  사진 HorizontalPager  ]  │  ← 4:3 또는 1:1, 인디케이터
│              ● ○                    │
├─────────────────────────────────────┤
│  🟢 공고중                          │  ← 상태 칩 (DESIGN_GUIDE 상태색)
│                                     │
│  ┌─ 기본 정보 ──────────────────┐  │
│  │ 품종    [개] 믹스견           │  │
│  │ 성별    암                    │  │
│  │ 중성화  O                     │  │
│  │ 나이    2024(년생)            │  │
│  │ 체중    5(Kg)                 │  │
│  │ 색상    검정                  │  │
│  └───────────────────────────────┘  │
│                                     │
│  ┌─ 특징 ───────────────────────┐  │
│  │ 건강하고 사람을 잘 따름...    │  │  ← specialMark, 전체 표시
│  └───────────────────────────────┘  │
│                                     │
│  ┌─ 발견 정보 ──────────────────┐  │
│  │ 장소    서울특별시 ○○구 ...   │  │  ← happenPlace
│  └───────────────────────────────┘  │
│                                     │
│  ┌─ 보호소 ─────────────────────┐  │
│  │ ○○동물보호센터                │  │  ← careNm
│  │ 📞 02-000-0000               │  │  ← careTel
│  │ (Phase 2) 주소 · 길찾기       │  │
│  └───────────────────────────────┘  │
│                                     │
│  유기번호 2024-000000000            │  ← desertionNo (캡션)
├─────────────────────────────────────┤
│  [  ♡ 찜하기  ]  [ 📞 보호소 전화 ] │  ← Sticky Bottom Bar (Phase 2 / MVP)
└─────────────────────────────────────┘
```

**레이아웃 원칙** ([DESIGN_GUIDE.md](../DESIGN_GUIDE.md) 준수)

- 화면 마진: `16.dp`
- 섹션 카드 모서리: `16.dp`
- 섹션 간격: `12.dp`
- 사진 영역: 카드 리스트와 구분해 **4:3** 권장 (상세에서 더 넓게 보기)
- 본문 스크롤 + 하단 CTA는 **고정(Sticky)**

---

## 3. UI 구성 요소

### 3.1 Top App Bar

| 요소 | MVP | Phase 2 |
| :--- | :---: | :---: |
| 뒤로가기 | ✅ | ✅ |
| 타이틀 | 품종명 (`kindName`) 1줄 ellipsis | 동일 |
| 찜(♡) | 🔲 | ✅ |
| 더보기(⋮) | 🔲 | 공유, 신고 등 |

- 타이틀에는 API `kindNm` 품종명 표시 (예: `믹스견`)
- `navigationIcon`: Material Icons Rounded `ArrowBack`

### 3.2 사진 갤러리

| 항목 | 스펙 |
| :--- | :--- |
| 데이터 | `imageUrls` (`popfile1`, `popfile2`) |
| UI | `HorizontalPager` + 페이지 인디케이터 |
| 이미지 없음 | 발바닥 placeholder (`ic_paw`) + "사진 없음" 캡션 |
| 로딩 | Coil + placeholder/error 동일 처리 (홈 카드와 공통) |
| 확대 | Phase 2: 탭 시 풀스크린 뷰어 |

홈 카드에 **2장 이상** 배지가 있을 때와 동일한 URL 목록을 그대로 사용합니다.

### 3.3 상태 칩

| API (`processState`) | UI | 색상 (DESIGN_GUIDE) |
| :--- | :--- | :--- |
| 공고·입양 관련 | 공고중 | Green `#4CAF50` |
| 보호중 | 보호중 | Blue `#2196F3` |
| 종료·반환·자연사 | 종료 | Grey `#9E9E9E` |
| 기타 | 원문 표시 | Grey |

홈 `ProcessStateType` 매핑 로직을 **공통 유틸**로 분리해 재사용합니다.

### 3.4 정보 섹션 (Key-Value)

MVP에서 표시할 필드 (현재 Domain `AbandonedAnimal` 기준):

| 라벨 | Domain 필드 | 빈 값 처리 |
| :--- | :--- | :--- |
| 품종 | `kindName` | "품종 미상" |
| 성별 | `sex` | "미상" (API: `M`/`F`/한글 등 원문 정규화) |
| 중성화 | `neuterYn` | "미상" (`Y`/`N`/한글 → 예/아니오) |
| 나이 | `age` | "-" |
| 체중 | `weight` | "-" |
| 색상 | `color` | "-" |

**표시 방식:** `DetailInfoRow(label, value)` Composable — 좌측 라벨 `bodySmall` secondary, 우측 값 `bodyLarge`.

### 3.5 특징 (`specialMark`)

- 섹션 타이틀: **특징**
- 여러 줄 전체 표시 (`bodyLarge`)
- 빈 값: "등록된 특징이 없습니다." (secondary 색)

### 3.6 발견 정보

| 라벨 | Domain 필드 |
| :--- | :--- |
| 발견 장소 | `happenPlace` |

Phase 2에서 API 확장 시 **발견일**(`happenDt`), **공고 기간**(`noticeSdt` ~ `noticeEdt`) 추가.

### 3.7 보호소 섹션

| 라벨 | Domain 필드 | MVP |
| :--- | :--- | :--- |
| 보호소명 | `shelterName` | ✅ |
| 전화번호 | `shelterTel` | ✅ (탭 시 dial) |
| 주소 | (미매핑) | Phase 2 — DTO `careAddr` Domain 확장 |

- 보호소명 Phase 2: 탭 → 보호소 탭 상세(또는 필터된 목록)
- 전화번호: `Intent.ACTION_DIAL` (`tel:` URI)

### 3.8 하단 CTA (Sticky Bottom Bar)

| 버튼 | MVP | Phase 2 |
| :--- | :---: | :---: |
| **보호소에 전화** (Primary, full-width 또는 60%) | ✅ | ✅ |
| **찜하기** (Outlined / Icon) | 🔲 | ✅ |

- 전화번호 없음: 버튼 disabled + "연락처 없음" 토스트
- 찜: Room 연동 후 filled/outlined 토글

### 3.9 메타 정보 (하단 캡션)

- **유기번호:** `desertionNo` — 공고 확인·문의 시 참조용
- `bodySmall`, secondary color, 섹션 최하단

---

## 4. 상태 UI

| 상태 | 화면 |
| :--- | :--- |
| **Loading** | 전체 화면 `CircularProgressIndicator` 또는 스켈레톤 (Phase 2) |
| **Success** | 위 와이어프레임 |
| **Error** | 메시지 + "다시 시도" 버튼 |
| **Not Found** | "해당 공고를 찾을 수 없습니다." + 뒤로가기 |

---

## 5. 데이터·아키텍처

### 5.1 현재 코드 현황

| 항목 | 상태 |
| :--- | :--- |
| Route / NavHost | ✅ `FindMeRoute.ANIMAL_DETAIL` |
| `AnimalDetailScreen` | 🔲 Placeholder (`desertionNo`만 표시) |
| Domain 상세 UseCase | 🔲 없음 |
| DTO → Domain 매핑 | ✅ `AbandonmentItemDto.toDomain()` (목록과 동일) |

### 5.2 상세 데이터 조회 전략 (결정 필요)

공공 API에는 **단건 전용 엔드포인트가 없고**, `abandonmentPublic_v2` 목록 응답에 상세 필드가 포함됩니다.

| 방안 | 설명 | 장점 | 단점 |
| :---: | :--- | :--- | :--- |
| **A. ID 재조회 (권장)** | `desertionNo`로 `abandonmentPublic_v2` 재호출 (`desertionNo` 쿼리 + 넓은 기간) | 딥링크·찜 탭 진입 가능, 최신 상태 | API 1회 추가 |
| **B. Nav 인자 전달** | 홈에서 `AbandonedAnimal` 직렬화 전달 | 즉시 표시, API 없음 | 찜·딥링크 불가, Nav payload 한계 |
| **C. 메모리 캐시** | Repository in-memory cache by `desertionNo` | 홈 진입 빠름 | 앱 재시작·다른 진입 경로 취약 |

**MVP 권장:** **A + C 하이브리드**

1. 홈에서 탭 시 캐시에 있으면 **즉시 표시**
2. 백그라운드에서 **A로 refresh** (상태 변경 반영)
3. 캐시 miss 시 로딩 UI 후 API 조회

> `desertion_no` 쿼리 파라미터는 공공데이터 명세에 포함됩니다. 구현 시 `GetAbandonedAnimalDetailUseCase(desertionNo)` 추가 및 `AbandonedAnimalFilter`에 `desertionNo` 필드 확장을 검토하세요.

### 5.3 Domain / Data 확장 (Phase 2)

현재 매핑되지 않았으나 상세 UX에 유용한 API 필드:

| API 필드 | UI 활용 |
| :--- | :--- |
| `happenDt` | 발견일 |
| `noticeSdt` / `noticeEdt` | 공고 기간, D-day |
| `careAddr` | 보호소 주소, 지도 |
| `orgNm` | 관할 기관 |
| `careRegNo` | 보호소 탭 연동 키 |

`AbandonedAnimal` Domain 모델 확장 또는 `AbandonedAnimalDetail` 별도 모델 검토.

### 5.4 레이어별 구현 위치 (예정)

| 레이어 | 경로 |
| :--- | :--- |
| Screen | `presentation/detail/AnimalDetailScreen.kt` |
| ViewModel | `presentation/detail/AnimalDetailViewModel.kt` |
| UiState | `presentation/detail/AnimalDetailUiState.kt` |
| UiModel | `presentation/detail/model/AnimalDetailUiModel.kt` |
| Components | `presentation/detail/component/` (갤러리, InfoSection, ShelterSection, BottomBar) |
| UseCase | `domain/usecase/GetAbandonedAnimalDetailUseCase.kt` |
| Repository | `AbandonedAnimalRepository.getByDesertionNo()` |

### 5.5 UiState 예시

```kotlin
data class AnimalDetailUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val animal: AnimalDetailUiModel? = null,
    val isFavorite: Boolean = false,        // Phase 2
    val errorMessage: String? = null,
    val isNotFound: Boolean = false,
)
```

---

## 6. 내비게이션·연동

| From | Action | To |
| :--- | :--- | :--- |
| 홈 카드 | `FindMeRoute.animalDetail(desertionNo)` | 동물 상세 |
| (Phase 2) 찜 카드 | 동일 | 동물 상세 |
| (Phase 2) 마이 > 최근 본 | 동일 + Room 기록 | 동물 상세 |
| 보호소 전화 | `ACTION_DIAL` | 시스템 전화 앱 |
| (Phase 2) 찜 | Room insert/delete | 스낵바 "찜 목록에 추가" |
| (Phase 2) 보호소명 | 보호소 탭 navigate | `shelter` (+ careRegNo) |

**최근 본 동물 (Phase 2):** 상세 진입 시 `desertionNo` + 타임스탬프 Room 저장 → 마이 탭 노출.

---

## 7. MVP vs 로드맵

### MVP (1차)

- [ ] `AnimalDetailViewModel` + API/캐시 기반 단건 로드
- [ ] Top App Bar (뒤로가기 + 품종 타이틀)
- [ ] 사진 pager (`popfile1` / `popfile2`)
- [ ] 상태 칩 + 기본 정보 Key-Value
- [ ] 특징 · 발견 장소 · 보호소명 · 전화번호
- [ ] 하단 **보호소에 전화** CTA
- [ ] Loading / Error / Not Found UI
- [ ] 유기번호 캡션

### Phase 2

- [ ] 찜하기 (Room + TopBar/BottomBar)
- [ ] Pull-to-refresh (상세 재조회)
- [ ] 보호소 주소 · 지도 · 길찾기
- [ ] 공고 기간 / D-day
- [ ] 사진 풀스크린 확대
- [ ] 최근 본 동물 기록
- [ ] 공유 (텍스트 + URL 스킴)

### Phase 3

- [ ] 입양·임시보호 신청서 바로가기 (마이 연동)
- [ ] 유사 동물 추천 (같은 보호소·품종)
- [ ] 공고 변경 알림 (찜한 동물 상태 변경)

---

## 8. 비기능 요구사항

| 항목 | 기준 |
| :--- | :--- |
| **성능** | 캐시 hit 시 0ms에 가깝게 표시, API refresh 3초 이내 |
| **이미지** | Coil disk cache, pager prefetch 1장 |
| **접근성** | 사진 CD: "{품종} 사진 {n}/{total}", CTA에 명확한 role |
| **전화** | `tel:` 실패 시 스낵바 안내 |
| **개인정보** | 발견 장소는 API 원문 그대로 — 과도한 가공 없음 |

---

## 9. 구현 순서 (권장)

1. **Domain/Data:** `getByDesertionNo`, Filter에 `desertionNo`, Repository 캐시
2. **Presentation Core:** ViewModel, UiState, UiModel, mapper (성별·중성화 표시 문자열)
3. **UI 조립:** Screen + 섹션 Composable
4. **CTA:** 전화 Intent 연동
5. **Phase 2:** 찜, 최근 본, 보호소 연동

---

## 10. 오픈 이슈 / 결정 필요

| # | 질문 | 제안 |
| :---: | :--- | :--- |
| 1 | 상세 데이터 소스 | **A+C 하이브리드** (§5.2) |
| 2 | 사진 비율 1:1 vs 4:3 | **4:3** — 상세에서 여유 있게 |
| 3 | `sexCd` / `neuterYn` 표시 | Presentation mapper에서 `M`→수컷, `F`→암컷, `Y`→예 등 정규화 |
| 4 | 종료 공고 상세 진입 | 홈에서 탭 가능하면 **회색 UI + 전화 disabled** |
| 5 | TopBar 찜 vs Bottom 찜 | Phase 2: **TopBar 아이콘 + Bottom 보조** 중 Bottom만으로 MVP 이후 통일 |
| 6 | Domain 필드 확장 시점 | MVP는 기존 12필드로 시작, Phase 2에서 `happenDt`·`careAddr` 추가 |

---

## 11. 현재 구현 현황

| 항목 | 상태 |
| :--- | :--- |
| Route 등록 | ✅ |
| `AnimalDetailScreen` | ✅ |
| ViewModel / UseCase | ✅ |
| UI 섹션·CTA | ✅ |
| 찜 / 최근 본 | 🔲 |

---

*이 문서는 동물 상세 화면 구현 시 기준 문서입니다. 변경 시 [PLANNING.md](./PLANNING.md) §6.3과 [HOME.md](./HOME.md) §6을 동기화하세요.*
