package kosa.server.board.exception;

import kosa.server.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class PartyMemberNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public PartyMemberNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
