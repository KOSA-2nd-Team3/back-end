package kosa.server.auth.exception;

import kosa.server.common.code.ErrorCode;
import lombok.Getter;

/**
 * 로그인 시 로그인 비밀번호가 다를 경우 발생하는 예외
 */
@Getter
public class PasswordMismatchException extends RuntimeException {

    private final ErrorCode errorCode;

    public PasswordMismatchException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
