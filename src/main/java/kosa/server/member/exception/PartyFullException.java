package kosa.server.member.exception;

import kosa.server.common.code.ErrorCode;

/**
 * 정원 체크 시 파티가 다 찼을 때 발생하는 예외
 */
public class PartyFullException extends RuntimeException {
    private final ErrorCode errorCode;

    public PartyFullException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}