package kosa.server.auth.exception;

import kosa.server.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class RoleNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public RoleNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
