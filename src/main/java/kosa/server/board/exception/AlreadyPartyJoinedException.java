package kosa.server.board.exception;

import kosa.server.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class AlreadyPartyJoinedException extends RuntimeException{
    private final ErrorCode errorCode;

    public AlreadyPartyJoinedException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
