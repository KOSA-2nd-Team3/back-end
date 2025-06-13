package kosa.server.member.exception;

import kosa.server.common.code.ErrorCode;
import lombok.Getter;

/**
 * 토큰이 유효하지 않을 때 발생하는 예외
 */
@Getter
public class InvalidTokenException extends RuntimeException {

    private final ErrorCode errorCode;

    public InvalidTokenException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
