package kosa.server.board.exception;

import kosa.server.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class PlatformNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public PlatformNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
