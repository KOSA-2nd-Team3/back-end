package kosa.server.chat.exception;

import kosa.server.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class ChatParticipantNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public ChatParticipantNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
