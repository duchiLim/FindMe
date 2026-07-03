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

---
*이 가이드는 프로젝트 진행에 따라 지속적으로 업데이트됩니다.*
