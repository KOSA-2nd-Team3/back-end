package kosa.server.chat.exception;

import kosa.server.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class NotGroupChatException extends RuntimeException{
    private final ErrorCode errorCode;

    public NotGroupChatException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
