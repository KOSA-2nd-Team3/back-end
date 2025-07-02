package kosa.server.chat.exception;

import kosa.server.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class NotMemberOfChatRoomException extends RuntimeException {
    private final ErrorCode errorCode;

    public NotMemberOfChatRoomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
