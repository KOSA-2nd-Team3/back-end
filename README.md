# OTTMOA 백엔드

OTT 구독료 함께 나누는 서비스 OTTMOA의 백엔드 API 서버입니다.

## 프로젝트 소개

Netflix, 유튜브 프리미엄, 스포티파이 등 각종 구독 서비스를 여러 명이 함께 사용해서 구독료를 절약할 수 있는 파티 매칭 서비스입니다.

프론트엔드: https://github.com/KOSA-2nd-Team3/front-end

## 주요 기능

- 파티 생성하고 참여하기
- 파티원들끼리 실시간 채팅
- 구글, 카카오, 네이버 소셜 로그인
- 이메일 인증 및 알림
- 파티 만료 자동 관리

## 기술 스택

- **백엔드**: Java 21, Spring Boot 3.4.6
- **데이터베이스**: PostgreSQL, Redis
- **인증**: JWT, OAuth2
- **실시간 채팅**: WebSocket + STOMP
- **배포**: AWS EC2, Docker

## 실행 방법

### 필요한 것들
- Java 21
- Docker
- PostgreSQL
- Redis

### 환경 변수 설정

`.env` 파일 만들고 아래 내용 추가:

```env
# 데이터베이스
username=db사용자명
dbpassword=db비밀번호

# OAuth2 설정
GOOGLE_CLIENT_ID=구글_클라이언트_ID
GOOGLE_CLIENT_SECRET=구글_클라이언트_시크릿
GOOGLE_REDIRECT_URI=http://localhost:8080/login/oauth2/code/google

KAKAO_CLIENT_ID=카카오_클라이언트_ID
KAKAO_CLIENT_SECRET=카카오_클라이언트_시크릿
KAKAO_REDIRECT_URI=http://localhost:8080/login/oauth2/code/kakao

NAVER_CLIENT_ID=네이버_클라이언트_ID
NAVER_CLIENT_SECRET=네이버_클라이언트_시크릿
NAVER_REDIRECT_URI=http://localhost:8080/login/oauth2/code/naver

# 메일 설정
MAIL_USERNAME=보내는이메일@gmail.com
MAIL_PASSWORD=앱비밀번호

# 사이트 주소
SITE_HOST=http://localhost:3000
```

### 실행하기

```bash
# 저장소 복사
git clone https://github.com/KOSA-2nd-Team3/back-end.git
cd back-end

# 데이터베이스 실행
docker-compose up -d

# 서버 실행
./gradlew bootRun
```

서버가 실행되면 `http://localhost:8080/swagger-ui.html`에서 API 문서를 볼 수 있습니다.

## 프로젝트 구조

```
src/main/java/kosa/server/
├── auth/          # 로그인, 회원가입
├── board/         # 파티 생성, 참여, 관리
├── chat/          # 실시간 채팅
├── member/        # 회원 정보 관리
├── mail/          # 이메일 발송
└── common/        # 공통 설정 (보안, 예외처리 등)
```

## 주요 API

### 인증 관련 (`/api/auth`)
- `POST /login` - 일반 로그인
- `POST /join` - 회원가입 및 인증 메일 발송
- `GET /login/verify` - 이메일 인증 확인
- `POST /social/exchange` - 소셜 로그인 토큰 교환
- `GET /status` - 인증 상태 확인
- `POST /logout` - 로그아웃
- `POST /token/reissue` - 액세스 토큰 재발급

### 파티 관련 (`/api/post`)
- `POST /create` - 파티 생성
- `POST /update` - 파티 수정
- `POST /join` - 파티 참여
- `DELETE /{postId}/out` - 파티 나가기
- `GET /send-mail/{postId}` - 파티 이메일 발송

### 플랫폼 관련 (`/api/platforms`)
- `GET /main` - 전체 플랫폼 조회
- `GET /category/{category}` - 카테고리별 플랫폼 조회
- `GET /{platformId}/post` - 플랫폼별 파티 목록
- `GET /{platformId}` - 플랫폼 상세 조회
- `GET /subscription` - 내 구독 플랫폼 조회
- `POST /subscription` - 구독 생성

### 대시보드 관련 (`/api/dashboard`)
- `GET /my-parties` - 내가 만든 파티 목록
- `GET /my-parties/{postId}` - 내 파티 상세 조회
- `GET /participated-parties` - 참여 중인 파티 목록
- `POST /my-parties/start` - 서비스 시작

### 채팅 관련 (`/api`)
- `POST /room/group/create` - 그룹 채팅방 생성
- `GET /chat/rooms` - 내 그룹 채팅방 목록
- `POST /room/group/{postId}/join` - 그룹 채팅방 참여
- `GET /chat/history/{roomId}` - 채팅방 메시지 조회
- `POST /room/{roomId}/read` - 메시지 읽음 처리
- `DELETE /chat-room/{roomId}` - 채팅방 삭제
- `DELETE /chat-room/{roomId}/leave` - 채팅방 나가기
- `GET /chat/room/{roomId}/members` - 채팅방 참여자 목록
- WebSocket `/connect` - 실시간 채팅

### 회원 관리 (`/api/member`)
- `GET /profile` - 회원 프로필 조회
- `POST /nickname` - 닉네임 사용 가능 여부 체크
- `PUT /nickname` - 닉네임 변경

## 지원하는 플랫폼

Netflix, Disney+, YouTube Premium, Spotify, Apple Music, Microsoft 365, Adobe Creative Cloud, Figma, Notion, ChatGPT Plus 등 총 28개 서비스

## 개발 정보

- **팀**: KOSA 3팀 (4명)
- **기간**: 3주
- **프론트엔드**: Vue3 + Pinia
- **백엔드**: Spring Boot + JPA
