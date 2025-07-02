package kosa.server.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common (Cxxx)
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "유효하지 않은 입력 값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "지원하지 않는 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "서버 내부 오류가 발생했습니다."),

    // Member (Mxxx)
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "해당 회원을 찾을 수 없습니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "M002", "이미 사용 중인 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "M003", "이미 사용 중인 이메일입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "M004", "비밀번호가 일치하지 않습니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "M005", "이미 사용 중인 닉네임입니다."),

    // Auth (Axxx)
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A001", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A002", "만료된 토큰입니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "A003", "인증에 실패했습니다. 유효한 자격 증명이 필요합니다."),
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "A004", "액세스 토큰이 필요합니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.UNAUTHORIZED, "A005", "이메일 인증이 필요합니다."),

    // Chat (CRxxx)
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CR001", "해당 채팅룸을 찾을 수 없습니다."),
    NOT_A_GROUP_CHAT(HttpStatus.BAD_REQUEST, "CR002", "그룹 채팅이 아닙니다."),
    NOT_MEMBER_OF_CHAT_ROOM(HttpStatus.BAD_REQUEST, "CR003", "본인이 속하지 않은 채팅방입니다."),

    // Post (Pxxx)
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "포스트를 찾을 수 없습니다."),
    PARTY_ALREADY_JOINED(HttpStatus.BAD_REQUEST, "P002", "이미 파티에 가입되어 있습니다."),
    SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "P003", "구독 정보가 없습니다."),
    PARTY_FULL(HttpStatus.BAD_REQUEST, "P004", "파티 정원이 가득 찼습니다."),

    // Platform (PFXXX)
    PLATFORM_NOT_FOUND(HttpStatus.NOT_FOUND, "PF001", "플랫폼을 찾을 수 없습니다."),

    // PartyMember(PMXXX)
    PARTY_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "PM001", "파티 멤버를 찾을 수 없습니다."),

    // Role (RXXX)
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "역할을 찾을 수 없습니다."),

    //ChatParticipant (CPXXX)
    CHAT_PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "CP001", "채팅방 참여자를 찾을 수 없습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
