package kosa.server.member.exception;

import kosa.server.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidArgumentException extends IllegalArgumentException {
  private final ErrorCode errorCode;

  public InvalidArgumentException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
