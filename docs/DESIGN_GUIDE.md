# FindMe 디자인 가이드라인

## 1. 디자인 컨셉
**"따뜻함, 신뢰, 연결"**
- 유기동물들에게 따뜻한 보금자리를 찾아주는 서비스인 만큼, 사용자에게 부드럽고 신뢰감을 줄 수 있는 톤앤매너를 유지합니다.

## 2. 컬러 시스템 (Color)

### 2.1 메인 컬러 (Primary)
- **Primary**: `#FFAB91` (Soft Warm Orange) - 따뜻함과 에너지를 상징
- **Primary Variant**: `#FF8A65` (강조·아이콘 포인트)
- **On Primary**: `#FFFFFF`

### 2.2 보조 컬러 (Secondary)
- **Secondary**: `#4DB6AC` (Soft Teal) - 안정감과 신뢰를 상징
- **On Secondary**: `#FFFFFF`

### 2.3 상태 컬러 (Status)
- **입양 가능**: `#4CAF50` (Green)
- **보호 중**: `#2196F3` (Blue)
- **공고 종료**: `#9E9E9E` (Grey)
- **긴급/안락사 임박**: `#F44336` (Red)

### 2.4 배경 및 텍스트
- **Background**: `#FAFAFA`
- **Surface**: `#FFFFFF`
- **Text Primary**: `#212121`
- **Text Secondary**: `#757575`

## 3. 타이포그래피 (Typography)
- **기본 폰트**: Pretendard 또는 Noto Sans KR (가독성 중시)
- **Head 1**: 24sp, Bold (화면 타이틀)
- **Head 2**: 20sp, Semi-Bold (섹션 타이틀)
- **Body 1**: 16sp, Normal (본문 텍스트)
- **Body 2**: 14sp, Normal (부연 설명, 캡션)

## 4. UI 컴포넌트 원칙

### 4.1 카드 (Cards)
- **Corner Radius**: `16.dp` (부드러운 느낌을 위해 큰 라운딩 사용)
- **Elevation**: `2.dp` ~ `4.dp` (과하지 않은 입체감)
- **내용**: 동물 사진이 강조되어야 하며, 하단에 이름/품종/상태 태그가 포함됨.

### 4.2 버튼 (Buttons)
- **Primary Button**: Full-width 또는 Fixed-width, `Corner Radius: 12.dp`
- **Text Button**: 보조 액션에 사용 (예: 더보기, 취소)

### 4.3 아이콘 (Icons)
- **스타일**: Rounded 스타일의 아이콘 사용 (Material Icons Rounded 권장)
- **의미 전달**: 동물 관련 커스텀 아이콘(발바닥 등)을 적절히 혼용.

## 5. 레이아웃 가이드
- **기본 마진 (Screen Margin)**: `16.dp`
- **컴포넌트 간 간격 (Gutter)**: `8.dp` 또는 `12.dp` (8배수 그리드 시스템 권장)

## 6. 스플래시 (Splash)

### 6.1 구성
스플래시는 **2단계**로 동작합니다.

| 단계 | 역할 | 표시 내용 |
|------|------|-----------|
| **시스템 스플래시** | 앱 cold start 직후 | Primary 배경색만 (아이콘 없음) |
| **애니메이션 스플래시** | Compose `AnimatedSplashScreen` | 로고 등장 애니메이션 |

시스템 스플래시에 런처 아이콘을 노출하지 않습니다. 아이콘이 미리 보이면 Compose 애니메이션(0%에서 등장)과 겹쳐 어색해지기 때문입니다.

### 6.2 비주얼

| 요소 | 스펙 |
|------|------|
| **배경** | Primary `#FFAB91` |
| **로고** | 흰색 하트 + Primary Variant `#FF8A65` 발바닥 (런처 아이콘과 동일) |
| **로고 크기** | `200.dp` (화면 중앙) |
| **에셋** | `presentation/.../drawable/ic_splash_logo.xml` |

### 6.3 애니메이션

**"안 보이는 상태 → 점점 커지며 나타남"**

| 속성 | 값 | Easing |
|------|-----|--------|
| **Scale** | 0% → 100% / 650ms | `LinearOutSlowIn` |
| **Opacity** | 0% → 100% / 350ms | `FastOutLinearIn` |
| **정지** | 500ms (최대 크기 유지) | — |

**총 재생 시간: 약 1.15초** (650ms + 500ms hold, opacity는 scale보다 먼저 완료)

구현: Compose `Animatable` + `graphicsLayer` (`presentation/.../splash/SplashScreen.kt`)

> **Lottie 사용 안 함** — 하트 path를 JSON으로 직접 작성하면 scale 애니메이션 중 path가 깨질 수 있습니다. 로고 변경 시 `ic_splash_logo.xml`(및 런처 foreground)을 함께 수정하세요.

### 6.4 관련 파일

| 파일 | 설명 |
|------|------|
| `app/.../themes.xml` | `Theme.App.Starting` — 시스템 스플래시 배경·투명 아이콘 |
| `app/.../drawable/ic_splash_empty.xml` | 시스템 스플래시용 투명 아이콘 |
| `app/.../drawable/ic_launcher_foreground.xml` | 런처·로고 마스터 (스플래시 로고와 동일 구성) |
| `presentation/.../splash/SplashScreen.kt` | 애니메이션 스플래시 Composable |
| `presentation/.../values/colors.xml` | `@color/primary`, `@color/primary_variant` |

### 6.5 타이밍 조정
`SplashScreen.kt` 상단 상수로 조정합니다.

- `GrowDurationMs` — 커지는 속도
- `FadeInDurationMs` — 나타나는 속도
- `HoldDurationMs` — 애니메이션 종료 후 정지 시간

---
*이 가이드는 프로젝트 진행에 따라 지속적으로 업데이트됩니다.*
