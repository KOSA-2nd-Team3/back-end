package kosa.server.member.exception;

import kosa.server.common.code.ErrorCode;
import lombok.Getter;

/**
 * 요청의 쿠키에서 JWT 토큰을 찾을 수 없을 때 발생하는 예외
 */
@Getter
public class TokenNotFoundException extends RuntimeException {

    private final ErrorCode errorCode;

    public TokenNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
