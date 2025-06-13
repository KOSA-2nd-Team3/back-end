package kosa.server.common.advice;

import jakarta.servlet.http.HttpServletRequest;
import kosa.server.common.code.ErrorCode;
import kosa.server.common.dto.ErrorResponseDto;
import kosa.server.member.exception.DuplicateMemberException;
import kosa.server.member.exception.LoginFailedException;
import kosa.server.member.exception.MemberNotFoundException;
import kosa.server.member.exception.TokenNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenNotFoundException.class)
    protected ResponseEntity<ErrorResponseDto> handleTokenNotFoundException(TokenNotFoundException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();

        log.warn("[TokenNotFoundException] {} - Path: {}", errorCode.getMessage(), request.getRequestURI());

        ErrorResponseDto response = ErrorResponseDto.of(errorCode, request.getRequestURI());
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<ErrorResponseDto> loginFailedExceptionHandler(LoginFailedException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();

        log.warn("[LoginFailedException] {} - Path: {}", errorCode.getMessage(), request.getRequestURI());

        ErrorResponseDto response = ErrorResponseDto.of(errorCode, request.getRequestURI());
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    @ExceptionHandler(DuplicateMemberException.class)
    public ResponseEntity<ErrorResponseDto> duplicateMemberExceptionHandler(DuplicateMemberException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();

        log.warn("[DuplicateMemberException] {} - Path: {}", errorCode.getMessage(), request.getRequestURI());

        ErrorResponseDto response = ErrorResponseDto.of(errorCode, request.getRequestURI());
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> memberNotFoundExceptionHandler(MemberNotFoundException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();

        log.warn("[MemberNotFoundException] {} - Path: {}", errorCode.getMessage(), request.getRequestURI());

        ErrorResponseDto response = ErrorResponseDto.of(errorCode, request.getRequestURI());
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

        log.warn("[MethodArgumentNotValidException] {} - Path: {}", errorCode.getMessage(), request.getRequestURI());

        ErrorResponseDto response = ErrorResponseDto.of(errorCode, request.getRequestURI(), ex.getBindingResult().getFieldErrors());
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleInternalServerError(Exception ex, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        log.error("서버 오류 발생: {}", ex.getMessage(), ex);

        ErrorResponseDto response = ErrorResponseDto.of(errorCode, request.getRequestURI());
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

}
