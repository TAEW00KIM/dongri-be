// ErrorResponse.java (신규 파일 - 실패 응답용 DTO)
package com.hufs.dongri.global.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final int status;
    private final String message;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}