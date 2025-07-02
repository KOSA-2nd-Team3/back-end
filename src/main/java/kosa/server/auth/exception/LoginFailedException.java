package kosa.server.auth.exception;

import kosa.server.common.code.ErrorCode;
import lombok.Getter;

/**
 * 로그인 실패 했을 때 발생하는 예외
 */
@Getter
public class LoginFailedException extends RuntimeException {

  private final ErrorCode errorCode;

  public LoginFailedException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
