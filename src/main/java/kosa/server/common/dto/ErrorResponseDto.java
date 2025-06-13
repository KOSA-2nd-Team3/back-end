package kosa.server.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import kosa.server.common.code.ErrorCode;
import lombok.Getter;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String error;
    private final String code;
    private final String message;
    private final String path;
    private List<ValidationError> details; // 유효성 검사 에러 상세 정보

    private ErrorResponseDto(ErrorCode errorCode, String path) {
        this.status = errorCode.getStatus().value();
        this.error = errorCode.getStatus().name();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.path = path;
    }

    private ErrorResponseDto(ErrorCode errorCode, String path, List<ValidationError> details) {
        this.status = errorCode.getStatus().value();
        this.error = errorCode.getStatus().name();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.path = path;
        this.details = details;
    }

    public static ErrorResponseDto of(ErrorCode errorCode, String path) {
        return new ErrorResponseDto(errorCode, path);
    }

    public static ErrorResponseDto of(ErrorCode errorCode, String path, List<FieldError> fieldErrors) {
        List<ValidationError> list = fieldErrors.stream()
                .map(ValidationError::of)
                .toList();

        return new ErrorResponseDto(errorCode, path, list);
    }

    // 유효성 검사 에러를 담을 내부 레코드
    @Getter
    public static class ValidationError {
        private final String field;
        private final String message;

        private ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public static ValidationError of(FieldError fieldError) {
            return new ValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
    }
}
