package kosa.server.member.exception;

import kosa.server.common.code.ErrorCode;
import lombok.Getter;

/**
 * 회원 가입 시 로그인 아이디가 중복될 경우 발생하는 예외
 */
@Getter
public class DuplicateLoginIdException extends RuntimeException {

    private final ErrorCode errorCode;

    public DuplicateLoginIdException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
