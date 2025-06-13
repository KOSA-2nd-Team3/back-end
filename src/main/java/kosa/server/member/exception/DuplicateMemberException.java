package kosa.server.member.exception;

import kosa.server.common.code.ErrorCode;
import lombok.Getter;

/**
 * 회원이 중복일 때 발생하는 예외
 */
@Getter
public class DuplicateMemberException extends RuntimeException {

    private final ErrorCode errorCode;

    public DuplicateMemberException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
