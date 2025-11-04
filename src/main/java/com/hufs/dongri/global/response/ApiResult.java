// ApiResult.java (ApiResponse.java에서 이름 변경)
package com.hufs.dongri.global.response;

import lombok.Getter;

@Getter
// 클래스 이름을 ApiResult로 변경
public class ApiResult<T> {

    private final int status;
    private final String message;
    private final T data;

    // 생성자 이름 변경
    private ApiResult(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // 생성자 이름 변경
    private ApiResult(int status, String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }


    // 1. 성공 (데이터 포함)
    public static <T> ApiResult<T> success(int status, String message, T data) {
        return new ApiResult<>(status, message, data);
    }

    // 2. 성공 (데이터 없음)
    public static <T> ApiResult<T> success(int status, String message) {
        return new ApiResult<>(status, message);
    }
}