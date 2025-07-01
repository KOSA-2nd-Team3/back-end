package kosa.server.member.exception;

import kosa.server.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class DuplicateNicknameException extends RuntimeException {

    private final ErrorCode errorCode;

    public DuplicateNicknameException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
