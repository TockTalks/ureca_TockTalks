package com.tocktalks.domain.community.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.tocktalks.domain.community")
public class CommunityExceptionHandler {

    @ExceptionHandler(CommunityException.class)
    public ResponseEntity<ErrorResponse> handleCommunityException(CommunityException e){
        log.warn("CommunityException: {}", e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(new ErrorResponse(e.getErrorCode().name(), e.getMessage()));
    }

    public record ErrorResponse(String code, String message){}
}
