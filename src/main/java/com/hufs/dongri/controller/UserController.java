package com.hufs.dongri.controller;

import com.hufs.dongri.dto.user.UserDetailRequestDto;
import com.hufs.dongri.global.exception.ErrorResponse;
import com.hufs.dongri.global.response.ApiResult;
import com.hufs.dongri.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "5. User (학생 정보)", description = "학생 유저(@hufs.ac.kr) 관련 API")
@SecurityRequirement(name = "Authorization")
public class UserController {

    private final UserService userService;

    @PatchMapping("/me/details")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "학생 추가 정보(학번/학과) 입력",
            description = "OAuth2로 최초 가입한 학생(USER)이 자신의 학번과 학과를 입력합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 업데이트 성공",
                    content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "400", description = "이미 등록된 학번",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (USER가 아님)")
    })
    public ResponseEntity<ApiResult<Void>> updateUserDetails(@RequestBody UserDetailRequestDto dto) {
        userService.updateUserDetails(dto);
        return ResponseEntity.ok(ApiResult.success(HttpStatus.OK.value(), "추가 정보가 성공적으로 업데이트되었습니다."));
    }
}