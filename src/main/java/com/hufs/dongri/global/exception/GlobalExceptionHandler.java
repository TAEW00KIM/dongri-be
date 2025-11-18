package com.hufs.dongri.global.exception;

import com.hufs.dongri.global.exception.code.BaseErrorCode;
import com.hufs.dongri.global.exception.code.ErrorCode;
import com.hufs.dongri.global.response.CustomResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomResponse<Void>> handleCustomException(CustomException ex) {
        BaseErrorCode code = ex.getCode();
        log.warn("[CustomException]: {}", code.getMessage());
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(CustomResponse.onFailure(code));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BaseErrorCode code = ErrorCode.INVALID_REQUEST;
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("[InvalidRequest]: {}", message);

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(CustomResponse.onFailure(code, message));
    }

    @ExceptionHandler({org.springframework.security.authentication.DisabledException.class, org.springframework.security.access.AccessDeniedException.class})
    public ResponseEntity<CustomResponse<Void>> handleForbidden(RuntimeException e) {
        BaseErrorCode code = ErrorCode.FORBIDDEN;
        log.warn("[Forbidden]: {}", e.getMessage());
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(CustomResponse.onFailure(code, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse<Void>> handleAllException(Exception ex) {
        log.error("[Exception]: {}", ex.getMessage(), ex);
        BaseErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(CustomResponse.onFailure(code));
    }
}