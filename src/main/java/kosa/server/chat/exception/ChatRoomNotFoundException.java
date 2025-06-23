package kosa.server.chat.exception;

import kosa.server.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class ChatRoomNotFoundException extends RuntimeException {

    private final ErrorCode errorCode;

    public ChatRoomNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
