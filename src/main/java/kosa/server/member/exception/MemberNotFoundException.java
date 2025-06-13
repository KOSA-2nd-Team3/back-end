package kosa.server.member.exception;

import kosa.server.common.code.ErrorCode;
import lombok.Getter;

/**
 * 회원을 찾을 수 없을 때 발생하는 예외
 */
@Getter
public class MemberNotFoundException extends RuntimeException {

    private final ErrorCode errorCode;

    public MemberNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
